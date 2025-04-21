package com.marketplace.salesapi.product.controller;

import com.marketplace.salesapi.product.dto.CategoryDto;
import com.marketplace.salesapi.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categorias", description = "API para gerenciamento de categorias de produtos")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Criar uma nova categoria")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        CategoryDto newCategory = categoryService.createCategory(categoryDto);
        return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter categoria por ID")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id) {
        CategoryDto category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping
    @Operation(summary = "Listar todas as categorias")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/root")
    @Operation(summary = "Listar categorias raiz (sem pai)")
    public ResponseEntity<List<CategoryDto>> getRootCategories() {
        List<CategoryDto> categories = categoryService.getRootCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{parentId}/subcategories")
    @Operation(summary = "Listar subcategorias de uma categoria")
    public ResponseEntity<List<CategoryDto>> getSubcategories(@PathVariable Long parentId) {
        List<CategoryDto> subcategories = categoryService.getSubcategories(parentId);
        return ResponseEntity.ok(subcategories);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma categoria")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDto categoryDto) {
        CategoryDto updatedCategory = categoryService.updateCategory(id, categoryDto);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir uma categoria")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}