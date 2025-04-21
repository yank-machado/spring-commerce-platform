package com.marketplace.salesapi.store.dto;

import com.marketplace.salesapi.store.model.StoreStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreDto {
    private Long id;
    private String name;
    private String description;
    private String logoUrl;
    private String bannerUrl;
    private StoreStatus status;
    private Long ownerId;
    private String ownerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}