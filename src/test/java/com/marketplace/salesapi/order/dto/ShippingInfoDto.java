package com.marketplace.salesapi.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingInfoDto {
    private Long id;
    private String recipientName;
    private String street;
    private String number;
    private String complement;
    private String neighborhood;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String phoneNumber;
    private String shippingMethod;
    private String trackingNumber;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime shippedDate;
    private LocalDateTime deliveredDate;
} 