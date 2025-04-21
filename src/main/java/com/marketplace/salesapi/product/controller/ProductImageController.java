package com.marketplace.salesapi.product.controller;

import com.marketplace.salesapi.product.dto.ProductImageDto;
import com.marketplace.salesapi.product.service.ProductImageService;
import com.marketplace.salesapi.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/images")
@Tag(name = "Imagens de Produtos", description = "API para gerenciamento de imagens de produtos")
public class ProductImageController {

    @Autowired
    private ProductImageService productImageService;

    @PostMapping
    @Operation(summary = "Adicionar imagem a um produto")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ProductImageDto> addProductImage(
            @PathVariable Long productId,
            @RequestParam String imageUrl,
            @RequestParam(required = false) Boolean isPrimary,
            @RequestParam(required = false) Integer displayOrder) {
        
        Long userId = getCurrentUserId();
        ProductImageDto imageDto = productImageService.addProductImage(
                productId, imageUrl, isPrimary, displayOrder, userId);
        
        return new ResponseEntity<>(imageDto, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listar imagens de um produto")
    public ResponseEntity<List<ProductImageDto>> getProductImages(@PathVariable Long productId) {
        List<ProductImageDto> images = productImageService.getProductImages(productId);
        return ResponseEntity.ok(images);
    }

    @DeleteMapping("/{imageId}")
    @Operation(summary = "Excluir imagem de um produto")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProductImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        
        Long userId = getCurrentUserId();
        productImageService.deleteProductImage(imageId, userId);
        
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{imageId}/primary")
    @Operation(summary = "Definir imagem como primária")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ProductImageDto> setPrimaryImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        
        Long userId = getCurrentUserId();
        ProductImageDto imageDto = productImageService.setPrimaryImage(imageId, userId);
        
        return ResponseEntity.ok(imageDto);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
}