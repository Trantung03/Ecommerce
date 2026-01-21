package com.example.e_commerce.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutPrepareResponse {
    private String sessionId;
    private LocalDateTime expiresAt;
    private List<ReservedItem> items;
    private Long totalAmount;
    private String message;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReservedItem {
        private Long skuId;
        private String productName;
        private String skuDetails;
        private int quantity;
        private Long price;
        private Long subtotal;
    }
}