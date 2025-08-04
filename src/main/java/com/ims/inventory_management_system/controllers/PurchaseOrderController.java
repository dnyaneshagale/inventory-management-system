package com.ims.inventory_management_system.controllers;

import com.ims.inventory_management_system.dto.PurchaseOrderDto;
import com.ims.inventory_management_system.dto.PurchaseOrderItemDto;
import com.ims.inventory_management_system.entities.PurchaseOrder;
import com.ims.inventory_management_system.services.PurchaseOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @GetMapping
    public ResponseEntity<List<PurchaseOrderDto>> getAllPurchaseOrders() {
        return ResponseEntity.ok(purchaseOrderService.getAllPurchaseOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderDto> getPurchaseOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrderById(id));
    }

    @GetMapping("/number/{poNumber}")
    public ResponseEntity<PurchaseOrderDto> getPurchaseOrderByPoNumber(@PathVariable String poNumber) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrderByPoNumber(poNumber));
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<PurchaseOrderDto>> getPurchaseOrdersBySupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrdersBySupplier(supplierId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PurchaseOrderDto>> getPurchaseOrdersByStatus(
            @PathVariable PurchaseOrder.POStatus status) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrdersByStatus(status));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<PurchaseOrderDto>> getPurchaseOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrdersByDateRange(startDate, endDate));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<PurchaseOrderDto>> getOverduePurchaseOrders() {
        return ResponseEntity.ok(purchaseOrderService.getOverduePurchaseOrders());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<PurchaseOrderDto> createPurchaseOrder(@Valid @RequestBody PurchaseOrderDto purchaseOrderDto) {
        return new ResponseEntity<>(purchaseOrderService.createPurchaseOrder(purchaseOrderDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<PurchaseOrderDto> updatePurchaseOrder(
            @PathVariable Long id, 
            @Valid @RequestBody PurchaseOrderDto purchaseOrderDto) {
        return ResponseEntity.ok(purchaseOrderService.updatePurchaseOrder(id, purchaseOrderDto));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<PurchaseOrderDto> updatePurchaseOrderStatus(
            @PathVariable Long id,
            @RequestParam PurchaseOrder.POStatus status) {
        return ResponseEntity.ok(purchaseOrderService.updatePurchaseOrderStatus(id, status));
    }

    @PostMapping("/{id}/receive")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<Void> receivePurchaseOrder(
            @PathVariable Long id,
            @RequestBody List<PurchaseOrderItemDto> receivedItems) {
        purchaseOrderService.receivePurchaseOrder(id, receivedItems);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePurchaseOrder(@PathVariable Long id) {
        purchaseOrderService.deletePurchaseOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/generate-automatic")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> generateAutomaticPurchaseOrders() {
        purchaseOrderService.generateAutomaticPurchaseOrders();
        return ResponseEntity.ok().build();
    }
}