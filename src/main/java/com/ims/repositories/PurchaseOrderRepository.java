package com.ims.repositories;

import com.ims.entities.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    Optional<PurchaseOrder> findByPoNumber(String poNumber);
    List<PurchaseOrder> findBySupplierId(Long supplierId);
    List<PurchaseOrder> findByStatus(PurchaseOrder.POStatus status);
    List<PurchaseOrder> findByOrderDateBetween(LocalDate startDate, LocalDate endDate);
    List<PurchaseOrder> findByExpectedDeliveryDateBefore(LocalDate date);
}