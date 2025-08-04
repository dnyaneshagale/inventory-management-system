package com.ims.inventory_management_system.services.impl;

import com.ims.inventory_management_system.dto.SupplierDto;
import com.ims.inventory_management_system.entities.Supplier;
import com.ims.inventory_management_system.exceptions.ResourceNotFoundException;
import com.ims.inventory_management_system.repositories.SupplierRepository;
import com.ims.inventory_management_system.services.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    @Transactional
    public SupplierDto createSupplier(SupplierDto supplierDto) {
        Supplier supplier = mapToEntity(supplierDto);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return mapToDto(savedSupplier);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierDto getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        return mapToDto(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierDto> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierDto> searchSuppliers(String keyword) {
        return supplierRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierDto> getActiveSuppliers() {
        return supplierRepository.findByActive(true).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SupplierDto updateSupplier(Long id, SupplierDto supplierDto) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        
        // Update fields
        supplier.setName(supplierDto.getName());
        supplier.setContactPerson(supplierDto.getContactPerson());
        supplier.setPhone(supplierDto.getPhone());
        supplier.setEmail(supplierDto.getEmail());
        supplier.setAddress(supplierDto.getAddress());
        supplier.setCity(supplierDto.getCity());
        supplier.setState(supplierDto.getState());
        supplier.setZipCode(supplierDto.getZipCode());
        supplier.setCountry(supplierDto.getCountry());
        supplier.setActive(supplierDto.getActive());
        supplier.setLeadTimeInDays(supplierDto.getLeadTimeInDays());
        
        Supplier updatedSupplier = supplierRepository.save(supplier);
        return mapToDto(updatedSupplier);
    }

    @Override
    @Transactional
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        
        // Check if supplier has products or purchase orders before deleting
        if (!supplier.getProducts().isEmpty() || !supplier.getPurchaseOrders().isEmpty()) {
            // Instead of hard delete, set to inactive
            supplier.setActive(false);
            supplierRepository.save(supplier);
        } else {
            supplierRepository.delete(supplier);
        }
    }
    
    // Helper methods for mapping between entity and DTO
    private Supplier mapToEntity(SupplierDto supplierDto) {
        Supplier supplier = new Supplier();
        supplier.setName(supplierDto.getName());
        supplier.setContactPerson(supplierDto.getContactPerson());
        supplier.setPhone(supplierDto.getPhone());
        supplier.setEmail(supplierDto.getEmail());
        supplier.setAddress(supplierDto.getAddress());
        supplier.setCity(supplierDto.getCity());
        supplier.setState(supplierDto.getState());
        supplier.setZipCode(supplierDto.getZipCode());
        supplier.setCountry(supplierDto.getCountry());
        supplier.setActive(supplierDto.getActive() != null ? supplierDto.getActive() : true);
        supplier.setLeadTimeInDays(supplierDto.getLeadTimeInDays());
        return supplier;
    }
    
    private SupplierDto mapToDto(Supplier supplier) {
        SupplierDto supplierDto = new SupplierDto();
        supplierDto.setId(supplier.getId());
        supplierDto.setName(supplier.getName());
        supplierDto.setContactPerson(supplier.getContactPerson());
        supplierDto.setPhone(supplier.getPhone());
        supplierDto.setEmail(supplier.getEmail());
        supplierDto.setAddress(supplier.getAddress());
        supplierDto.setCity(supplier.getCity());
        supplierDto.setState(supplier.getState());
        supplierDto.setZipCode(supplier.getZipCode());
        supplierDto.setCountry(supplier.getCountry());
        supplierDto.setActive(supplier.getActive());
        supplierDto.setLeadTimeInDays(supplier.getLeadTimeInDays());
        
        // Set product count
        if (supplier.getProducts() != null) {
            supplierDto.setProductCount(supplier.getProducts().size());
        } else {
            supplierDto.setProductCount(0);
        }
        
        return supplierDto;
    }
}