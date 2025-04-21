package com.marketplace.salesapi.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateOrderItemRequest {
    
    @NotNull
    private Long productId;
    
    @NotNull
    @Positive
    private Integer quantity;
    
    public CreateOrderItemRequest() {
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}