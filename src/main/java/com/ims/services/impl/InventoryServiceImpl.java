package com.ims.services.impl;

import com.ims.dto.InventoryDto;
import com.ims.entities.Inventory;
import com.ims.entities.Product;
import com.ims.entities.Warehouse;
import com.ims.exceptions.InsufficientInventoryException;
import com.ims.exceptions.ResourceNotFoundException;
import com.ims.repositories.InventoryRepository;
import com.ims.repositories.ProductRepository;
import com.ims.repositories.WarehouseRepository;
import com.ims.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    @Override
    @Transactional
    public InventoryDto addInventory(InventoryDto inventoryDto) {
        Product product = productRepository.findById(inventoryDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + inventoryDto.getProductId()));

        Warehouse warehouse = warehouseRepository.findById(inventoryDto.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + inventoryDto.getWarehouseId()));

        // Check if inventory already exists for this product, warehouse, and batch
        Optional<Inventory> existingInventory = inventoryRepository.findByProductAndWarehouseAndBatchNumber(
                product, warehouse, inventoryDto.getBatchNumber());

        if (existingInventory.isPresent()) {
            // Update existing inventory quantity
            Inventory inventory = existingInventory.get();
            inventory.setQuantity(inventory.getQuantity() + inventoryDto.getQuantity());
            if (inventoryDto.getExpiryDate() != null) {
                inventory.setExpiryDate(inventoryDto.getExpiryDate());
            }
            if (inventoryDto.getLocation() != null) {
                inventory.setLocation(inventoryDto.getLocation());
            }
            return mapToDto(inventoryRepository.save(inventory));
        } else {
            // Create new inventory
            Inventory inventory = new Inventory();
            inventory.setProduct(product);
            inventory.setWarehouse(warehouse);
            inventory.setQuantity(inventoryDto.getQuantity());
            inventory.setBatchNumber(inventoryDto.getBatchNumber());
            inventory.setExpiryDate(inventoryDto.getExpiryDate());
            inventory.setLocation(inventoryDto.getLocation());

            return mapToDto(inventoryRepository.save(inventory));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryDto getInventoryById(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));
        return mapToDto(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDto> getAllInventory() {
        return inventoryRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDto> getInventoryByProduct(Long productId) {
        return inventoryRepository.findByProductId(productId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDto> getInventoryByWarehouse(Long warehouseId) {
        return inventoryRepository.findByWarehouseId(warehouseId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDto> getLowStockInventory() {
        return inventoryRepository.findAll().stream()
                .filter(inventory -> {
                    Integer totalQuantity = inventoryRepository.getTotalQuantityByProductId(inventory.getProduct().getId());
                    return totalQuantity < inventory.getProduct().getMinStockLevel();
                })
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDto> getExpiringInventory(int daysToExpiry) {
        LocalDate expiryThreshold = LocalDate.now().plusDays(daysToExpiry);
        return inventoryRepository.findByExpiryDateBefore(expiryThreshold).stream()
                .filter(inventory -> inventory.getQuantity() > 0)
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InventoryDto updateInventory(Long id, InventoryDto inventoryDto) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));

        inventory.setQuantity(inventoryDto.getQuantity());
        if (inventoryDto.getBatchNumber() != null) {
            inventory.setBatchNumber(inventoryDto.getBatchNumber());
        }
        if (inventoryDto.getExpiryDate() != null) {
            inventory.setExpiryDate(inventoryDto.getExpiryDate());
        }
        if (inventoryDto.getLocation() != null) {
            inventory.setLocation(inventoryDto.getLocation());
        }

        return mapToDto(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional
    public void adjustInventory(Long id, Integer quantityChange, String reason) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));

        int newQuantity = inventory.getQuantity() + quantityChange;
        if (newQuantity < 0) {
            throw new InsufficientInventoryException("Cannot adjust inventory below zero. Current: " +
                    inventory.getQuantity() + ", Change: " + quantityChange);
        }

        inventory.setQuantity(newQuantity);
        inventoryRepository.save(inventory);

        // TODO: Log inventory adjustment with reason
    }

    @Override
    @Transactional
    public void transferInventory(Long sourceInventoryId, Long destinationWarehouseId, Integer quantity) {
        Inventory sourceInventory = inventoryRepository.findById(sourceInventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Source inventory not found with id: " + sourceInventoryId));

        if (sourceInventory.getQuantity() < quantity) {
            throw new InsufficientInventoryException("Insufficient inventory to transfer. Available: " +
                    sourceInventory.getQuantity() + ", Requested: " + quantity);
        }

        Warehouse destinationWarehouse = warehouseRepository.findById(destinationWarehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Destination warehouse not found with id: " + destinationWarehouseId));

        // Reduce source inventory
        sourceInventory.setQuantity(sourceInventory.getQuantity() - quantity);
        inventoryRepository.save(sourceInventory);

        // Check if destination inventory exists
        Optional<Inventory> destinationInventoryOpt = inventoryRepository.findByProductAndWarehouseAndBatchNumber(
                sourceInventory.getProduct(), destinationWarehouse, sourceInventory.getBatchNumber());

        if (destinationInventoryOpt.isPresent()) {
            // Add to existing inventory
            Inventory destinationInventory = destinationInventoryOpt.get();
            destinationInventory.setQuantity(destinationInventory.getQuantity() + quantity);
            inventoryRepository.save(destinationInventory);
        } else {
            // Create new inventory
            Inventory newInventory = new Inventory();
            newInventory.setProduct(sourceInventory.getProduct());
            newInventory.setWarehouse(destinationWarehouse);
            newInventory.setQuantity(quantity);
            newInventory.setBatchNumber(sourceInventory.getBatchNumber());
            newInventory.setExpiryDate(sourceInventory.getExpiryDate());
            inventoryRepository.save(newInventory);
        }

        // TODO: Log inventory transfer
    }

    @Override
    @Transactional
    public void deleteInventory(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));
        inventoryRepository.delete(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getTotalQuantityForProduct(Long productId) {
        return inventoryRepository.getTotalQuantityByProductId(productId);
    }

    // Helper methods for mapping between entity and DTO
    private InventoryDto mapToDto(Inventory inventory) {
        InventoryDto inventoryDto = new InventoryDto();
        inventoryDto.setId(inventory.getId());
        inventoryDto.setProductId(inventory.getProduct().getId());
        inventoryDto.setProductName(inventory.getProduct().getName());
        inventoryDto.setProductSku(inventory.getProduct().getSku());
        inventoryDto.setWarehouseId(inventory.getWarehouse().getId());
        inventoryDto.setWarehouseName(inventory.getWarehouse().getName());
        inventoryDto.setQuantity(inventory.getQuantity());
        inventoryDto.setBatchNumber(inventory.getBatchNumber());
        inventoryDto.setExpiryDate(inventory.getExpiryDate());
        inventoryDto.setLocation(inventory.getLocation());

        // Check if inventory is low
        Integer totalQuantity = inventoryRepository.getTotalQuantityByProductId(inventory.getProduct().getId());
        inventoryDto.setLowStock(totalQuantity < inventory.getProduct().getMinStockLevel());

        return inventoryDto;
    }
}