package com.ims.services;

import com.ims.dto.ProductDto;

import java.util.List;

public interface ProductService {
    ProductDto createProduct(ProductDto productDto);
    ProductDto getProductById(Long id);
    ProductDto getProductBySku(String sku);
    List<ProductDto> getAllProducts();
    List<ProductDto> searchProducts(String keyword);
    List<ProductDto> getProductsByCategory(Long categoryId);
    List<ProductDto> getActiveProducts();
    List<ProductDto> getProductsBelowMinStockLevel();
    ProductDto updateProduct(Long id, ProductDto productDto);
    void deleteProduct(Long id);
    Integer getCurrentStockLevel(Long productId);
}