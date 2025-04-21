package com.marketplace.salesapi.product.service;

import com.marketplace.salesapi.product.dto.CategoryDto;
import com.marketplace.salesapi.product.model.Category;
import com.marketplace.salesapi.product.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        category.setImageUrl(categoryDto.getImageUrl());
        
        // Set parent category if provided
        if (categoryDto.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryDto.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoria pai não encontrada com id: " + categoryDto.getParentId()));
            category.setParent(parent);
        }
        
        Category savedCategory = categoryRepository.save(category);
        return convertToDto(savedCategory);
    }

    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com id: " + id));
        return convertToDto(category);
    }

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<CategoryDto> getRootCategories() {
        return categoryRepository.findByParentIsNull().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<CategoryDto> getSubcategories(Long parentId) {
        return categoryRepository.findByParentId(parentId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com id: " + id));
        
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        category.setImageUrl(categoryDto.getImageUrl());
        
        // Update parent category if provided
        if (categoryDto.getParentId() != null) {
            // Prevent circular reference
            if (categoryDto.getParentId().equals(id)) {
                throw new IllegalArgumentException("Uma categoria não pode ser pai dela mesma");
            }
            
            Category parent = categoryRepository.findById(categoryDto.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoria pai não encontrada com id: " + categoryDto.getParentId()));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }
        
        Category updatedCategory = categoryRepository.save(category);
        return convertToDto(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com id: " + id));
        
        // Move subcategories to parent category or make them root categories
        if (!category.getSubcategories().isEmpty()) {
            Category parent = category.getParent();
            for (Category subcategory : category.getSubcategories()) {
                subcategory.setParent(parent);
                categoryRepository.save(subcategory);
            }
        }
        
        categoryRepository.delete(category);
    }

    private CategoryDto convertToDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setImageUrl(category.getImageUrl());
        
        if (category.getParent() != null) {
            dto.setParentId(category.getParent().getId());
            dto.setParentName(category.getParent().getName());
        }
        
        // Convert subcategories (without recursively loading their subcategories to avoid infinite loops)
        if (category.getSubcategories() != null && !category.getSubcategories().isEmpty()) {
            List<CategoryDto> subcategoryDtos = new ArrayList<>();
            for (Category subcategory : category.getSubcategories()) {
                CategoryDto subcategoryDto = new CategoryDto();
                subcategoryDto.setId(subcategory.getId());
                subcategoryDto.setName(subcategory.getName());
                subcategoryDto.setDescription(subcategory.getDescription());
                subcategoryDto.setImageUrl(subcategory.getImageUrl());
                subcategoryDto.setParentId(category.getId());
                subcategoryDto.setParentName(category.getName());
                subcategoryDtos.add(subcategoryDto);
            }
            dto.setSubcategories(subcategoryDtos);
        }
        
        return dto;
    }
}