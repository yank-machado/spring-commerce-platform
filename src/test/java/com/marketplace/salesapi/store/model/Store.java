package com.marketplace.salesapi.store.model;

import com.marketplace.salesapi.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Mock Store class for tests. Real implementation should be in main code.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Store {
    private Long id;
    private String name;
    private String description;
    private String logoUrl;
    private String bannerUrl;
    private StoreStatus status = StoreStatus.PENDING;
    private User owner;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 