package com.ims.inventory_management_system.config;

import com.ims.inventory_management_system.services.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {

    private final PurchaseOrderService purchaseOrderService;

    // Run every day at 2 AM
    @Scheduled(cron = "0 0 2 * * *")
    public void generateAutomaticPurchaseOrders() {
        purchaseOrderService.generateAutomaticPurchaseOrders();
    }
}