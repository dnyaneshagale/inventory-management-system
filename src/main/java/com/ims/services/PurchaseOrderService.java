package com.ims.services;

import com.ims.dto.PurchaseOrderDto;
import com.ims.dto.PurchaseOrderItemDto;
import com.ims.entities.PurchaseOrder;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseOrderService {
    PurchaseOrderDto createPurchaseOrder(PurchaseOrderDto purchaseOrderDto);
    PurchaseOrderDto getPurchaseOrderById(Long id);
    PurchaseOrderDto getPurchaseOrderByPoNumber(String poNumber);
    List<PurchaseOrderDto> getAllPurchaseOrders();
    List<PurchaseOrderDto> getPurchaseOrdersBySupplier(Long supplierId);
    List<PurchaseOrderDto> getPurchaseOrdersByStatus(PurchaseOrder.POStatus status);
    List<PurchaseOrderDto> getPurchaseOrdersByDateRange(LocalDate startDate, LocalDate endDate);
    List<PurchaseOrderDto> getOverduePurchaseOrders();
    PurchaseOrderDto updatePurchaseOrder(Long id, PurchaseOrderDto purchaseOrderDto);
    PurchaseOrderDto updatePurchaseOrderStatus(Long id, PurchaseOrder.POStatus status);
    void receivePurchaseOrder(Long id, List<PurchaseOrderItemDto> receivedItems);
    void deletePurchaseOrder(Long id);
    void generateAutomaticPurchaseOrders();
}