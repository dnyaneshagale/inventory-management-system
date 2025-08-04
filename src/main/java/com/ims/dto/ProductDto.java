package com.ims.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Long id;
    private String sku;
    private String name;
    private String description;
    private Long categoryId;
    private String categoryName;
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;
    private Integer minStockLevel;
    private Boolean trackExpiryDate;
    private Boolean trackBatchNumber;
    private Boolean active;
    private String imageUrl;
    private String barcode;
    private String dimensions;
    private Double weight;
    private Long defaultSupplierId;
    private String defaultSupplierName;
    private Integer currentStockLevel;
}