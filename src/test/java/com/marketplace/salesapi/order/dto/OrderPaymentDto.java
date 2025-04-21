package com.marketplace.salesapi.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPaymentDto {
    private Long id;
    private String paymentMethod;
    private String paymentStatus;
    private BigDecimal amount;
    private String transactionId;
    private String paymentDetails;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 