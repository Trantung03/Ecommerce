package com.example.e_commerce.controller;

import com.example.e_commerce.dto.response.ApiResponse;
import com.example.e_commerce.dto.response.CheckoutPrepareResponse;
import com.example.e_commerce.service.CheckoutService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping
    public ApiResponse<CheckoutPrepareResponse> prepareCheckout(
            HttpServletRequest request) {
        String sessionId =
                (String) request.getAttribute("X-Session-Id");

        CheckoutPrepareResponse response = checkoutService.prepareCheckout( sessionId);
        return ApiResponse.<CheckoutPrepareResponse>builder().result(response).build();
    }

    @GetMapping("/verify/{sessionId}")
    public ApiResponse<Boolean> verifyReservation(@PathVariable String sessionId) {
        boolean isValid = checkoutService.verifyReservation(sessionId);
        return ApiResponse.<Boolean>builder().result(isValid).build();
    }

}
