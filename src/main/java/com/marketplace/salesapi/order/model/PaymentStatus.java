package com.marketplace.salesapi.order.model;

public enum PaymentStatus {
    PENDING,       // Payment is pending
    PROCESSING,    // Payment is being processed
    COMPLETED,     // Payment has been completed
    FAILED,        // Payment has failed
    REFUNDED       // Payment has been refunded
}