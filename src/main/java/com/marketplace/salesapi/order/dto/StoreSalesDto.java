package com.marketplace.salesapi.order.dto;

import java.math.BigDecimal;

public class StoreSalesDto {
    private Long storeId;
    private String storeName;
    private Long totalOrders;
    private BigDecimal totalRevenue;
    
    public StoreSalesDto() {
    }
    
    public StoreSalesDto(Long storeId, String storeName, Long totalOrders, BigDecimal totalRevenue) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
    }
    
    public Long getStoreId() {
        return storeId;
    }
    
    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }
    
    public String getStoreName() {
        return storeName;
    }
    
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
    
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
}