package com.ims.inventory_management_system.services.impl;

import com.ims.inventory_management_system.dto.WarehouseDto;
import com.ims.inventory_management_system.entities.Warehouse;
import com.ims.inventory_management_system.exceptions.ResourceNotFoundException;
import com.ims.inventory_management_system.repositories.WarehouseRepository;
import com.ims.inventory_management_system.services.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Override
    @Transactional
    public WarehouseDto createWarehouse(WarehouseDto warehouseDto) {
        Warehouse warehouse = mapToEntity(warehouseDto);
        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        return mapToDto(savedWarehouse);
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseDto getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));
        return mapToDto(warehouse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseDto> getAllWarehouses() {
        return warehouseRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseDto> getActiveWarehouses() {
        return warehouseRepository.findByActive(true).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WarehouseDto updateWarehouse(Long id, WarehouseDto warehouseDto) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));
        
        // Update fields
        warehouse.setName(warehouseDto.getName());
        warehouse.setDescription(warehouseDto.getDescription());
        warehouse.setAddress(warehouseDto.getAddress());
        warehouse.setCity(warehouseDto.getCity());
        warehouse.setState(warehouseDto.getState());
        warehouse.setZipCode(warehouseDto.getZipCode());
        warehouse.setCountry(warehouseDto.getCountry());
        warehouse.setActive(warehouseDto.getActive());
        
        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);
        return mapToDto(updatedWarehouse);
    }

    @Override
    @Transactional
    public void deleteWarehouse(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));
        
        // Check if warehouse has inventory before deleting
        if (!warehouse.getInventories().isEmpty()) {
            // Instead of hard delete, set to inactive
            warehouse.setActive(false);
            warehouseRepository.save(warehouse);
        } else {
            warehouseRepository.delete(warehouse);
        }
    }
    
    // Helper methods for mapping between entity and DTO
    private Warehouse mapToEntity(WarehouseDto warehouseDto) {
        Warehouse warehouse = new Warehouse();
        warehouse.setName(warehouseDto.getName());
        warehouse.setDescription(warehouseDto.getDescription());
        warehouse.setAddress(warehouseDto.getAddress());
        warehouse.setCity(warehouseDto.getCity());
        warehouse.setState(warehouseDto.getState());
        warehouse.setZipCode(warehouseDto.getZipCode());
        warehouse.setCountry(warehouseDto.getCountry());
        warehouse.setActive(warehouseDto.getActive() != null ? warehouseDto.getActive() : true);
        return warehouse;
    }
    
    private WarehouseDto mapToDto(Warehouse warehouse) {
        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setId(warehouse.getId());
        warehouseDto.setName(warehouse.getName());
        warehouseDto.setDescription(warehouse.getDescription());
        warehouseDto.setAddress(warehouse.getAddress());
        warehouseDto.setCity(warehouse.getCity());
        warehouseDto.setState(warehouse.getState());
        warehouseDto.setZipCode(warehouse.getZipCode());
        warehouseDto.setCountry(warehouse.getCountry());
        warehouseDto.setActive(warehouse.getActive());
        
        // Set inventory item count
        if (warehouse.getInventories() != null) {
            warehouseDto.setInventoryItemCount(warehouse.getInventories().size());
        } else {
            warehouseDto.setInventoryItemCount(0);
        }
        
        return warehouseDto;
    }
}