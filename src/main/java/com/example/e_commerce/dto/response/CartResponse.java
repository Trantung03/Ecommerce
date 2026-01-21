package com.example.e_commerce.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CartResponse {

    private Long cartId;
    private String sessionId;
    private List<CartItemResponse> items;
}
