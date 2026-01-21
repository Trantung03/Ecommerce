package com.example.e_commerce.controller;

import com.example.e_commerce.dto.request.CreateOrderRequest;
import com.example.e_commerce.dto.request.UpdateOrderStatusRequest;
import com.example.e_commerce.dto.response.ApiResponse;
import com.example.e_commerce.dto.response.OrderResponse;
import com.example.e_commerce.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping()
    public ApiResponse<OrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest createOrderRequest,HttpServletRequest request) {
        String sessionId =
                (String) request.getAttribute("X-Session-Id");
        OrderResponse o = orderService.createOrder(sessionId, createOrderRequest);
        return new ApiResponse<>(200, "ORDER_CONFIRMED", o);
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrderById(
            @PathVariable Long id) {
        return ApiResponse.<OrderResponse>builder().result(orderService.getOrderById(id)).build();
    }
    @PatchMapping("/admin/{id}/status")
    public ApiResponse<Object> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) throws BadRequestException {
        return ApiResponse.builder().result(orderService.updateOrderStatus(id, request)).build();
    }
}
