package com.marketplace.salesapi.order.controller;

import com.marketplace.salesapi.order.dto.*;
import com.marketplace.salesapi.order.model.OrderStatus;
import com.marketplace.salesapi.order.service.OrderService;
import com.marketplace.salesapi.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Pedidos", description = "API para gerenciamento de pedidos")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    @Operation(summary = "Listar todos os pedidos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderDto>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<OrderDto> orders = orderService.getAllOrders(pageable);
        
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/my-orders")
    @Operation(summary = "Listar pedidos do usuário logado")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<Page<OrderDto>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Long userId = getCurrentUserId();
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<OrderDto> orders = orderService.getOrdersByUser(userId, pageable);
        
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/store/{storeId}")
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

    @GetMapping("/store/{storeId}/status/{status}")
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

    @GetMapping("/status/{status}")
    @Operation(summary = "Listar pedidos por status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderDto>> getOrdersByStatus(
            @PathVariable OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<OrderDto> orders = orderService.getOrdersByStatus(status, pageable);
        
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter pedido por ID")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        OrderDto order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Obter pedido por número")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<OrderDto> getOrderByNumber(@PathVariable String orderNumber) {
        OrderDto order = orderService.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(order);
    }

    @PostMapping
    @Operation(summary = "Criar um novo pedido")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Long userId = getCurrentUserId();
        OrderDto createdOrder = orderService.createOrder(request, userId);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status de um pedido")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        
        Long userId = getCurrentUserId();
        OrderDto updatedOrder = orderService.updateOrderStatus(id, request, userId);
        
        return ResponseEntity.ok(updatedOrder);
    }

    @PatchMapping("/{id}/payment")
    @Operation(summary = "Atualizar status de pagamento")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<OrderDto> updatePaymentStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePaymentStatusRequest request) {
        
        Long userId = getCurrentUserId();
        OrderDto updatedOrder = orderService.updatePaymentStatus(id, request, userId);
        
        return ResponseEntity.ok(updatedOrder);
    }

    @PatchMapping("/{id}/shipping")
    @Operation(summary = "Atualizar informações de envio")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<OrderDto> updateShippingInfo(
            @PathVariable Long id,
            @Valid @RequestBody UpdateShippingInfoRequest request) {
        
        Long userId = getCurrentUserId();
        OrderDto updatedOrder = orderService.updateShippingInfo(id, request, userId);
        
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}/cancel")
    @Operation(summary = "Cancelar um pedido")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        orderService.cancelOrder(id, userId);
        return ResponseEntity.noContent().build();
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null || 
            !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            // Return default user ID for development testing
            return 1L;
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
}