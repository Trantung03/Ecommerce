package com.example.e_commerce.service;

import com.example.e_commerce.entity.Order;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {

    void sendOrderConfirmation(Order order);

    void sendOrderStatusUpdate(Order order, String oldStatus, String newStatus);
}