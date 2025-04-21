package com.marketplace.salesapi.order.dto;

import com.marketplace.salesapi.order.model.OrderStatus;
import lombok.Data;

@Data
public class TestUpdateOrderStatusRequest {
    private OrderStatus status;
    private String notes;

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
} 