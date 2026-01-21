package com.example.e_commerce.service;

import com.example.e_commerce.dto.request.CreateOrderRequest;
import com.example.e_commerce.dto.request.UpdateOrderStatusRequest;
import com.example.e_commerce.dto.response.OrderResponse;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

@Service
public interface OrderService {

    OrderResponse createOrder(String sessionId, @Valid CreateOrderRequest createOrderRequest);

    OrderResponse getOrderById(Long orderId);

    OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) throws BadRequestException;
}
