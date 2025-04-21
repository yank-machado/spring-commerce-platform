package com.marketplace.salesapi.order.dto;

import java.math.BigDecimal;

public class ProductSalesDto {
    private Long productId;
    private String productName;
    private Long quantitySold;
    private BigDecimal totalRevenue;
    
    public ProductSalesDto() {
    }
    
    public ProductSalesDto(Long productId, String productName, Long quantitySold, BigDecimal totalRevenue) {
        this.productId = productId;
        this.productName = productName;
        this.quantitySold = quantitySold;
        this.totalRevenue = totalRevenue;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public Long getQuantitySold() {
        return quantitySold;
    }
    
    public void setQuantitySold(Long quantitySold) {
        this.quantitySold = quantitySold;
    }
    
    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
    
    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}