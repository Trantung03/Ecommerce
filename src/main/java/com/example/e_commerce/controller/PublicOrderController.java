package com.example.e_commerce.controller;

import com.example.e_commerce.dto.response.ApiResponse;
import com.example.e_commerce.dto.response.OrderResponse;
import com.example.e_commerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.hibernate.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/orders")
@RequiredArgsConstructor
@Slf4j
public class PublicOrderController {

    private final OrderService orderService;

    @GetMapping(value = "/{orderId}", produces = "application/json")
    public ApiResponse<OrderResponse> trackOrder(
            @PathVariable Long orderId,
            @RequestParam String email) throws BadRequestException {

        log.info("Public order tracking request: orderId={}, email={}", orderId, email);

        if (email == null || email.trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }

        OrderResponse order = orderService.getOrderById(orderId);

        if (!email.equalsIgnoreCase(order.getCustomerEmail())) {
            log.warn("Email mismatch for order tracking: orderId={}, provided={}", orderId, email);
            throw new RuntimeException();
        }

        log.info("Order tracked successfully: orderId={}", orderId);
        return ApiResponse.<OrderResponse>builder().result(order).build();
    }
}