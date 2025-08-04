package com.ims.inventory_management_system.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "warehouses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warehouse extends BaseEntity {

    @NotBlank(message = "Warehouse name is required")
    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @NotBlank(message = "Address is required")
    @Column(nullable = false)
    private String address;

    private String city;
    private String state;
    private String zipCode;
    private String country;

    @Column(nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL)
    private List<Inventory> inventories = new ArrayList<>();
}