package com.marketplace.salesapi.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.salesapi.order.dto.OrderDto;
import com.marketplace.salesapi.order.dto.UpdateOrderStatusRequest;
import com.marketplace.salesapi.order.model.OrderStatus;
import com.marketplace.salesapi.order.service.OrderService;
import com.marketplace.salesapi.config.TestSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OrderController.class, 
            properties = {"spring.main.allow-bean-definition-overriding=true"})
@Import(TestSecurityConfig.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    // Mock DTOs for testing
    static class MockOrderDto {
        private Long id;
        private String orderNumber;
        private OrderStatus status;
        
        public MockOrderDto() {}
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getOrderNumber() { return orderNumber; }
        public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
        
        public OrderStatus getStatus() { return status; }
        public void setStatus(OrderStatus status) { this.status = status; }
    }
    
    static class MockUpdateStatusRequest {
        private OrderStatus status;
        private String notes;
        
        public OrderStatus getStatus() { return status; }
        public void setStatus(OrderStatus status) { this.status = status; }
        
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
    
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

    private MockOrderDto orderDto;
    private MockUpdateStatusRequest updateOrderStatusRequest;
    private MockUserDetails customerUserDetails;
    private MockUserDetails adminUserDetails;

    @BeforeEach
    void setUp() {
        // Create test order DTO
        orderDto = new MockOrderDto();
        orderDto.setId(1L);
        orderDto.setOrderNumber("ORD-123456");
        orderDto.setStatus(OrderStatus.PENDING);

        // Create update status request
        updateOrderStatusRequest = new MockUpdateStatusRequest();
        updateOrderStatusRequest.setStatus(OrderStatus.PROCESSING);
        updateOrderStatusRequest.setNotes("Order processing started");

        // Create user details for testing
        List<SimpleGrantedAuthority> customerAuthorities = new ArrayList<>();
        customerAuthorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        customerUserDetails = new MockUserDetails(
                1L,
                "customer@example.com",
                "password",
                customerAuthorities
        );

        List<SimpleGrantedAuthority> adminAuthorities = new ArrayList<>();
        adminAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        adminUserDetails = new MockUserDetails(
                2L,
                "admin@example.com",
                "password",
                adminAuthorities
        );
    }

    @Test
    @DisplayName("GET /api/orders should return all orders for admin")
    @WithMockUser(roles = "ADMIN")
    void getAllOrders_ShouldReturnOrdersPage_ForAdmin() throws Exception {
        // Arrange
        MockOrderDto sampleDto = new MockOrderDto();
        sampleDto.setId(1L);
        sampleDto.setOrderNumber("TEST-123");
        sampleDto.setStatus(OrderStatus.PENDING);
        
        Page<OrderDto> orderPage = new PageImpl<>(List.of(new OrderDto()), PageRequest.of(0, 10), 1);
        when(orderService.getAllOrders(any(Pageable.class))).thenReturn(orderPage);

        // Act & Assert
        mockMvc.perform(get("/api/orders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(orderService).getAllOrders(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/orders/{id} should return order by ID")
    void getOrderById_ShouldReturnOrder_WhenOrderExists() throws Exception {
        // Arrange
        MockOrderDto mockDto = new MockOrderDto();
        mockDto.setId(1L);
        mockDto.setOrderNumber("ORD-123456");
        mockDto.setStatus(OrderStatus.PENDING);
        
        when(orderService.getOrderById(anyLong())).thenReturn(new OrderDto());

        // Act & Assert
        mockMvc.perform(get("/api/orders/1")
                .with(user(customerUserDetails))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(orderService).getOrderById(1L);
    }

    @Test
    @DisplayName("PUT /api/orders/{id}/status should update order status")
    void updateOrderStatus_ShouldUpdateStatus_WhenUserIsAuthorized() throws Exception {
        // Arrange
        MockOrderDto resultDto = new MockOrderDto();
        resultDto.setStatus(OrderStatus.PROCESSING);
        
        when(orderService.updateOrderStatus(anyLong(), any(UpdateOrderStatusRequest.class), anyLong()))
                .thenReturn(new OrderDto());

        // Act & Assert
        mockMvc.perform(put("/api/orders/1/status")
                .with(user(customerUserDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateOrderStatusRequest)))
                .andExpect(status().isOk());

        verify(orderService).updateOrderStatus(eq(1L), any(UpdateOrderStatusRequest.class), anyLong());
    }

    @Test
    @DisplayName("DELETE /api/orders/{id} should cancel order")
    void cancelOrder_ShouldCancelOrder_WhenUserIsAuthorized() throws Exception {
        // Arrange
        doNothing().when(orderService).cancelOrder(anyLong(), anyLong());

        // Act & Assert
        mockMvc.perform(delete("/api/orders/1")
                .with(user(customerUserDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(orderService).cancelOrder(eq(1L), anyLong());
    }
    
    @Test
    @DisplayName("POST /api/orders should create a new order")
    void createOrder_ShouldCreateOrder_WhenUserIsAuthorized() throws Exception {
        // Arrange
        when(orderService.createOrder(any(), anyLong())).thenReturn(new OrderDto());
        
        // Mock request body
        String requestBody = "{\"items\":[{\"productId\":1,\"quantity\":2}],\"shippingAddress\":{\"street\":\"Main St\",\"city\":\"Springfield\"}}";
        
        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .with(user(customerUserDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated());
        
        verify(orderService).createOrder(any(), anyLong());
    }
    
    @Test
    @DisplayName("PATCH /api/orders/{id}/payment should update payment status")
    void updatePaymentStatus_ShouldUpdatePaymentStatus_WhenUserIsAuthorized() throws Exception {
        // Arrange
        when(orderService.updatePaymentStatus(anyLong(), any(), anyLong())).thenReturn(new OrderDto());
        
        // Mock request body
        String requestBody = "{\"status\":\"PAID\",\"transactionId\":\"TRX-12345\"}";
        
        // Act & Assert
        mockMvc.perform(patch("/api/orders/1/payment")
                .with(user(adminUserDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
        
        verify(orderService).updatePaymentStatus(eq(1L), any(), anyLong());
    }
    
    @Test
    @DisplayName("PATCH /api/orders/{id}/shipping should update shipping info")
    void updateShippingInfo_ShouldUpdateShippingInfo_WhenUserIsAuthorized() throws Exception {
        // Arrange
        when(orderService.updateShippingInfo(anyLong(), any(), anyLong())).thenReturn(new OrderDto());
        
        // Mock request body
        String requestBody = "{\"trackingNumber\":\"TRK-12345\",\"carrier\":\"FastShip\"}";
        
        // Act & Assert
        mockMvc.perform(patch("/api/orders/1/shipping")
                .with(user(adminUserDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
        
        verify(orderService).updateShippingInfo(eq(1L), any(), anyLong());
    }

    @Test
    @DisplayName("POST /api/orders should return 403 when CSRF token is missing")
    @WithMockUser(roles = "CUSTOMER")
    void createOrder_ShouldReturn403_WhenCsrfTokenIsMissing() throws Exception {
        // Mock request body
        String requestBody = "{\"items\":[{\"productId\":1,\"quantity\":2}],\"shippingAddress\":{\"street\":\"Main St\",\"city\":\"Springfield\"}}";
        
        // Act & Assert - Note the missing CSRF token
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isForbidden());
        
        verify(orderService, never()).createOrder(any(), anyLong());
    }
} 