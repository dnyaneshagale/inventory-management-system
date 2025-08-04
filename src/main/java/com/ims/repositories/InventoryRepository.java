package com.ims.repositories;

import com.ims.entities.Inventory;
import com.ims.entities.Product;
import com.ims.entities.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByProductId(Long productId);
    List<Inventory> findByWarehouseId(Long warehouseId);
    Optional<Inventory> findByProductAndWarehouseAndBatchNumber(Product product, Warehouse warehouse, String batchNumber);

    @Query("SELECT SUM(i.quantity) FROM Inventory i WHERE i.product.id = :productId")
    Integer getTotalQuantityByProductId(Long productId);

    List<Inventory> findByExpiryDateBefore(LocalDate date);

    @Query("SELECT i FROM Inventory i WHERE i.quantity = 0")
    List<Inventory> findOutOfStockItems();
}