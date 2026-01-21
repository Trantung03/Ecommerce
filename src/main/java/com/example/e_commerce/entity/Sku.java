package com.example.e_commerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sku")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sku {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private String size;
    private String color;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private int stockTotal;

    @Column(nullable = false)
    private int stockAvailable;

    private LocalDateTime createdAt;

}