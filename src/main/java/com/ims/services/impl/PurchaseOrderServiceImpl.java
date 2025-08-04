package com.ims.services.impl;

import com.ims.dto.PurchaseOrderDto;
import com.ims.dto.PurchaseOrderItemDto;
import com.ims.entities.*;
import com.ims.exceptions.ResourceNotFoundException;
import com.ims.repositories.*;
import com.ims.services.InventoryService;
import com.ims.services.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository; // Need to create this repository
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryService inventoryService;

    @Override
    @Transactional
    public PurchaseOrderDto createPurchaseOrder(PurchaseOrderDto purchaseOrderDto) {
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        
        // Generate PO Number
        purchaseOrder.setPoNumber(generatePoNumber());
        
        // Set supplier
        Supplier supplier = supplierRepository.findById(purchaseOrderDto.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + purchaseOrderDto.getSupplierId()));
        purchaseOrder.setSupplier(supplier);
        
        // Set dates
        purchaseOrder.setOrderDate(purchaseOrderDto.getOrderDate() != null ? 
                purchaseOrderDto.getOrderDate() : LocalDate.now());
        purchaseOrder.setExpectedDeliveryDate(purchaseOrderDto.getExpectedDeliveryDate());
        
        // Set status
        purchaseOrder.setStatus(purchaseOrderDto.getStatus() != null ? 
                purchaseOrderDto.getStatus() : PurchaseOrder.POStatus.DRAFT);
        
        // Set notes
        purchaseOrder.setNotes(purchaseOrderDto.getNotes());
        
        // Set created by
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOpt = userRepository.findByUsername(username);
        userOpt.ifPresent(purchaseOrder::setCreatedBy);
        
        // Save the purchase order first
        PurchaseOrder savedPO = purchaseOrderRepository.save(purchaseOrder);
        
        // Process items and calculate total
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<PurchaseOrderItem> items = new ArrayList<>();
        
        if (purchaseOrderDto.getItems() != null && !purchaseOrderDto.getItems().isEmpty()) {
            for (PurchaseOrderItemDto itemDto : purchaseOrderDto.getItems()) {
                PurchaseOrderItem item = new PurchaseOrderItem();
                item.setPurchaseOrder(savedPO);
                
                Product product = productRepository.findById(itemDto.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDto.getProductId()));
                item.setProduct(product);
                
                item.setQuantity(itemDto.getQuantity());
                item.setUnitPrice(itemDto.getUnitPrice());
                item.setReceivedQuantity(0);
                
                // Calculate total price for item
                BigDecimal itemTotal = itemDto.getUnitPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
                item.setTotalPrice(itemTotal);
                
                // Add to total amount
                totalAmount = totalAmount.add(itemTotal);
                
                // Add to items list
                items.add(item);
            }
            
            // Save all items
            purchaseOrderItemRepository.saveAll(items);
        }
        
        // Update total amount
        savedPO.setTotalAmount(totalAmount);
        savedPO.setItems(items);
        savedPO = purchaseOrderRepository.save(savedPO);
        
        return mapToDto(savedPO);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrderDto getPurchaseOrderById(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with id: " + id));
        return mapToDto(purchaseOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrderDto getPurchaseOrderByPoNumber(String poNumber) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findByPoNumber(poNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with PO number: " + poNumber));
        return mapToDto(purchaseOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderDto> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderDto> getPurchaseOrdersBySupplier(Long supplierId) {
        return purchaseOrderRepository.findBySupplierId(supplierId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderDto> getPurchaseOrdersByStatus(PurchaseOrder.POStatus status) {
        return purchaseOrderRepository.findByStatus(status).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderDto> getPurchaseOrdersByDateRange(LocalDate startDate, LocalDate endDate) {
        return purchaseOrderRepository.findByOrderDateBetween(startDate, endDate).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderDto> getOverduePurchaseOrders() {
        return purchaseOrderRepository.findByExpectedDeliveryDateBefore(LocalDate.now()).stream()
                .filter(po -> po.getStatus() != PurchaseOrder.POStatus.RECEIVED && 
                              po.getStatus() != PurchaseOrder.POStatus.CANCELLED)
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PurchaseOrderDto updatePurchaseOrder(Long id, PurchaseOrderDto purchaseOrderDto) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with id: " + id));
        
        // Only allow updates if PO is in DRAFT status
        if (purchaseOrder.getStatus() != PurchaseOrder.POStatus.DRAFT) {
            throw new IllegalStateException("Purchase order can only be updated in DRAFT status");
        }
        
        if (purchaseOrderDto.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(purchaseOrderDto.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + purchaseOrderDto.getSupplierId()));
            purchaseOrder.setSupplier(supplier);
        }
        
        if (purchaseOrderDto.getOrderDate() != null) {
            purchaseOrder.setOrderDate(purchaseOrderDto.getOrderDate());
        }
        
        if (purchaseOrderDto.getExpectedDeliveryDate() != null) {
            purchaseOrder.setExpectedDeliveryDate(purchaseOrderDto.getExpectedDeliveryDate());
        }
        
        if (purchaseOrderDto.getNotes() != null) {
            purchaseOrder.setNotes(purchaseOrderDto.getNotes());
        }
        
        // Update items if provided
        if (purchaseOrderDto.getItems() != null && !purchaseOrderDto.getItems().isEmpty()) {
            // Remove existing items
            purchaseOrderItemRepository.deleteAll(purchaseOrder.getItems());
            purchaseOrder.getItems().clear();
            
            // Add new items
            BigDecimal totalAmount = BigDecimal.ZERO;
            List<PurchaseOrderItem> items = new ArrayList<>();
            
            for (PurchaseOrderItemDto itemDto : purchaseOrderDto.getItems()) {
                PurchaseOrderItem item = new PurchaseOrderItem();
                item.setPurchaseOrder(purchaseOrder);
                
                Product product = productRepository.findById(itemDto.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDto.getProductId()));
                item.setProduct(product);
                
                item.setQuantity(itemDto.getQuantity());
                item.setUnitPrice(itemDto.getUnitPrice());
                item.setReceivedQuantity(0);
                
                // Calculate total price for item
                BigDecimal itemTotal = itemDto.getUnitPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
                item.setTotalPrice(itemTotal);
                
                // Add to total amount
                totalAmount = totalAmount.add(itemTotal);
                
                // Add to items list
                items.add(item);
            }
            
            // Save all items and update PO
            purchaseOrderItemRepository.saveAll(items);
            purchaseOrder.setItems(items);
            purchaseOrder.setTotalAmount(totalAmount);
        }
        
        return mapToDto(purchaseOrderRepository.save(purchaseOrder));
    }

    @Override
    @Transactional
    public PurchaseOrderDto updatePurchaseOrderStatus(Long id, PurchaseOrder.POStatus status) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with id: " + id));
        
        // Validate status transition
        validateStatusTransition(purchaseOrder.getStatus(), status);
        
        purchaseOrder.setStatus(status);
        
        // If status is SENT, update the expected delivery date based on supplier lead time if not already set
        if (status == PurchaseOrder.POStatus.SENT && purchaseOrder.getExpectedDeliveryDate() == null) {
            Supplier supplier = purchaseOrder.getSupplier();
            if (supplier.getLeadTimeInDays() != null) {
                purchaseOrder.setExpectedDeliveryDate(LocalDate.now().plusDays(supplier.getLeadTimeInDays()));
            }
        }
        
        return mapToDto(purchaseOrderRepository.save(purchaseOrder));
    }

    @Override
    @Transactional
    public void receivePurchaseOrder(Long id, List<PurchaseOrderItemDto> receivedItems) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with id: " + id));
        
        // Validate PO status
        if (purchaseOrder.getStatus() != PurchaseOrder.POStatus.SENT && 
            purchaseOrder.getStatus() != PurchaseOrder.POStatus.PARTIAL_RECEIVED) {
            throw new IllegalStateException("Purchase order must be in SENT or PARTIAL_RECEIVED status to receive items");
        }
        
        // Get the main warehouse (for simplicity, using first active warehouse - this should be configurable)
        Warehouse warehouse = warehouseRepository.findByActive(true)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No active warehouse found"));
        
        // Process received items
        boolean allItemsReceived = true;
        
        for (PurchaseOrderItemDto receivedItemDto : receivedItems) {
            // Find the corresponding PO item
            PurchaseOrderItem poItem = purchaseOrder.getItems().stream()
                    .filter(item -> item.getId().equals(receivedItemDto.getId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Purchase order item not found with id: " + receivedItemDto.getId()));
            
            // Update received quantity
            int newTotalReceived = poItem.getReceivedQuantity() + receivedItemDto.getReceivedQuantity();
            
            // Validate that we don't receive more than ordered
            if (newTotalReceived > poItem.getQuantity()) {
                throw new IllegalArgumentException("Cannot receive more items than ordered. Ordered: " + 
                        poItem.getQuantity() + ", Already received: " + poItem.getReceivedQuantity() + 
                        ", Trying to receive: " + receivedItemDto.getReceivedQuantity());
            }
            
            poItem.setReceivedQuantity(newTotalReceived);
            
            // Check if all items are fully received
            if (newTotalReceived < poItem.getQuantity()) {
                allItemsReceived = false;
            }
            
            // Add to inventory
            InventoryDto inventoryDto = new InventoryDto();
            inventoryDto.setProductId(poItem.getProduct().getId());
            inventoryDto.setWarehouseId(warehouse.getId());
            inventoryDto.setQuantity(receivedItemDto.getReceivedQuantity());
            inventoryDto.setBatchNumber(receivedItemDto.getBatchNumber());
            inventoryDto.setExpiryDate(receivedItemDto.getExpiryDate());
            
            inventoryService.addInventory(inventoryDto);
        }
        
        // Update PO status
        if (allItemsReceived) {
            purchaseOrder.setStatus(PurchaseOrder.POStatus.RECEIVED);
            purchaseOrder.setActualDeliveryDate(LocalDate.now());
        } else {
            purchaseOrder.setStatus(PurchaseOrder.POStatus.PARTIAL_RECEIVED);
        }
        
        purchaseOrderRepository.save(purchaseOrder);
    }

    @Override
    @Transactional
    public void deletePurchaseOrder(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with id: " + id));
        
        // Only allow deletion if PO is in DRAFT status
        if (purchaseOrder.getStatus() != PurchaseOrder.POStatus.DRAFT) {
            throw new IllegalStateException("Purchase order can only be deleted in DRAFT status");
        }
        
        purchaseOrderRepository.delete(purchaseOrder);
    }

    @Override
    @Transactional
    public void generateAutomaticPurchaseOrders() {
        // Get all products below minimum stock level
        List<Product> lowStockProducts = productRepository.findProductsBelowMinStockLevel();
        
        // Group by supplier
        Map<Supplier, List<Product>> productsBySupplier = new HashMap<>();
        
        for (Product product : lowStockProducts) {
            if (product.getDefaultSupplier() != null) {
                productsBySupplier.computeIfAbsent(product.getDefaultSupplier(), k -> new ArrayList<>()).add(product);
            }
        }
        
        // Create POs for each supplier
        for (Map.Entry<Supplier, List<Product>> entry : productsBySupplier.entrySet()) {
            Supplier supplier = entry.getKey();
            List<Product> products = entry.getValue();
            
            // Create PO
            PurchaseOrder purchaseOrder = new PurchaseOrder();
            purchaseOrder.setPoNumber(generatePoNumber());
            purchaseOrder.setSupplier(supplier);
            purchaseOrder.setOrderDate(LocalDate.now());
            
            // Set expected delivery date based on supplier lead time
            if (supplier.getLeadTimeInDays() != null) {
                purchaseOrder.setExpectedDeliveryDate(LocalDate.now().plusDays(supplier.getLeadTimeInDays()));
            }
            
            purchaseOrder.setStatus(PurchaseOrder.POStatus.DRAFT);
            purchaseOrder.setNotes("Auto-generated PO for low stock items");
            
            // Save PO to get ID
            purchaseOrder = purchaseOrderRepository.save(purchaseOrder);
            
            // Create PO items
            List<PurchaseOrderItem> items = new ArrayList<>();
            BigDecimal totalAmount = BigDecimal.ZERO;
            
            for (Product product : products) {
                PurchaseOrderItem item = new PurchaseOrderItem();
                item.setPurchaseOrder(purchaseOrder);
                item.setProduct(product);
                
                // Calculate quantity to order (min stock level - current stock)
                Integer currentStock = inventoryService.getTotalQuantityForProduct(product.getId());
                int quantityToOrder = product.getMinStockLevel() - (currentStock != null ? currentStock : 0);
                
                // Order at least 1
                quantityToOrder = Math.max(quantityToOrder, 1);
                
                item.setQuantity(quantityToOrder);
                item.setUnitPrice(product.getCostPrice());
                item.setReceivedQuantity(0);
                
                // Calculate total price
                BigDecimal itemTotal = product.getCostPrice().multiply(BigDecimal.valueOf(quantityToOrder));
                item.setTotalPrice(itemTotal);
                totalAmount = totalAmount.add(itemTotal);
                
                items.add(item);
            }
            
            // Save items
            purchaseOrderItemRepository.saveAll(items);
            
            // Update PO with total and items
            purchaseOrder.setItems(items);
            purchaseOrder.setTotalAmount(totalAmount);
            purchaseOrderRepository.save(purchaseOrder);
        }
    }

    // Helper methods
    private String generatePoNumber() {
        // Simple PO number generation: PO-YYYYMMDD-XXXX where XXXX is a random number
        LocalDate today = LocalDate.now();
        String datePart = String.format("%d%02d%02d", today.getYear(), today.getMonthValue(), today.getDayOfMonth());
        String randomPart = String.format("%04d", new Random().nextInt(10000));
        return "PO-" + datePart + "-" + randomPart;
    }
    
    private void validateStatusTransition(PurchaseOrder.POStatus currentStatus, PurchaseOrder.POStatus newStatus) {
        // Define valid status transitions
        switch (currentStatus) {
            case DRAFT:
                if (newStatus != PurchaseOrder.POStatus.SUBMITTED && newStatus != PurchaseOrder.POStatus.CANCELLED) {
                    throw new IllegalStateException("From DRAFT, PO can only move to SUBMITTED or CANCELLED");
                }
                break;
            case SUBMITTED:
                if (newStatus != PurchaseOrder.POStatus.APPROVED && newStatus != PurchaseOrder.POStatus.CANCELLED) {
                    throw new IllegalStateException("From SUBMITTED, PO can only move to APPROVED or CANCELLED");
                }
                break;
            case APPROVED:
                if (newStatus != PurchaseOrder.POStatus.SENT && newStatus != PurchaseOrder.POStatus.CANCELLED) {
                    throw new IllegalStateException("From APPROVED, PO can only move to SENT or CANCELLED");
                }
                break;
            case SENT:
                if (newStatus != PurchaseOrder.POStatus.PARTIAL_RECEIVED && newStatus != PurchaseOrder.POStatus.RECEIVED && newStatus != PurchaseOrder.POStatus.CANCELLED) {
                    throw new IllegalStateException("From SENT, PO can only move to PARTIAL_RECEIVED, RECEIVED or CANCELLED");
                }
                break;
            case PARTIAL_RECEIVED:
                if (newStatus != PurchaseOrder.POStatus.RECEIVED && newStatus != PurchaseOrder.POStatus.CANCELLED) {
                    throw new IllegalStateException("From PARTIAL_RECEIVED, PO can only move to RECEIVED or CANCELLED");
                }
                break;
            case RECEIVED:
                throw new IllegalStateException("RECEIVED is a final status and cannot be changed");
            case CANCELLED:
                throw new IllegalStateException("CANCELLED is a final status and cannot be changed");
        }
    }
    
    private PurchaseOrderDto mapToDto(PurchaseOrder po) {
        PurchaseOrderDto dto = new PurchaseOrderDto();
        dto.setId(po.getId());
        dto.setPoNumber(po.getPoNumber());
        dto.setSupplierId(po.getSupplier().getId());
        dto.setSupplierName(po.getSupplier().getName());
        dto.setOrderDate(po.getOrderDate());
        dto.setExpectedDeliveryDate(po.getExpectedDeliveryDate());
        dto.setActualDeliveryDate(po.getActualDeliveryDate());
        dto.setStatus(po.getStatus());
        dto.setTotalAmount(po.getTotalAmount());
        dto.setNotes(po.getNotes());
        
        if (po.getCreatedBy() != null) {
            dto.setCreatedByUsername(po.getCreatedBy().getUsername());
        }
        
        // Map items
        if (po.getItems() != null) {
            dto.setItems(po.getItems().stream()
                    .map(this::mapToItemDto)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    private PurchaseOrderItemDto mapToItemDto(PurchaseOrderItem item) {
        PurchaseOrderItemDto dto = new PurchaseOrderItemDto();
        dto.setId(item.getId());
        dto.setPurchaseOrderId(item.getPurchaseOrder().getId());
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setProductSku(item.getProduct().getSku());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setReceivedQuantity(item.getReceivedQuantity());
        dto.setTotalPrice(item.getTotalPrice());
        return dto;
    }
}