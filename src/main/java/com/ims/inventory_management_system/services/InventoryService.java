package com.ims.inventory_management_system.services;

import com.ims.inventory_management_system.dto.InventoryDto;

import java.util.List;

public interface InventoryService {
    InventoryDto addInventory(InventoryDto inventoryDto);
    InventoryDto getInventoryById(Long id);
    List<InventoryDto> getAllInventory();
    List<InventoryDto> getInventoryByProduct(Long productId);
    List<InventoryDto> getInventoryByWarehouse(Long warehouseId);
    List<InventoryDto> getLowStockInventory();
    List<InventoryDto> getExpiringInventory(int daysToExpiry);
    InventoryDto updateInventory(Long id, InventoryDto inventoryDto);
    void adjustInventory(Long id, Integer quantityChange, String reason);
    void transferInventory(Long sourceInventoryId, Long destinationWarehouseId, Integer quantity);
    void deleteInventory(Long id);
    Integer getTotalQuantityForProduct(Long productId);
}