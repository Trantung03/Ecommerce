package com.example.e_commerce.scheduler;

import com.example.e_commerce.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryScheduler {

    private final InventoryService inventoryService;

    @Scheduled(fixedRate = 60000)
    public void cleanupExpiredReservations() {
        log.debug("Running scheduled cleanup of expired reservations");
        try {
            inventoryService.cleanupExpiredReservations();
        } catch (Exception e) {
            log.error("Error during reservation cleanup", e);
        }
    }
}