package com.ims.inventory_management_system.controllers;

import com.ims.inventory_management_system.dto.InventoryDto;
import com.ims.inventory_management_system.services.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<InventoryDto>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryDto> getInventoryById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getInventoryById(id));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<InventoryDto>> getInventoryByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventoryByProduct(productId));
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<InventoryDto>> getInventoryByWarehouse(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(inventoryService.getInventoryByWarehouse(warehouseId));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryDto>> getLowStockInventory() {
        return ResponseEntity.ok(inventoryService.getLowStockInventory());
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<InventoryDto>> getExpiringInventory(@RequestParam(defaultValue = "30") int daysToExpiry) {
        return ResponseEntity.ok(inventoryService.getExpiringInventory(daysToExpiry));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<InventoryDto> addInventory(@Valid @RequestBody InventoryDto inventoryDto) {
        return new ResponseEntity<>(inventoryService.addInventory(inventoryDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<InventoryDto> updateInventory(@PathVariable Long id, @Valid @RequestBody InventoryDto inventoryDto) {
        return ResponseEntity.ok(inventoryService.updateInventory(id, inventoryDto));
    }

    @PostMapping("/{id}/adjust")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<Void> adjustInventory(
            @PathVariable Long id,
            @RequestParam Integer quantityChange,
            @RequestParam String reason) {
        inventoryService.adjustInventory(id, quantityChange, reason);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{sourceId}/transfer")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> transferInventory(
            @PathVariable Long sourceId,
            @RequestParam Long destinationWarehouseId,
            @RequestParam Integer quantity) {
        inventoryService.transferInventory(sourceId, destinationWarehouseId, quantity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }
}