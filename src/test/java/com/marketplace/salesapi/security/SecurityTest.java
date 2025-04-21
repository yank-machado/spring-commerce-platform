package com.marketplace.salesapi.security;

import com.marketplace.salesapi.order.controller.OrderController;
import com.marketplace.salesapi.order.dto.OrderDto;
import com.marketplace.salesapi.order.model.OrderStatus;
import com.marketplace.salesapi.order.service.OrderService;
import com.marketplace.salesapi.user.model.ERole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.marketplace.salesapi.config.TestSecurityConfig;
import org.springframework.context.annotation.Import;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrderController.class, 
            properties = {"spring.main.allow-bean-definition-overriding=true"})
@Import(TestSecurityConfig.class)
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;
    
    // Mock DTO for testing
    static class MockOrderDto {
        private Long id;
        
        public MockOrderDto() {}
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }

    @Test
    @DisplayName("Anonymous users should be denied access to protected endpoints")
    @WithAnonymousUser
    void anonymousUser_ShouldBeDeniedAccess() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/orders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
                
        mockMvc.perform(get("/api/orders/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Customers should only access their own orders")
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void customer_ShouldAccessOnlyOwnOrders() throws Exception {
        // Arrange
        MockOrderDto customerOrder = new MockOrderDto();
        customerOrder.setId(1L);
        
        // Mock service to return order for ID 1
        when(orderService.getOrderById(1L)).thenReturn(new OrderDto());
        
        // Mock service to throw UnauthorizedException for ID 2
        when(orderService.getOrderById(2L))
                .thenThrow(new com.marketplace.salesapi.exception.UnauthorizedException("Access denied"));

        // Act & Assert
        // Customer can access their own order
        mockMvc.perform(get("/api/orders/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
                
        // Customer cannot access another user's order
        mockMvc.perform(get("/api/orders/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Admin should access all orders")
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void admin_ShouldAccessAllOrders() throws Exception {
        // Arrange
        MockOrderDto order = new MockOrderDto();
        order.setId(1L);
        
        when(orderService.getOrderById(anyLong())).thenReturn(new OrderDto());

        // Act & Assert
        // Admin can access any order
        mockMvc.perform(get("/api/orders/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
                
        mockMvc.perform(get("/api/orders/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Sellers should access orders for their store")
    @WithMockUser(username = "seller@example.com", roles = "SELLER")
    void seller_ShouldAccessOrdersForTheirStore() throws Exception {
        // Arrange
        MockOrderDto order = new MockOrderDto();
        order.setId(1L);
        
        when(orderService.getOrderById(1L)).thenReturn(new OrderDto());
        when(orderService.getOrderById(2L))
                .thenThrow(new com.marketplace.salesapi.exception.UnauthorizedException("Access denied"));

        // Act & Assert
        // Seller can access orders for their store
        mockMvc.perform(get("/api/orders/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
                
        // Seller cannot access orders for other stores
        mockMvc.perform(get("/api/orders/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
} 