package com.example.e_commerce.service.impl;

import com.example.e_commerce.entity.Order;
import com.example.e_commerce.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "spring.mail.enabled", havingValue = "true", matchIfMissing = false)
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username:noreply@hunghypebeast.com}")
    private String fromEmail;

    @Value("${app.order.tracking.base-url:http://localhost:8080/api/v1/public/orders}")
    private String trackingBaseUrl;

    @Override
    public void sendOrderConfirmation(Order order) {
        try {
            String trackingLink = String.format("%s/%s?email=%s",
                    trackingBaseUrl, order.getId(), order.getCustomerEmail());

            Context context = new Context();
            context.setVariable("customerName", order.getCustomerName());
            context.setVariable("orderId", order.getId());
            context.setVariable("totalAmount", order.getTotalAmount());
            context.setVariable("trackingLink", trackingLink);
            context.setVariable("items", order.getItems());
            context.setVariable("shippingAddress", order.getShippingAddress());
            context.setVariable("paymentMethod", order.getPaymentMethod());

            String htmlContent = templateEngine.process("email/order-confirmation", context);

            sendEmail(
                    order.getCustomerEmail(),
                    "Xác nhận đơn hàng #" + order.getId(),
                    htmlContent
            );

            log.info("Order confirmation email sent to: {}", order.getCustomerEmail());
        } catch (Exception e) {
            log.error("Failed to send order confirmation email", e);
        }
    }

    @Override
    public void sendOrderStatusUpdate(Order order, String oldStatus, String newStatus) {
        try {
            String trackingLink = String.format("%s/%s?email=%s",
                    trackingBaseUrl, order.getId(), order.getCustomerEmail());

            Context context = new Context();
            context.setVariable("customerName", order.getCustomerName());
            context.setVariable("orderId", order.getId());
            context.setVariable("oldStatus", oldStatus);
            context.setVariable("newStatus", newStatus);
            context.setVariable("trackingLink", trackingLink);

            String htmlContent = templateEngine.process("email/order-status-update", context);

            sendEmail(
                    order.getCustomerEmail(),
                    "Cập nhật đơn hàng #" + order.getId(),
                    htmlContent
            );

            log.info("Order status update email sent to: {}", order.getCustomerEmail());
        } catch (Exception e) {
            log.error("Failed to send order status update email", e);
        }
    }

    private void sendEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
