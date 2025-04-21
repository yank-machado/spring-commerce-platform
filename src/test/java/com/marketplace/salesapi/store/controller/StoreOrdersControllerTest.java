package com.marketplace.salesapi.store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.salesapi.config.TestSecurityConfig;
import com.marketplace.salesapi.order.dto.OrderDto;
import com.marketplace.salesapi.order.model.OrderStatus;
import com.marketplace.salesapi.order.service.OrderService;
import com.marketplace.salesapi.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StoreOrdersController.class, 
            properties = {"spring.main.allow-bean-definition-overriding=true"})
@Import(TestSecurityConfig.class)
class StoreOrdersControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private MockUserDetails sellerUserDetails;
    private MockUserDetails adminUserDetails;
    private MockUserDetails customerUserDetails;
    private List<OrderDto> orderDtoList;

    // Mock UserDetails implementation
    static class MockUserDetails implements UserDetails {
        private Long id;
        private String username;
        private String password;
        private Collection<SimpleGrantedAuthority> authorities;
        
        public MockUserDetails(Long id, String username, String password, List<SimpleGrantedAuthority> authorities) {
            this.id = id;
            this.username = username;
            this.password = password;
            this.authorities = authorities;
        }
        
        public Long getId() { return id; }
        
        @Override
        public Collection<SimpleGrantedAuthority> getAuthorities() { return authorities; }
        
        @Override
        public String getPassword() { return password; }
        
        @Override
        public String getUsername() { return username; }
        
        @Override
        public boolean isAccountNonExpired() { return true; }
        
        @Override
        public boolean isAccountNonLocked() { return true; }
        
        @Override
        public boolean isCredentialsNonExpired() { return true; }
        
        @Override
        public boolean isEnabled() { return true; }
    }

    @BeforeEach
    void setUp() {
        // Configurar usuários de teste
        List<SimpleGrantedAuthority> sellerAuthorities = new ArrayList<>();
        sellerAuthorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        sellerUserDetails = new MockUserDetails(
                1L,
                "seller@example.com",
                "password",
                sellerAuthorities
        );

        List<SimpleGrantedAuthority> adminAuthorities = new ArrayList<>();
        adminAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        adminUserDetails = new MockUserDetails(
                2L,
                "admin@example.com",
                "password",
                adminAuthorities
        );

        List<SimpleGrantedAuthority> customerAuthorities = new ArrayList<>();
        customerAuthorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        customerUserDetails = new MockUserDetails(
                3L,
                "customer@example.com",
                "password",
                customerAuthorities
        );

        // Configurar dados de pedidos para testes
        orderDtoList = new ArrayList<>();
        // Criar duas instâncias de OrderDto simples para os testes
        orderDtoList.add(new OrderDto());
        orderDtoList.add(new OrderDto());
    }

    @Test
    @DisplayName("GET /api/stores/{storeId}/orders should return store orders when seller is authorized")
    void getStoreOrders_ShouldReturnOrders_WhenSellerIsAuthorized() throws Exception {
        // Arrange
        Page<OrderDto> orderPage = new PageImpl<>(orderDtoList);
        when(orderService.getOrdersByStore(anyLong(), any(Pageable.class))).thenReturn(orderPage);

        // Act & Assert
        mockMvc.perform(get("/api/stores/1/orders")
                .with(user(sellerUserDetails))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("GET /api/stores/{storeId}/orders should return store orders when admin is authorized")
    void getStoreOrders_ShouldReturnOrders_WhenAdminIsAuthorized() throws Exception {
        // Arrange
        Page<OrderDto> orderPage = new PageImpl<>(orderDtoList);
        when(orderService.getOrdersByStore(anyLong(), any(Pageable.class))).thenReturn(orderPage);

        // Act & Assert
        mockMvc.perform(get("/api/stores/1/orders")
                .with(user(adminUserDetails))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("GET /api/stores/{storeId}/orders should return 403 when customer tries to access")
    void getStoreOrders_ShouldReturn403_WhenCustomerTriesToAccess() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/stores/1/orders")
                .with(user(customerUserDetails))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/stores/{storeId}/orders/status/{status} should return orders by status when seller is authorized")
    void getStoreOrdersByStatus_ShouldReturnOrders_WhenSellerIsAuthorized() throws Exception {
        // Arrange
        Page<OrderDto> orderPage = new PageImpl<>(orderDtoList);
        when(orderService.getOrdersByStoreAndStatus(anyLong(), any(OrderStatus.class), any(Pageable.class))).thenReturn(orderPage);

        // Act & Assert
        mockMvc.perform(get("/api/stores/1/orders/status/PENDING")
                .with(user(sellerUserDetails))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("GET /api/stores/{storeId}/orders/status/{status} should return orders by status when admin is authorized")
    void getStoreOrdersByStatus_ShouldReturnOrders_WhenAdminIsAuthorized() throws Exception {
        // Arrange
        Page<OrderDto> orderPage = new PageImpl<>(orderDtoList);
        when(orderService.getOrdersByStoreAndStatus(anyLong(), any(OrderStatus.class), any(Pageable.class))).thenReturn(orderPage);

        // Act & Assert
        mockMvc.perform(get("/api/stores/1/orders/status/PENDING")
                .with(user(adminUserDetails))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("GET /api/stores/{storeId}/orders/status/{status} should return 403 when customer tries to access")
    void getStoreOrdersByStatus_ShouldReturn403_WhenCustomerTriesToAccess() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/stores/1/orders/status/DELIVERED")
                .with(user(customerUserDetails))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SELLER")
    @DisplayName("GET /api/stores/{storeId}/orders should return store orders with WithMockUser annotation")
    void getStoreOrders_ShouldReturnOrders_WithMockUserAnnotation() throws Exception {
        // Arrange
        Page<OrderDto> orderPage = new PageImpl<>(orderDtoList);
        when(orderService.getOrdersByStore(anyLong(), any(Pageable.class))).thenReturn(orderPage);

        // Act & Assert
        mockMvc.perform(get("/api/stores/1/orders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(roles = "SELLER")
    @DisplayName("GET /api/stores/{storeId}/orders should handle pagination parameters")
    void getStoreOrders_ShouldHandlePaginationParameters() throws Exception {
        // Arrange
        Page<OrderDto> orderPage = new PageImpl<>(orderDtoList);
        when(orderService.getOrdersByStore(anyLong(), any(Pageable.class))).thenReturn(orderPage);

        // Act & Assert
        mockMvc.perform(get("/api/stores/1/orders")
                .param("page", "1")
                .param("size", "5")
                .param("sortBy", "orderNumber")
                .param("direction", "asc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
    
    @Test
    @DisplayName("PUT /api/stores/{storeId}/orders/{orderId}/status should update status with CSRF token")
    @WithMockUser(roles = "SELLER")
    void updateOrderStatus_ShouldUpdateStatus_WhenCsrfTokenIsPresent() throws Exception {
        // Arrange
        when(orderService.updateOrderStatus(anyLong(), any(), anyLong())).thenReturn(new OrderDto());
        
        // Mock request body
        String requestBody = "{\"status\":\"PROCESSING\",\"notes\":\"Order is being processed\"}";
        
        // Act & Assert
        mockMvc.perform(put("/api/stores/1/orders/1/status")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
        
        verify(orderService).updateOrderStatus(eq(1L), any(), anyLong());
    }
    
    @Test
    @DisplayName("PUT /api/stores/{storeId}/orders/{orderId}/status should return 403 when CSRF token is missing")
    @WithMockUser(roles = "SELLER")
    void updateOrderStatus_ShouldReturn403_WhenCsrfTokenIsMissing() throws Exception {
        // Mock request body
        String requestBody = "{\"status\":\"PROCESSING\",\"notes\":\"Order is being processed\"}";
        
        // Act & Assert - Note the missing CSRF token
        mockMvc.perform(put("/api/stores/1/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isForbidden());
        
        verify(orderService, never()).updateOrderStatus(anyLong(), any(), anyLong());
    }
    
    @Test
    @DisplayName("PATCH /api/stores/{storeId}/orders/{orderId}/shipping should update shipping with CSRF token")
    @WithMockUser(roles = "SELLER")
    void updateShippingInfo_ShouldUpdateShipping_WhenCsrfTokenIsPresent() throws Exception {
        // Arrange
        when(orderService.updateShippingInfo(anyLong(), any(), anyLong())).thenReturn(new OrderDto());
        
        // Mock request body
        String requestBody = "{\"trackingNumber\":\"TRK-12345\",\"carrier\":\"FastShip\"}";
        
        // Act & Assert
        mockMvc.perform(patch("/api/stores/1/orders/1/shipping")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
        
        verify(orderService).updateShippingInfo(eq(1L), any(), anyLong());
    }
    
    @Test
    @DisplayName("PATCH /api/stores/{storeId}/orders/{orderId}/shipping should return 403 when CSRF token is missing")
    @WithMockUser(roles = "SELLER")
    void updateShippingInfo_ShouldReturn403_WhenCsrfTokenIsMissing() throws Exception {
        // Mock request body
        String requestBody = "{\"trackingNumber\":\"TRK-12345\",\"carrier\":\"FastShip\"}";
        
        // Act & Assert - Note the missing CSRF token
        mockMvc.perform(patch("/api/stores/1/orders/1/shipping")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isForbidden());
        
        verify(orderService, never()).updateShippingInfo(anyLong(), any(), anyLong());
    }
} 