package com.marketplace.salesapi.order.dto;

import com.marketplace.salesapi.order.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private String orderNumber;
    private Long userId;
    private String userName;
    private OrderStatus status;
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal total;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemDto> items;
    private OrderPaymentDto payment;
    private ShippingInfoDto shippingInfo;
} 