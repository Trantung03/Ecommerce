package com.example.e_commerce.entity;

import com.example.e_commerce.Enum.ReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_reservation",
        indexes = {
                @Index(name = "idx_reservation_sku", columnList = "skuId"),
                @Index(name = "idx_reservation_expires", columnList = "expiresAt")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long skuId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private String sessionId;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
    // RESERVED, CONFIRMED, EXPIRED, CANCELLED

    private LocalDateTime expiresAt;

    private LocalDateTime createdAt;
}

