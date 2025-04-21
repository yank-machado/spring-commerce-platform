package com.marketplace.salesapi.order.service;

import com.marketplace.salesapi.exception.ResourceNotFoundException;
import com.marketplace.salesapi.exception.UnauthorizedException;
import com.marketplace.salesapi.order.dto.*;
import com.marketplace.salesapi.order.model.*;
import com.marketplace.salesapi.order.repository.OrderItemRepository;
import com.marketplace.salesapi.order.repository.OrderPaymentRepository;
import com.marketplace.salesapi.order.repository.OrderRepository;
import com.marketplace.salesapi.order.repository.ShippingInfoRepository;
import com.marketplace.salesapi.product.model.Product;
import com.marketplace.salesapi.product.repository.ProductRepository;
import com.marketplace.salesapi.store.model.Store;
import com.marketplace.salesapi.user.model.User;
import com.marketplace.salesapi.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderPaymentRepository orderPaymentRepository;

    @Autowired
    private ShippingInfoRepository shippingInfoRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<OrderDto> getAllOrders(Pageable pageable) {
        Page<Order> orders = orderRepository.findAll(pageable);
        return orders.map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getOrdersByUser(Long userId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);
        return orders.map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getOrdersByStore(Long storeId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByStoreId(storeId, pageable);
        return orders.map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getOrdersByStoreAndStatus(Long storeId, OrderStatus status, Pageable pageable) {
        Page<Order> orders = orderRepository.findByStoreIdAndStatus(storeId, status, pageable);
        return orders.map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        Page<Order> orders = orderRepository.findByStatus(status, pageable);
        return orders.map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long id) {
        Order order = findOrderById(id);
        return convertToDto(order);
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com o número: " + orderNumber));
        return convertToDto(order);
    }

    @Transactional
    public OrderDto createOrder(CreateOrderRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + userId));

        // Create new order
        Order order = new Order();
        order.setUser(user);
        order.setNotes(request.getNotes());
        
        // Calculate shipping cost (simplified for now)
        order.setShippingCost(new BigDecimal("15.00"));
        
        // Save order to get ID
        Order savedOrder = orderRepository.save(order);
        
        // Process order items
        List<OrderItem> orderItems = new ArrayList<>();
        for (CreateOrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " + itemRequest.getProductId()));
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setProductSku(product.getSku());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            
            orderItems.add(orderItem);
        }
        
        // Save order items
        orderItemRepository.saveAll(orderItems);
        
        // Create shipping info
        ShippingInfo shippingInfo = new ShippingInfo();
        shippingInfo.setOrder(savedOrder);
        shippingInfo.setRecipientName(request.getShippingInfo().getRecipientName());
        shippingInfo.setStreet(request.getShippingInfo().getStreet());
        shippingInfo.setNumber(request.getShippingInfo().getNumber());
        shippingInfo.setComplement(request.getShippingInfo().getComplement());
        shippingInfo.setNeighborhood(request.getShippingInfo().getNeighborhood());
        shippingInfo.setCity(request.getShippingInfo().getCity());
        shippingInfo.setState(request.getShippingInfo().getState());
        shippingInfo.setZipCode(request.getShippingInfo().getZipCode());
        shippingInfo.setCountry(request.getShippingInfo().getCountry());
        shippingInfo.setPhoneNumber(request.getShippingInfo().getPhoneNumber());
        shippingInfo.setShippingMethod(request.getShippingInfo().getShippingMethod());
        shippingInfo.setEstimatedDeliveryDate(LocalDateTime.now().plusDays(7)); // Simplified
        
        shippingInfoRepository.save(shippingInfo);
        
        // Create payment
        OrderPayment payment = new OrderPayment();
        payment.setOrder(savedOrder);
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setPaymentDetails(request.getPaymentDetails());
        payment.setAmount(savedOrder.getTotal());
        
        orderPaymentRepository.save(payment);
        
        // Recalculate order totals
        savedOrder.recalculateOrderTotals();
        orderRepository.save(savedOrder);
        
        return convertToDto(savedOrder);
    }

    @Transactional
    public OrderDto updateOrderStatus(Long id, UpdateOrderStatusRequest request, Long userId) {
        Order order = findOrderById(id);
        
        // Check if user is authorized (admin or the order owner)
        if (!order.getUser().getId().equals(userId) && !isUserAdmin(userId)) {
            throw new UnauthorizedException("Você não tem permissão para atualizar este pedido");
        }
        
        order.setStatus(request.getStatus());
        if (request.getNotes() != null) {
            order.setNotes(request.getNotes());
        }
        
        Order updatedOrder = orderRepository.save(order);
        return convertToDto(updatedOrder);
    }

    @Transactional
    public OrderDto updatePaymentStatus(Long orderId, UpdatePaymentStatusRequest request, Long userId) {
        Order order = findOrderById(orderId);
        
        // Check if user is authorized (admin or seller)
        if (!isUserAdmin(userId) && !isUserSeller(userId)) {
            throw new UnauthorizedException("Você não tem permissão para atualizar o status de pagamento");
        }
        
        OrderPayment payment = orderPaymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado para o pedido com ID: " + orderId));
        
        payment.setPaymentStatus(request.getPaymentStatus());
        
        if (request.getTransactionId() != null) {
            payment.setTransactionId(request.getTransactionId());
        }
        
        if (request.getPaymentDetails() != null) {
            payment.setPaymentDetails(request.getPaymentDetails());
        }
        
        // If payment is completed, update order status
        if (request.getPaymentStatus() == PaymentStatus.COMPLETED && order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.PROCESSING);
            orderRepository.save(order);
        }
        
        orderPaymentRepository.save(payment);
        return convertToDto(order);
    }

    @Transactional
    public OrderDto updateShippingInfo(Long orderId, UpdateShippingInfoRequest request, Long userId) {
        Order order = findOrderById(orderId);
        
        // Check if user is authorized (admin or seller)
        if (!isUserAdmin(userId) && !isUserSeller(userId)) {
            throw new UnauthorizedException("Você não tem permissão para atualizar informações de envio");
        }
        
        ShippingInfo shippingInfo = shippingInfoRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Informações de envio não encontradas para o pedido com ID: " + orderId));
        
        if (request.getTrackingNumber() != null) {
            shippingInfo.setTrackingNumber(request.getTrackingNumber());
        }
        
        if (request.getEstimatedDeliveryDate() != null) {
            shippingInfo.setEstimatedDeliveryDate(request.getEstimatedDeliveryDate());
        }
        
        if (request.getShippedDate() != null) {
            shippingInfo.setShippedDate(request.getShippedDate());
            // Update order status to SHIPPED if it's in PROCESSING
            if (order.getStatus() == OrderStatus.PROCESSING) {
                order.setStatus(OrderStatus.SHIPPED);
                orderRepository.save(order);
            }
        }
        
        if (request.getDeliveredDate() != null) {
            shippingInfo.setDeliveredDate(request.getDeliveredDate());
            // Update order status to DELIVERED
            order.setStatus(OrderStatus.DELIVERED);
            orderRepository.save(order);
        }
        
        shippingInfoRepository.save(shippingInfo);
        return convertToDto(order);
    }

    @Transactional
    public void cancelOrder(Long id, Long userId) {
        Order order = findOrderById(id);
        
        // Check if user is authorized (admin or the order owner)
        if (!order.getUser().getId().equals(userId) && !isUserAdmin(userId)) {
            throw new UnauthorizedException("Você não tem permissão para cancelar este pedido");
        }
        
        // Only allow cancellation if order is in PENDING or PROCESSING status
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.PROCESSING) {
            throw new IllegalStateException("Não é possível cancelar um pedido que já foi enviado ou entregue");
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    // Helper methods
    private Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com o ID: " + id));
    }

    private OrderDto convertToDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setUserId(order.getUser().getId());
        dto.setUserName(order.getUser().getName());
        dto.setStatus(order.getStatus());
        dto.setSubtotal(order.getSubtotal());
        dto.setShippingCost(order.getShippingCost());
        dto.setDiscount(order.getDiscount());
        dto.setTax(order.getTax());
        dto.setTotal(order.getTotal());
        dto.setNotes(order.getNotes());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        
        // Convert order items
        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        dto.setItems(itemDtos);
        
        // Convert payment info
        if (order.getPayment() != null) {
            dto.setPayment(convertToDto(order.getPayment()));
        }
        
        // Convert shipping info
        if (order.getShippingInfo() != null) {
            dto.setShippingInfo(convertToDto(order.getShippingInfo()));
        }
        
        return dto;
    }

    private OrderItemDto convertToDto(OrderItem item) {
        OrderItemDto dto = new OrderItemDto();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProductName());
        dto.setProductSku(item.getProductSku());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setDiscount(item.getDiscount());
        dto.setSubtotal(item.getSubtotal());
        
        // Get store info
        Store store = item.getProduct().getStore();
        if (store != null) {
            dto.setStoreId(store.getId());
            dto.setStoreName(store.getName());
        }
        
        return dto;
    }

    private OrderPaymentDto convertToDto(OrderPayment payment) {
        OrderPaymentDto dto = new OrderPaymentDto();
        dto.setId(payment.getId());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setTransactionId(payment.getTransactionId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentDetails(payment.getPaymentDetails());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());
        return dto;
    }

    private ShippingInfoDto convertToDto(ShippingInfo shippingInfo) {
        ShippingInfoDto dto = new ShippingInfoDto();
        dto.setId(shippingInfo.getId());
        dto.setRecipientName(shippingInfo.getRecipientName());
        dto.setStreet(shippingInfo.getStreet());
        dto.setNumber(shippingInfo.getNumber());
        dto.setComplement(shippingInfo.getComplement());
        dto.setNeighborhood(shippingInfo.getNeighborhood());
        dto.setCity(shippingInfo.getCity());
        dto.setState(shippingInfo.getState());
        dto.setZipCode(shippingInfo.getZipCode());
        dto.setCountry(shippingInfo.getCountry());
        dto.setPhoneNumber(shippingInfo.getPhoneNumber());
        dto.setTrackingNumber(shippingInfo.getTrackingNumber());
        dto.setShippingMethod(shippingInfo.getShippingMethod());
        dto.setEstimatedDeliveryDate(shippingInfo.getEstimatedDeliveryDate());
        dto.setShippedDate(shippingInfo.getShippedDate());
        dto.setDeliveredDate(shippingInfo.getDeliveredDate());
        dto.setCreatedAt(shippingInfo.getCreatedAt());
        dto.setUpdatedAt(shippingInfo.getUpdatedAt());
        return dto;
    }
    
    // Authorization helper methods
    private boolean isUserAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Usuário não autorizado com o ID: " + userId));
        
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().name().equals("ROLE_ADMIN"));
    }
    
    private boolean isUserSeller(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Usuário não autorizado com o ID: " + userId));
        
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().name().equals("ROLE_SELLER"));
    }
}