package com.marketplace.salesapi.order.dto;

import com.marketplace.salesapi.order.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {
    private OrderStatus status;
    private String notes;
} 