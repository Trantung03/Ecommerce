package com.example.e_commerce.controller;

import com.example.e_commerce.dto.response.OrderResponse;
import com.example.e_commerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PublicOrderViewController {

    private final OrderService orderService;

    @GetMapping("/api/v1/public/orders/{orderId}")
    public String trackOrder(
            @PathVariable Long orderId,
            @RequestParam String email,
            Model model) throws BadRequestException {

        log.info("Public order tracking (HTML view): orderId={}, email={}", orderId, email);

        if (email == null || email.trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }

        OrderResponse order = orderService.getOrderById(orderId);

        if (!email.equalsIgnoreCase(order.getCustomerEmail())) {
            throw new RuntimeException();
        }

        model.addAttribute("order", order);
        log.info("Order tracking page rendered: orderId={}", orderId);

        return "order-tracking";
    }
}