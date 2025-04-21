package com.marketplace.salesapi.order.dto;

import com.marketplace.salesapi.order.model.OrderStatus;
import lombok.Data;

@Data
public class TestOrderDto {
    private Long id;
    private String orderNumber;
    private OrderStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
} 