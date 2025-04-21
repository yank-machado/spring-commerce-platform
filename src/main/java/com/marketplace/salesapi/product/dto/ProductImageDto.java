package com.marketplace.salesapi.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDto {
    private Long id;
    private String imageUrl;
    private Boolean isPrimary;
    private Integer displayOrder;
}