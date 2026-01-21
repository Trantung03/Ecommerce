package com.example.e_commerce.service;

import com.example.e_commerce.dto.response.CartResponse;
import com.example.e_commerce.dto.request.AddCartItemRequest;
import org.springframework.stereotype.Service;

@Service
public interface CartService {
    CartResponse getCart(String sessionId);

    CartResponse addItem(String sessionId, AddCartItemRequest request);

    CartResponse updateItem(String sessionId, AddCartItemRequest request);

    void removeItem(String sessionId, Long cartItemId);

    void clearCart(String sessionId);
}
