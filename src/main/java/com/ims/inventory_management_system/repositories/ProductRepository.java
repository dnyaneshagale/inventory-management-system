package com.ims.inventory_management_system.repositories;

import com.ims.inventory_management_system.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByActive(Boolean active);

    @Query("SELECT p FROM Product p WHERE p.minStockLevel >= (SELECT COALESCE(SUM(i.quantity), 0) FROM Inventory i WHERE i.product = p)")
    List<Product> findProductsBelowMinStockLevel();
}