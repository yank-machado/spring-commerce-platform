package com.marketplace.salesapi.order.model;

public enum OrderStatus {
    PENDING,       // Order created but not yet processed
    PROCESSING,    // Order is being processed
    SHIPPED,       // Order has been shipped
    DELIVERED,     // Order has been delivered
    COMPLETED,     // Order has been completed
    CANCELLED,     // Order has been cancelled
    REFUNDED       // Order has been refunded
}