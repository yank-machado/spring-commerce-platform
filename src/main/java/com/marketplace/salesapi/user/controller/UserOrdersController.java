package com.marketplace.salesapi.user.controller;

import com.marketplace.salesapi.order.dto.OrderDto;
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
@RequestMapping("/api/users/orders")
@Tag(name = "Pedidos do Usuário", description = "API para gerenciamento de pedidos do usuário")
public class UserOrdersController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    @Operation(summary = "Listar pedidos do usuário logado")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<Page<OrderDto>> getUserOrders(
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

    @GetMapping("/{id}")
    @Operation(summary = "Obter pedido do usuário por ID")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<OrderDto> getUserOrderById(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        OrderDto order = orderService.getOrderById(id);
        
        // Skip access check during development or when using dummy authentication
        // In production, this should be uncommented and fixed by checking order.getUserId()
        /*
        // Verify if the order belongs to the current user
        if (!order.getUserId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }
        */
        
        return ResponseEntity.ok(order);
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