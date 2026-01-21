package com.example.e_commerce.service;

import com.example.e_commerce.Enum.ReservationStatus;
import com.example.e_commerce.entity.InventoryReservation;
import com.example.e_commerce.entity.Sku;
import com.example.e_commerce.exception.AppException;
import com.example.e_commerce.exception.ErrorCode;
import com.example.e_commerce.repository.InventoryReservationRepository;
import com.example.e_commerce.repository.SkuRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final SkuRepository skuRepository;
    private final InventoryReservationRepository reservationRepository;

    @Transactional
    public InventoryReservation reserveStock(Long skuId, int quantity, String sessionId) {

        Sku sku = skuRepository.findByIdWithLock(skuId)
                .orElseThrow(() -> new AppException(ErrorCode.SKU_NOT_FOUND));

        int reserved = reservationRepository.sumReservedQuantity(skuId);
        int available = sku.getStockTotal() - reserved;

        if (available < quantity) {
            throw new AppException(ErrorCode.OUT_OF_STOCK);
        }

        InventoryReservation reservation = new InventoryReservation();
        reservation.setSkuId(skuId);
        reservation.setQuantity(quantity);
        reservation.setSessionId(sessionId);
        reservation.setStatus(ReservationStatus.RESERVED);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(15));

       return reservationRepository.save(reservation);
    }

    @Transactional
    public void releaseStock(Long skuId, int quantity, String sessionId) {
        reservationRepository.cancelReservationsBySessionId(sessionId);
    }

    @Transactional
    public void confirmStock(String sessionId) {

        List<InventoryReservation> reservations = reservationRepository
                .findBySessionIdAndStatus(sessionId, ReservationStatus.RESERVED);

        for (InventoryReservation reservation : reservations) {
            Sku sku = skuRepository.findByIdWithLock(reservation.getSkuId())
                    .orElseThrow(() -> new RuntimeException("Variant not found"));

            sku.setStockAvailable(sku.getStockAvailable() - reservation.getQuantity());
            skuRepository.save(sku);

            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservationRepository.save(reservation);
        }

    }

    @Transactional
    public void cleanupExpiredReservations() {

        List<InventoryReservation> expired = reservationRepository
                .findExpiredReservations(LocalDateTime.now());

        for (InventoryReservation reservation : expired) {
            if (reservation.getStatus() == ReservationStatus.RESERVED && LocalDateTime.now().isAfter(reservation.getExpiresAt())) {
                reservation.setStatus(ReservationStatus.EXPIRED);
            }
            reservationRepository.save(reservation);
        }
    }
}
