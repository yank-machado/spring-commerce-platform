package com.marketplace.salesapi.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    private List<OrderItemRequest> items;

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
} 