package com.example.e_commerce.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemResponse {

    private Long cartItemId;
    private Long skuId;
    private int quantity;
}
