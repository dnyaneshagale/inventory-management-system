package com.ims.inventory_management_system.services;

import com.ims.inventory_management_system.dto.SupplierDto;

import java.util.List;

public interface SupplierService {
    SupplierDto createSupplier(SupplierDto supplierDto);
    SupplierDto getSupplierById(Long id);
    List<SupplierDto> getAllSuppliers();
    List<SupplierDto> searchSuppliers(String keyword);
    List<SupplierDto> getActiveSuppliers();
    SupplierDto updateSupplier(Long id, SupplierDto supplierDto);
    void deleteSupplier(Long id);
}