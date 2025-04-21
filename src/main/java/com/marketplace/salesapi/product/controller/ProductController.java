package com.marketplace.salesapi.product.controller;

import com.marketplace.salesapi.product.dto.CreateProductRequest;
import com.marketplace.salesapi.product.dto.ProductDto;
import com.marketplace.salesapi.product.model.ProductStatus;
import com.marketplace.salesapi.product.service.ProductService;
import com.marketplace.salesapi.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Produtos", description = "API para gerenciamento de produtos")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    @Operation(summary = "Listar todos os produtos")
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<ProductDto> products = productService.getAllProducts(pageable);
        
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter produto por ID")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        ProductDto product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/search")
    @Operation(summary = "Pesquisar produtos por palavra-chave")
    public ResponseEntity<Page<ProductDto>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = productService.searchProducts(keyword, pageable);
        
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Listar produtos por categoria")
    public ResponseEntity<Page<ProductDto>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = productService.getProductsByCategory(categoryId, pageable);
        
        return ResponseEntity.ok(products);
    }

    @GetMapping("/tag/{tagName}")
    @Operation(summary = "Listar produtos por tag")
    public ResponseEntity<Page<ProductDto>> getProductsByTag(
            @PathVariable String tagName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = productService.getProductsByTag(tagName, pageable);
        
        return ResponseEntity.ok(products);
    }

    @PostMapping("/store/{storeId}")
    @Operation(summary = "Criar um novo produto")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ProductDto> createProduct(
            @PathVariable Long storeId,
            @Valid @RequestBody CreateProductRequest request) {
        
        Long userId = getCurrentUserId();
        ProductDto createdProduct = productService.createProduct(request, storeId, userId);
        
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um produto")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody CreateProductRequest request) {
        
        Long userId = getCurrentUserId();
        ProductDto updatedProduct = productService.updateProduct(id, request, userId);
        
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um produto")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        productService.deleteProduct(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status de um produto")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<Void> updateProductStatus(
            @PathVariable Long id,
            @RequestParam ProductStatus status) {
        
        Long userId = getCurrentUserId();
        productService.updateProductStatus(id, status, userId);
        
        return ResponseEntity.ok().build();
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