package com.marketplace.salesapi.store.controller;

import com.marketplace.salesapi.security.UserDetailsImpl;
import com.marketplace.salesapi.store.dto.CreateStoreAddressRequest;
import com.marketplace.salesapi.store.dto.StoreAddressDto;
import com.marketplace.salesapi.store.service.StoreAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stores/{storeId}/address")
@Tag(name = "Endereços de Lojas", description = "API para gerenciamento de endereços de lojas")
public class StoreAddressController {

    @Autowired
    private StoreAddressService storeAddressService;

    @PostMapping
    @Operation(summary = "Criar ou atualizar endereço de loja")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<StoreAddressDto> createOrUpdateStoreAddress(
            @PathVariable Long storeId,
            @Valid @RequestBody CreateStoreAddressRequest request) {
        Long userId = getCurrentUserId();
        StoreAddressDto addressDto = storeAddressService.createOrUpdateStoreAddress(storeId, request, userId);
        return new ResponseEntity<>(addressDto, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Obter endereço de loja")
    public ResponseEntity<StoreAddressDto> getStoreAddress(@PathVariable Long storeId) {
        try {
            StoreAddressDto addressDto = storeAddressService.getStoreAddressByStoreId(storeId);
            if (addressDto == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(addressDto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null || 
            !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            // Return default user ID for development testing
            return 1L;
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
}