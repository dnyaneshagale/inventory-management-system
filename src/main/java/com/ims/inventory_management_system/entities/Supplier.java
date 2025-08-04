package com.ims.inventory_management_system.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier extends BaseEntity {

    @NotBlank(message = "Supplier name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Contact person is required")
    @Column(nullable = false)
    private String contactPerson;

    @NotBlank(message = "Phone number is required")
    @Column(nullable = false)
    private String phone;

    @Email(message = "Email should be valid")
    private String email;

    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    @Column(nullable = false)
    private Boolean active = true;

    private Integer leadTimeInDays;

    @OneToMany(mappedBy = "supplier")
    private List<PurchaseOrder> purchaseOrders = new ArrayList<>();

    @OneToMany(mappedBy = "defaultSupplier")
    private List<Product> products = new ArrayList<>();
}