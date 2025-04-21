package com.marketplace.salesapi.store.controller;

import com.marketplace.salesapi.product.dto.ProductDto;
import com.marketplace.salesapi.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stores/{storeId}/products")
@Tag(name = "Produtos da Loja", description = "API para gerenciamento de produtos por loja")
public class StoreProductsController {

    @Autowired
    private ProductService productService;

    @GetMapping
    @Operation(summary = "Listar produtos de uma loja")
    public ResponseEntity<Page<ProductDto>> getStoreProducts(
            @PathVariable Long storeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<ProductDto> products = productService.getProductsByStore(storeId, pageable);
        
        return ResponseEntity.ok(products);
    }
}