package com.marketplace.salesapi.store.controller;

import com.marketplace.salesapi.order.dto.OrderDto;
import com.marketplace.salesapi.order.dto.UpdateOrderStatusRequest;
import com.marketplace.salesapi.order.dto.UpdateShippingInfoRequest;
import com.marketplace.salesapi.order.model.OrderStatus;
import com.marketplace.salesapi.order.service.OrderService;
import com.marketplace.salesapi.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stores/{storeId}/orders")
@Tag(name = "Pedidos da Loja", description = "API para gerenciamento de pedidos por loja")
public class StoreOrdersController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    @Operation(summary = "Listar pedidos de uma loja")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<Page<OrderDto>> getStoreOrders(
            @PathVariable Long storeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<OrderDto> orders = orderService.getOrdersByStore(storeId, pageable);
        
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Listar pedidos de uma loja por status")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<Page<OrderDto>> getStoreOrdersByStatus(
            @PathVariable Long storeId,
            @PathVariable OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<OrderDto> orders = orderService.getOrdersByStoreAndStatus(storeId, status, pageable);
        
        return ResponseEntity.ok(orders);
    }
    
    @PutMapping("/{orderId}/status")
    @Operation(summary = "Atualizar status de um pedido da loja")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable Long storeId,
            @PathVariable Long orderId,
            @RequestBody UpdateOrderStatusRequest request) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Long userId = userDetails.getId();
        
        OrderDto updatedOrder = orderService.updateOrderStatus(orderId, request, userId);
        return ResponseEntity.ok(updatedOrder);
    }
    
    @PatchMapping("/{orderId}/shipping")
    @Operation(summary = "Atualizar informações de envio de um pedido da loja")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<OrderDto> updateShippingInfo(
            @PathVariable Long storeId,
            @PathVariable Long orderId,
            @RequestBody UpdateShippingInfoRequest request) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Long userId = userDetails.getId();
        
        OrderDto updatedOrder = orderService.updateShippingInfo(orderId, request, userId);
        return ResponseEntity.ok(updatedOrder);
    }
}