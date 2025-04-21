package com.marketplace.salesapi.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateStoreRequest {
    
    @NotBlank
    @Size(min = 3, max = 100)
    private String name;
    
    @Size(max = 500)
    private String description;
    
    private String logoUrl;
    
    private String bannerUrl;
}