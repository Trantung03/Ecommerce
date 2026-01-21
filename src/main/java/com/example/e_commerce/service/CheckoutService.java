package com.example.e_commerce.service;

import com.example.e_commerce.Enum.ReservationStatus;
import com.example.e_commerce.dto.response.CheckoutPrepareResponse;
import com.example.e_commerce.entity.*;
import com.example.e_commerce.exception.AppException;
import com.example.e_commerce.exception.ErrorCode;
import com.example.e_commerce.repository.CartRepository;
import com.example.e_commerce.repository.InventoryReservationRepository;
import com.example.e_commerce.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final InventoryReservationRepository inventoryReservationRepository;

    @Value("${inventory.reservation.expiry-minutes}")
    private int reservationExpiryMinutes;

    @Transactional
    public CheckoutPrepareResponse prepareCheckout(String sessionId) {

        Cart cart = cartRepository.findBySessionId(sessionId).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        InventoryReservation i = null;

        if (cart.getItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }
        List<CheckoutPrepareResponse.ReservedItem> reservedItems = new ArrayList<>();
        Long totalAmount = 0L;
        for (CartItem cartItem : cart.getItems()) {
            Sku sku = cartItem.getSku();
            i = inventoryService.reserveStock(sku.getId(), cartItem.getQuantity(), sessionId);
            CheckoutPrepareResponse.ReservedItem reservedItem = CheckoutPrepareResponse.ReservedItem.builder()
                    .skuId(sku.getId())
                    .productName(sku.getProduct().getName())
                    .skuDetails(String.format("Size: %s, Color: %s", sku.getSize(), sku.getColor()))
                    .quantity(cartItem.getQuantity())
                    .price(sku.getPrice())
                    .subtotal(sku.getPrice() * (cartItem.getQuantity()))
                    .build();
            reservedItems.add(reservedItem);
            totalAmount = totalAmount + reservedItem.getSubtotal();
        }

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(reservationExpiryMinutes);

        return CheckoutPrepareResponse.builder()
                .sessionId(sessionId)
                .expiresAt(expiresAt)
                .items(reservedItems)
                .totalAmount(totalAmount)
                .message(String.format("Stock reserved for %d minutes. Please complete your order.", reservationExpiryMinutes))
                .build();
    }

    public boolean verifyReservation(String reservationSessionId) {
        long activeReservations = inventoryReservationRepository
                .countBySessionIdAndStatus(reservationSessionId, ReservationStatus.RESERVED);

        boolean isValid = activeReservations > 0;

        return isValid;
    }
}

