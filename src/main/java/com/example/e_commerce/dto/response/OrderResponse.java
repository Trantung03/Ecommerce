package com.example.e_commerce.dto.response;

import com.example.e_commerce.Enum.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String shippingAddress;
    private Long totalAmount;
    private OrderStatus status;
    private String paymentMethod;
    private String paymentStatus;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
}
