package com.example.e_commerce.repository;

import com.example.e_commerce.Enum.ReservationStatus;
import com.example.e_commerce.entity.InventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryReservationRepository
        extends JpaRepository<InventoryReservation, Long> {

    @Query("""
        SELECT COALESCE(SUM(r.quantity), 0)
        FROM InventoryReservation r
        WHERE r.skuId = :skuId
          AND r.status = 'RESERVED'
          AND r.expiresAt > CURRENT_TIMESTAMP
    """)
    int sumReservedQuantity(@Param("skuId") Long skuId);

    List<InventoryReservation> findBySessionIdAndStatus(String sessionId, ReservationStatus status);

    long countBySessionIdAndStatus(String sessionId, ReservationStatus status);

    @Modifying
    @Query("UPDATE InventoryReservation ir SET ir.status = 'CANCELLED' " +
            "WHERE ir.sessionId = :sessionId AND ir.status = 'RESERVED'")
    void cancelReservationsBySessionId(String sessionId);

    @Query("SELECT ir FROM InventoryReservation ir " +
            "WHERE ir.expiresAt < :currentTime AND ir.status = 'RESERVED'")
    List<InventoryReservation> findExpiredReservations(@Param("currentTime") LocalDateTime currentTime);}
