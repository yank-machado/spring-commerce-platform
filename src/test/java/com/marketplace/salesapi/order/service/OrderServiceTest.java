package com.marketplace.salesapi.order.service;

import com.marketplace.salesapi.exception.ResourceNotFoundException;
import com.marketplace.salesapi.exception.UnauthorizedException;
import com.marketplace.salesapi.order.dto.CreateOrderRequest;
import com.marketplace.salesapi.order.dto.OrderDto;
import com.marketplace.salesapi.order.dto.UpdateOrderStatusRequest;
import com.marketplace.salesapi.order.model.Order;
import com.marketplace.salesapi.order.model.OrderStatus;
import com.marketplace.salesapi.order.repository.OrderItemRepository;
import com.marketplace.salesapi.order.repository.OrderPaymentRepository;
import com.marketplace.salesapi.order.repository.OrderRepository;
import com.marketplace.salesapi.order.repository.ShippingInfoRepository;
import com.marketplace.salesapi.product.repository.ProductRepository;
import com.marketplace.salesapi.user.model.ERole;
import com.marketplace.salesapi.user.model.User;
import com.marketplace.salesapi.user.repository.UserRepository;
import com.marketplace.salesapi.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderPaymentRepository orderPaymentRepository;

    @Mock
    private ShippingInfoRepository shippingInfoRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    private User customer;
    private User admin;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        customer = TestUtil.createTestUser("customer@example.com", ERole.ROLE_CUSTOMER);
        customer.setId(1L);

        admin = TestUtil.createTestUser("admin@example.com", ERole.ROLE_ADMIN);
        admin.setId(2L);

        testOrder = TestUtil.createTestOrder(customer);
        testOrder.setId(1L);
    }

    @Test
    @DisplayName("Should get order by ID when it exists")
    void getOrderById_ShouldReturnOrder_WhenOrderExists() {
        // Arrange
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(testOrder));

        // Act
        OrderDto result = orderService.getOrderById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testOrder.getId(), result.getId());
        verify(orderRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when getting order by non-existent ID")
    void getOrderById_ShouldThrowException_WhenOrderDoesNotExist() {
        // Arrange
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(99L));
        verify(orderRepository).findById(99L);
    }

    @Test
    @DisplayName("Should get all orders paginated")
    void getAllOrders_ShouldReturnPageOfOrders() {
        // Arrange
        Page<Order> orderPage = new PageImpl<>(List.of(testOrder));
        when(orderRepository.findAll(any(Pageable.class))).thenReturn(orderPage);

        // Act
        Page<OrderDto> result = orderService.getAllOrders(Pageable.unpaged());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(orderRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should cancel order when user is authorized")
    void cancelOrder_ShouldCancelOrder_WhenUserIsAuthorized() {
        // Arrange
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(testOrder));
        
        // Act
        orderService.cancelOrder(1L, 1L); // Customer cancelling their own order
        
        // Assert
        assertEquals(OrderStatus.CANCELLED, testOrder.getStatus());
        verify(orderRepository).save(testOrder);
    }
    
    @Test
    @DisplayName("Should throw exception when unauthorized user tries to cancel order")
    void cancelOrder_ShouldThrowException_WhenUserIsUnauthorized() {
        // Arrange
        User anotherUser = TestUtil.createTestUser("another@example.com", ERole.ROLE_CUSTOMER);
        anotherUser.setId(3L);
        
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(testOrder));
        
        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> orderService.cancelOrder(1L, 3L));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should update order status when user is authorized")
    void updateOrderStatus_ShouldUpdateStatus_WhenUserIsAuthorized() {
        // Arrange
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setStatus(OrderStatus.PROCESSING);
        
        // Act
        OrderDto result = orderService.updateOrderStatus(1L, request, 1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.PROCESSING, testOrder.getStatus());
        verify(orderRepository).save(testOrder);
    }
} 