package com.example.e_commerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

    private Long skuId;
    private int quantity;
    private Long priceAtPurchase;
    private Long totalPrice;
}
