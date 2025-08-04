package com.ims.inventory_management_system.services.impl;

import com.ims.inventory_management_system.dto.ProductDto;
import com.ims.inventory_management_system.entities.Category;
import com.ims.inventory_management_system.entities.Product;
import com.ims.inventory_management_system.entities.Supplier;
import com.ims.inventory_management_system.exceptions.ResourceNotFoundException;
import com.ims.inventory_management_system.repositories.CategoryRepository;
import com.ims.inventory_management_system.repositories.InventoryRepository;
import com.ims.inventory_management_system.repositories.ProductRepository;
import com.ims.inventory_management_system.repositories.SupplierRepository;
import com.ims.inventory_management_system.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        Product product = mapToEntity(productDto);
        Product savedProduct = productRepository.save(product);
        return mapToDto(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        ProductDto productDto = mapToDto(product);
        productDto.setCurrentStockLevel(getCurrentStockLevel(id));
        return productDto;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + sku));
        ProductDto productDto = mapToDto(product);
        productDto.setCurrentStockLevel(getCurrentStockLevel(product.getId()));
        return productDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getActiveProducts() {
        return productRepository.findByActive(true).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getProductsBelowMinStockLevel() {
        return productRepository.findProductsBelowMinStockLevel().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Update fields
        product.setSku(productDto.getSku());
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setCostPrice(productDto.getCostPrice());
        product.setSellingPrice(productDto.getSellingPrice());
        product.setMinStockLevel(productDto.getMinStockLevel());
        product.setTrackExpiryDate(productDto.getTrackExpiryDate());
        product.setTrackBatchNumber(productDto.getTrackBatchNumber());
        product.setActive(productDto.getActive());
        product.setImageUrl(productDto.getImageUrl());
        product.setBarcode(productDto.getBarcode());
        product.setDimensions(productDto.getDimensions());
        product.setWeight(productDto.getWeight());

        // Update category if provided
        if (productDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productDto.getCategoryId()));
            product.setCategory(category);
        }

        // Update default supplier if provided
        if (productDto.getDefaultSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(productDto.getDefaultSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + productDto.getDefaultSupplierId()));
            product.setDefaultSupplier(supplier);
        }

        Product updatedProduct = productRepository.save(product);
        return mapToDto(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getCurrentStockLevel(Long productId) {
        return inventoryRepository.getTotalQuantityByProductId(productId);
    }

    // Helper methods for mapping between entity and DTO
    private Product mapToEntity(ProductDto productDto) {
        Product product = new Product();
        product.setSku(productDto.getSku());
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setCostPrice(productDto.getCostPrice());
        product.setSellingPrice(productDto.getSellingPrice());
        product.setMinStockLevel(productDto.getMinStockLevel());
        product.setTrackExpiryDate(productDto.getTrackExpiryDate());
        product.setTrackBatchNumber(productDto.getTrackBatchNumber());
        product.setActive(productDto.getActive() != null ? productDto.getActive() : true);
        product.setImageUrl(productDto.getImageUrl());
        product.setBarcode(productDto.getBarcode());
        product.setDimensions(productDto.getDimensions());
        product.setWeight(productDto.getWeight());

        if (productDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productDto.getCategoryId()));
            product.setCategory(category);
        }

        if (productDto.getDefaultSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(productDto.getDefaultSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + productDto.getDefaultSupplierId()));
            product.setDefaultSupplier(supplier);
        }

        return product;
    }

    private ProductDto mapToDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setSku(product.getSku());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setCostPrice(product.getCostPrice());
        productDto.setSellingPrice(product.getSellingPrice());
        productDto.setMinStockLevel(product.getMinStockLevel());
        productDto.setTrackExpiryDate(product.getTrackExpiryDate());
        productDto.setTrackBatchNumber(product.getTrackBatchNumber());
        productDto.setActive(product.getActive());
        productDto.setImageUrl(product.getImageUrl());
        productDto.setBarcode(product.getBarcode());
        productDto.setDimensions(product.getDimensions());
        productDto.setWeight(product.getWeight());

        if (product.getCategory() != null) {
            productDto.setCategoryId(product.getCategory().getId());
            productDto.setCategoryName(product.getCategory().getName());
        }

        if (product.getDefaultSupplier() != null) {
            productDto.setDefaultSupplierId(product.getDefaultSupplier().getId());
            productDto.setDefaultSupplierName(product.getDefaultSupplier().getName());
        }

        return productDto;
    }
}