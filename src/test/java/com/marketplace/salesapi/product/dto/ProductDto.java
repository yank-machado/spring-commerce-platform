package com.marketplace.salesapi.product.dto;

import com.marketplace.salesapi.product.model.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Integer stockQuantity;
    private String sku;
    private ProductStatus status;
    private Long storeId;
    private String storeName;
    private Long categoryId;
    private String categoryName;
    private Set<TagDto> tags;
    private Set<ProductImageDto> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 