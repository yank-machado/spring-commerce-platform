package com.marketplace.salesapi.store.dto;

import com.marketplace.salesapi.store.model.StoreStatus;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStoreRequest {
    
    @Size(min = 3, max = 100)
    private String name;
    
    @Size(max = 500)
    private String description;
    
    private String logoUrl;
    
    private String bannerUrl;
    
    private StoreStatus status;
}