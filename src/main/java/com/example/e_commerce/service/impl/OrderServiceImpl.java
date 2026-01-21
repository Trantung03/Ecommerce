package com.example.e_commerce.service.impl;

import com.example.e_commerce.Enum.OrderStatus;
import com.example.e_commerce.Enum.PaymentMethod;
import com.example.e_commerce.Enum.PaymentStatus;
import com.example.e_commerce.dto.request.CreateOrderRequest;
import com.example.e_commerce.dto.request.UpdateOrderStatusRequest;
import com.example.e_commerce.dto.response.OrderItemResponse;
import com.example.e_commerce.dto.response.OrderResponse;
import com.example.e_commerce.entity.*;
import com.example.e_commerce.exception.AppException;
import com.example.e_commerce.exception.ErrorCode;
import com.example.e_commerce.repository.CartRepository;
import com.example.e_commerce.repository.OrderRepository;
import com.example.e_commerce.service.EmailService;
import com.example.e_commerce.service.InventoryService;
import com.example.e_commerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final InventoryService inventoryService;
    private final EmailService emailService;

    @Override
    @Transactional
    public OrderResponse createOrder(String sesionId, CreateOrderRequest request) {

        Cart cart = cartRepository.findBySessionId(sesionId).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        if (cart.getItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        Order order = Order.builder()
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .customerPhone(request.getCustomerPhone())
                .shippingAddress(request.getShippingAddress())
                .status(OrderStatus.PENDING)
                .paymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()))
                .paymentStatus(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now()).build();

        Long totalAmount = 0L;

        for (CartItem cartItem : cart.getItems()) {
            Sku sku = cartItem.getSku();
            Long price = sku.getPrice();

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .skuId(sku.getId())
                    .price(price)
                    .quantity(cartItem.getQuantity())
                    .build();

            order.getItems().add(orderItem);
            totalAmount = totalAmount + price * cartItem.getQuantity();
        }

        order.setTotalAmount(totalAmount);

        inventoryService.confirmStock(sesionId);

        order = orderRepository.save(order);

        cart.getItems().clear();

        cartRepository.save(cart);

        try {
            emailService.sendOrderConfirmation(order);
        } catch (Exception e) {
            throw new AppException(ErrorCode.OUT_OF_STOCK);
        }

        return OrderResponse.builder()
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .customerPhone(order.getCustomerPhone())
                .shippingAddress(order.getShippingAddress())
                .totalAmount(totalAmount)
                .status(order.getStatus())
                .paymentMethod(String.valueOf(order.getPaymentMethod()))
                .paymentStatus(String.valueOf(order.getPaymentStatus()))
                .items(order.getItems().stream().map((a)->OrderItemResponse.builder().skuId(a.getSkuId())
                        .quantity(a.getQuantity())
                        .priceAtPurchase(a.getPrice())
                        .totalPrice(a.getPrice() * a.getQuantity()).build()).toList())
                .createdAt(order.getCreatedAt()).build();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("can not find order"));
        return OrderResponse.builder()
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .customerPhone(order.getCustomerPhone())
                .shippingAddress(order.getShippingAddress())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .paymentMethod(String.valueOf(order.getPaymentMethod()))
                .paymentStatus(String.valueOf(order.getPaymentStatus()))
                .items(order.getItems().stream().map((a)->OrderItemResponse.builder().skuId(a.getSkuId())
                        .quantity(a.getQuantity())
                        .priceAtPurchase(a.getPrice())
                        .totalPrice(a.getPrice() * a.getQuantity()).build()).toList())
                .createdAt(order.getCreatedAt()).build();
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) throws BadRequestException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("can not find order"));

        validateStatusTransition(order.getStatus(), request.getStatus());

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(request.getStatus());
        order = orderRepository.save(order);


        try {
            emailService.sendOrderStatusUpdate(order, oldStatus.name(), request.getStatus().name());
        } catch (Exception e) {
        }

        return OrderResponse.builder()
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .customerPhone(order.getCustomerPhone())
                .shippingAddress(order.getShippingAddress())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .paymentMethod(String.valueOf(order.getPaymentMethod()))
                .paymentStatus(String.valueOf(order.getPaymentStatus()))
                .items(order.getItems().stream().map((a)->OrderItemResponse.builder().skuId(a.getSkuId())
                        .quantity(a.getQuantity())
                        .priceAtPurchase(a.getPrice())
                        .totalPrice(a.getPrice() * a.getQuantity()).build()).toList())
                .createdAt(order.getCreatedAt()).build();
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus target) throws BadRequestException {
        boolean valid = switch (current) {
            case PENDING -> target == OrderStatus.CONFIRMED || target == OrderStatus.CANCELLED;
            case CONFIRMED -> target == OrderStatus.PROCESSING || target == OrderStatus.CANCELLED;
            case PROCESSING -> target == OrderStatus.SHIPPED || target == OrderStatus.CANCELLED;
            case SHIPPED -> target == OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };

        if (!valid) {
            throw new BadRequestException(
                    String.format("Invalid status transition: %s -> %s", current, target));
        }
    }
}
