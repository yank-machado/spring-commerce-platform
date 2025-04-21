package com.marketplace.salesapi.order.service;

import com.marketplace.salesapi.order.dto.OrderSummaryDto;
import com.marketplace.salesapi.order.dto.ProductSalesDto;
import com.marketplace.salesapi.order.dto.StoreSalesDto;
import com.marketplace.salesapi.order.model.Order;
import com.marketplace.salesapi.order.model.OrderItem;
import com.marketplace.salesapi.order.model.OrderStatus;
import com.marketplace.salesapi.order.repository.OrderItemRepository;
import com.marketplace.salesapi.order.repository.OrderRepository;
import com.marketplace.salesapi.product.model.Product;
import com.marketplace.salesapi.store.model.Store;
import com.marketplace.salesapi.store.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderAnalyticsService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Transactional(readOnly = true)
    public OrderSummaryDto getOrderSummary(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderRepository.findOrdersInDateRange(startDate, endDate);
        
        OrderSummaryDto summary = new OrderSummaryDto();
        summary.setTotalOrders((long) orders.size());
        
        // Calculate total revenue
        BigDecimal totalRevenue = orders.stream()
                .map(Order::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setTotalRevenue(totalRevenue);
        
        // Calculate average order value
        BigDecimal averageOrderValue = BigDecimal.ZERO;
        if (!orders.isEmpty()) {
            averageOrderValue = totalRevenue.divide(new BigDecimal(orders.size()), 2, RoundingMode.HALF_UP);
        }
        summary.setAverageOrderValue(averageOrderValue);
        
        // Group orders by status
        Map<OrderStatus, Long> ordersByStatus = orders.stream()
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));
        summary.setOrdersByStatus(ordersByStatus);
        
        // Group orders by month
        Map<String, Long> ordersByMonth = orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                        Collectors.counting()));
        summary.setOrdersByMonth(ordersByMonth);
        
        return summary;
    }

    @Transactional(readOnly = true)
    public OrderSummaryDto getStoreSummary(Long storeId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> allOrders = orderRepository.findOrdersInDateRange(startDate, endDate);
        
        // Filter orders that contain products from the specified store
        List<Order> storeOrders = allOrders.stream()
                .filter(order -> order.getItems().stream()
                        .anyMatch(item -> item.getProduct().getStore().getId().equals(storeId)))
                .collect(Collectors.toList());
        
        OrderSummaryDto summary = new OrderSummaryDto();
        summary.setTotalOrders((long) storeOrders.size());
        
        // Calculate total revenue for the store
        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (Order order : storeOrders) {
            for (OrderItem item : order.getItems()) {
                if (item.getProduct().getStore().getId().equals(storeId)) {
                    totalRevenue = totalRevenue.add(item.getSubtotal());
                }
            }
        }
        summary.setTotalRevenue(totalRevenue);
        
        // Calculate average order value
        BigDecimal averageOrderValue = BigDecimal.ZERO;
        if (!storeOrders.isEmpty()) {
            averageOrderValue = totalRevenue.divide(new BigDecimal(storeOrders.size()), 2, RoundingMode.HALF_UP);
        }
        summary.setAverageOrderValue(averageOrderValue);
        
        // Group orders by status
        Map<OrderStatus, Long> ordersByStatus = storeOrders.stream()
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));
        summary.setOrdersByStatus(ordersByStatus);
        
        // Group orders by month
        Map<String, Long> ordersByMonth = storeOrders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                        Collectors.counting()));
        summary.setOrdersByMonth(ordersByMonth);
        
        return summary;
    }

    @Transactional(readOnly = true)
    public List<StoreSalesDto> getTopSellingStores(int limit, LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderRepository.findOrdersInDateRange(startDate, endDate);
        
        // Group by store and calculate metrics
        Map<Long, StoreSalesDto> storeStats = new HashMap<>();
        
        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                Store store = product.getStore();
                Long storeId = store.getId();
                
                StoreSalesDto storeSales = storeStats.getOrDefault(storeId, new StoreSalesDto());
                storeSales.setStoreId(storeId);
                storeSales.setStoreName(store.getName());
                
                // Update order count
                Long totalOrders = storeSales.getTotalOrders() == null ? 1L : storeSales.getTotalOrders() + 1;
                storeSales.setTotalOrders(totalOrders);
                
                // Update revenue
                BigDecimal revenue = storeSales.getTotalRevenue() == null ? 
                        item.getSubtotal() : storeSales.getTotalRevenue().add(item.getSubtotal());
                storeSales.setTotalRevenue(revenue);
                
                storeStats.put(storeId, storeSales);
            }
        }
        
        // Sort by revenue and limit results
        return storeStats.values().stream()
                .sorted(Comparator.comparing(StoreSalesDto::getTotalRevenue).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductSalesDto> getTopSellingProducts(int limit, LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderRepository.findOrdersInDateRange(startDate, endDate);
        
        // Group by product and calculate metrics
        Map<Long, ProductSalesDto> productStats = new HashMap<>();
        
        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                Long productId = product.getId();
                
                ProductSalesDto productSales = productStats.getOrDefault(productId, new ProductSalesDto());
                productSales.setProductId(productId);
                productSales.setProductName(product.getName());
                
                // Update quantity
                Long quantity = productSales.getQuantitySold() == null ? 
                        item.getQuantity().longValue() : productSales.getQuantitySold() + item.getQuantity();
                productSales.setQuantitySold(quantity);
                
                // Update revenue
                BigDecimal revenue = productSales.getTotalRevenue() == null ? 
                        item.getSubtotal() : productSales.getTotalRevenue().add(item.getSubtotal());
                productSales.setTotalRevenue(revenue);
                
                productStats.put(productId, productSales);
            }
        }
        
        // Sort by quantity sold and limit results
        return productStats.values().stream()
                .sorted(Comparator.comparing(ProductSalesDto::getQuantitySold).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}