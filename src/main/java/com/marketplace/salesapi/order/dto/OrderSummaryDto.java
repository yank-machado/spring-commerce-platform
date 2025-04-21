package com.marketplace.salesapi.order.dto;

import com.marketplace.salesapi.order.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDto {
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private Map<OrderStatus, Long> ordersByStatus;
    private Map<String, Long> ordersByMonth;
    private BigDecimal averageOrderValue;
    
    public Long getTotalOrders() {
        return totalOrders;
    }
    
    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }
    
    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
    
    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    
    public Map<OrderStatus, Long> getOrdersByStatus() {
        return ordersByStatus;
    }
    
    public void setOrdersByStatus(Map<OrderStatus, Long> ordersByStatus) {
        this.ordersByStatus = ordersByStatus;
    }
    
    public Map<String, Long> getOrdersByMonth() {
        return ordersByMonth;
    }
    
    public void setOrdersByMonth(Map<String, Long> ordersByMonth) {
        this.ordersByMonth = ordersByMonth;
    }
    
    public BigDecimal getAverageOrderValue() {
        return averageOrderValue;
    }
    
    public void setAverageOrderValue(BigDecimal averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }
}