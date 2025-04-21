package com.marketplace.salesapi.product.service;

import com.marketplace.salesapi.product.dto.CategoryDto;
import com.marketplace.salesapi.product.dto.CreateProductRequest;
import com.marketplace.salesapi.product.dto.ProductDto;
import com.marketplace.salesapi.product.dto.ProductImageDto;
import com.marketplace.salesapi.product.model.*;
import com.marketplace.salesapi.product.repository.CategoryRepository;
import com.marketplace.salesapi.product.repository.ProductImageRepository;
import com.marketplace.salesapi.product.repository.ProductRepository;
import com.marketplace.salesapi.store.model.Store;
import com.marketplace.salesapi.store.repository.StoreRepository;
import com.marketplace.salesapi.user.model.ERole;
import com.marketplace.salesapi.user.model.User;
import com.marketplace.salesapi.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagService tagService;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Transactional
    public ProductDto createProduct(CreateProductRequest request, Long storeId, Long userId) {
        // Verificar se a loja existe
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Loja não encontrada com id: " + storeId));
        
        // Verificar se o usuário é o dono da loja ou um admin
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + userId));
        
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName() == ERole.ROLE_ADMIN);
        
        if (!store.getOwner().getId().equals(userId) && !isAdmin) {
            throw new AccessDeniedException("Você não tem permissão para criar produtos nesta loja");
        }
        
        // Criar novo produto
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setSku(request.getSku());
        product.setStore(store);
        
        // Definir categoria se fornecida
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com id: " + request.getCategoryId()));
            product.setCategory(category);
        }
        
        // Adicionar tags se fornecidas
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            Set<Tag> tags = tagService.getOrCreateTags(request.getTags());
            product.setTags(tags);
        }
        
        Product savedProduct = productRepository.save(product);
        
        return convertToDto(savedProduct);
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com id: " + id));
        return convertToDto(product);
    }

    public Page<ProductDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    public Page<ProductDto> getProductsByStore(Long storeId, Pageable pageable) {
        return productRepository.findByStoreId(storeId, pageable)
                .map(this::convertToDto);
    }

    public Page<ProductDto> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable)
                .map(this::convertToDto);
    }

    public Page<ProductDto> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchProducts(keyword, pageable)
                .map(this::convertToDto);
    }

    public Page<ProductDto> getProductsByTag(String tagName, Pageable pageable) {
        return productRepository.findByTagName(tagName, pageable)
                .map(this::convertToDto);
    }

    @Transactional
    public ProductDto updateProduct(Long id, CreateProductRequest request, Long userId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com id: " + id));
        
        // Verificar se o usuário é o dono da loja ou um admin
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + userId));
        
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName() == ERole.ROLE_ADMIN);
        
        if (!product.getStore().getOwner().getId().equals(userId) && !isAdmin) {
            throw new AccessDeniedException("Você não tem permissão para atualizar este produto");
        }
        
        // Atualizar campos
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setSku(request.getSku());
        
        // Atualizar categoria se fornecida
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com id: " + request.getCategoryId()));
            product.setCategory(category);
        } else {
            product.setCategory(null);
        }
        
        // Atualizar tags se fornecidas
        if (request.getTags() != null) {
            Set<Tag> tags = tagService.getOrCreateTags(request.getTags());
            product.setTags(tags);
        }
        
        Product updatedProduct = productRepository.save(product);
        return convertToDto(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id, Long userId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com id: " + id));
        
        // Verificar se o usuário é o dono da loja ou um admin
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + userId));
        
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName() == ERole.ROLE_ADMIN);
        
        if (!product.getStore().getOwner().getId().equals(userId) && !isAdmin) {
            throw new AccessDeniedException("Você não tem permissão para excluir este produto");
        }
        
        productRepository.delete(product);
    }

    @Transactional
    public void updateProductStatus(Long id, ProductStatus status, Long userId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com id: " + id));
        
        // Verificar se o usuário é o dono da loja ou um admin
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + userId));
        
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName() == ERole.ROLE_ADMIN);
        
        if (!product.getStore().getOwner().getId().equals(userId) && !isAdmin) {
            throw new AccessDeniedException("Você não tem permissão para atualizar o status deste produto");
        }
        
        product.setStatus(status);
        productRepository.save(product);
    }

    private ProductDto convertToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setDiscountPrice(product.getDiscountPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setSku(product.getSku());
        dto.setStatus(product.getStatus());
        dto.setStoreId(product.getStore().getId());
        dto.setStoreName(product.getStore().getName());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        
        // Set category
        if (product.getCategory() != null) {
            CategoryDto categoryDto = new CategoryDto();
            categoryDto.setId(product.getCategory().getId());
            categoryDto.setName(product.getCategory().getName());
            categoryDto.setDescription(product.getCategory().getDescription());
            categoryDto.setImageUrl(product.getCategory().getImageUrl());
            
            if (product.getCategory().getParent() != null) {
                categoryDto.setParentId(product.getCategory().getParent().getId());
                categoryDto.setParentName(product.getCategory().getParent().getName());
            }
            
            dto.setCategory(categoryDto);
        }
        
        // Set tags
        if (product.getTags() != null && !product.getTags().isEmpty()) {
            Set<String> tagNames = product.getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.toSet());
            dto.setTags(tagNames);
        }
        
        // Set images
        List<ProductImage> images = productImageRepository.findByProductId(product.getId());
        if (images != null && !images.isEmpty()) {
            List<ProductImageDto> imageDtos = images.stream()
                    .map(image -> {
                        ProductImageDto imageDto = new ProductImageDto();
                        imageDto.setId(image.getId());
                        imageDto.setImageUrl(image.getImageUrl());
                        imageDto.setIsPrimary(image.getIsPrimary());
                        imageDto.setDisplayOrder(image.getDisplayOrder());
                        return imageDto;
                    })
                    .collect(Collectors.toList());
            dto.setImages(imageDtos);
            
            // Set primary image URL
            Optional<ProductImage> primaryImage = images.stream()
                    .filter(ProductImage::getIsPrimary)
                    .findFirst();
            primaryImage.ifPresent(image -> dto.setPrimaryImageUrl(image.getImageUrl()));
        }
        
        return dto;
    }
}