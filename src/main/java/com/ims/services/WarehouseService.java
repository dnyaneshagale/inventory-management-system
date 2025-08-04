package com.ims.services;

import com.ims.dto.WarehouseDto;

import java.util.List;

public interface WarehouseService {
    WarehouseDto createWarehouse(WarehouseDto warehouseDto);
    WarehouseDto getWarehouseById(Long id);
    List<WarehouseDto> getAllWarehouses();
    List<WarehouseDto> getActiveWarehouses();
    WarehouseDto updateWarehouse(Long id, WarehouseDto warehouseDto);
    void deleteWarehouse(Long id);
}