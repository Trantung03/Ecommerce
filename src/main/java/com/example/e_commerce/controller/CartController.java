package com.example.e_commerce.controller;

import com.example.e_commerce.dto.request.AddCartItemRequest;
import com.example.e_commerce.dto.response.ApiResponse;
import com.example.e_commerce.dto.response.CartResponse;
import com.example.e_commerce.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ApiResponse<CartResponse> getCart(HttpServletRequest request) {
        String sessionId =
                (String) request.getAttribute("X-Session-Id");
        return new ApiResponse<>(200, "Get cart successfully",
                cartService.getCart(sessionId));
    }


    @PostMapping("/addItems")
    public ApiResponse<CartResponse> addToCart(
            @RequestBody @Valid AddCartItemRequest requestBody,
            HttpServletRequest request
    ) {
        String sessionId =
                (String) request.getAttribute("X-Session-Id");
        return new ApiResponse<>(200, "Item added to cart",
                cartService.addItem(sessionId, requestBody));
    }


    @PutMapping("/updateItems")
    public ApiResponse<CartResponse> updateItemQuantity(
            @RequestBody @Valid AddCartItemRequest requestBody,
            HttpServletRequest request
    ) {
        String sessionId =
                (String) request.getAttribute("X-Session-Id");
        return new ApiResponse<>(200, "Item quantity updated",
                cartService.updateItem(sessionId, requestBody));
    }


    @DeleteMapping("/items/{itemId}")
    public ApiResponse<CartResponse> removeItem(
            @PathVariable Long itemId,
            HttpServletRequest request
    ) {
        String sessionId =
                (String) request.getAttribute("X-Session-Id");
        cartService.removeItem(sessionId, itemId);
        return new ApiResponse<>(200, "Item removed", cartService.getCart(sessionId));

    }

    @DeleteMapping()
    public  ApiResponse<CartResponse> clearCart(HttpServletRequest request) {
        String sessionId =
                (String) request.getAttribute("X-Session-Id");
        cartService.clearCart(sessionId);
        return new ApiResponse<>(200, "Item cleared", cartService.getCart(sessionId));
    }

}
