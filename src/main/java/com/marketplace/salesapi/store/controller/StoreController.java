package com.marketplace.salesapi.store.controller;

import com.marketplace.salesapi.security.UserDetailsImpl;
import com.marketplace.salesapi.store.dto.CreateStoreRequest;
import com.marketplace.salesapi.store.dto.StoreDto;
import com.marketplace.salesapi.store.dto.UpdateStoreRequest;
import com.marketplace.salesapi.store.model.StoreStatus;
import com.marketplace.salesapi.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
@Tag(name = "Lojas", description = "API para gerenciamento de lojas")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @PostMapping
    @Operation(summary = "Criar uma nova loja")
    public ResponseEntity<StoreDto> createStore(@Valid @RequestBody CreateStoreRequest request) {
        Long userId = getCurrentUserId();
        StoreDto newStore = storeService.createStore(request, userId);
        return new ResponseEntity<>(newStore, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter loja por ID")
    public ResponseEntity<StoreDto> getStoreById(@PathVariable Long id) {
        StoreDto store = storeService.getStoreById(id);
        return ResponseEntity.ok(store);
    }

    @GetMapping
    @Operation(summary = "Listar todas as lojas")
    public ResponseEntity<List<StoreDto>> getAllStores() {
        List<StoreDto> stores = storeService.getAllStores();
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "Listar lojas por dono")
    public ResponseEntity<List<StoreDto>> getStoresByOwnerId(@PathVariable Long ownerId) {
        List<StoreDto> stores = storeService.getStoresByOwnerId(ownerId);
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Listar lojas por status")
    public ResponseEntity<List<StoreDto>> getStoresByStatus(@PathVariable StoreStatus status) {
        List<StoreDto> stores = storeService.getStoresByStatus(status);
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/my-stores")
    @Operation(summary = "Listar lojas do usuário logado")
    public ResponseEntity<List<StoreDto>> getMyStores() {
        Long userId = getCurrentUserId();
        List<StoreDto> stores = storeService.getStoresByOwnerId(userId);
        return ResponseEntity.ok(stores);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma loja")
    public ResponseEntity<StoreDto> updateStore(@PathVariable Long id, @Valid @RequestBody UpdateStoreRequest request) {
        Long userId = getCurrentUserId();
        StoreDto updatedStore = storeService.updateStore(id, request, userId);
        return ResponseEntity.ok(updatedStore);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir uma loja")
    public ResponseEntity<Void> deleteStore(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        storeService.deleteStore(id, userId);
        return ResponseEntity.noContent().build();
    }

    private Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                return userDetails.getId();
            }
        } catch (Exception e) {
            // Log a tentativa falha de obter usuário autenticado
            System.out.println("Nenhum usuário autenticado, usando ID de desenvolvimento");
        }
        
        // Retorna ID fictício para desenvolvimento (1L)
        return 1L;
    }
}