package com.ims.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @NotBlank(message = "Product SKU is required")
    @Column(unique = true, nullable = false)
    private String sku;

    @NotBlank(message = "Product name is required")
    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @NotNull(message = "Product cost price is required")
    @PositiveOrZero(message = "Cost price must be positive or zero")
    @Column(nullable = false)
    private BigDecimal costPrice;

    @NotNull(message = "Product selling price is required")
    @PositiveOrZero(message = "Selling price must be positive or zero")
    @Column(nullable = false)
    private BigDecimal sellingPrice;

    @NotNull(message = "Minimum stock level is required")
    @PositiveOrZero(message = "Minimum stock level must be positive or zero")
    @Column(nullable = false)
    private Integer minStockLevel;

    @Column(nullable = false)
    private Boolean trackExpiryDate = false;

    @Column(nullable = false)
    private Boolean trackBatchNumber = false;

    @Column(nullable = false)
    private Boolean active = true;

    private String imageUrl;

    private String barcode;

    private String dimensions;

    private Double weight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "default_supplier_id")
    private Supplier defaultSupplier;
}