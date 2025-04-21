package com.marketplace.salesapi.product.service;

import com.marketplace.salesapi.product.dto.ProductImageDto;
import com.marketplace.salesapi.product.model.Product;
import com.marketplace.salesapi.product.model.ProductImage;
import com.marketplace.salesapi.product.repository.ProductImageRepository;
import com.marketplace.salesapi.product.repository.ProductRepository;
import com.marketplace.salesapi.user.model.ERole;
import com.marketplace.salesapi.user.model.User;
import com.marketplace.salesapi.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductImageService {

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ProductImageDto addProductImage(Long productId, String imageUrl, Boolean isPrimary, Integer displayOrder, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com id: " + productId));
        
        // Verificar se o usuário é o dono da loja ou um admin
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + userId));
        
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName() == ERole.ROLE_ADMIN);
        
        if (!product.getStore().getOwner().getId().equals(userId) && !isAdmin) {
            throw new AccessDeniedException("Você não tem permissão para adicionar imagens a este produto");
        }
        
        ProductImage productImage = new ProductImage();
        productImage.setProduct(product);
        productImage.setImageUrl(imageUrl);
        productImage.setIsPrimary(isPrimary != null ? isPrimary : false);
        productImage.setDisplayOrder(displayOrder != null ? displayOrder : 0);
        
        // Se esta imagem for definida como primária, remover a flag de outras imagens
        if (Boolean.TRUE.equals(productImage.getIsPrimary())) {
            List<ProductImage> existingImages = productImageRepository.findByProductId(productId);
            for (ProductImage existingImage : existingImages) {
                if (Boolean.TRUE.equals(existingImage.getIsPrimary())) {
                    existingImage.setIsPrimary(false);
                    productImageRepository.save(existingImage);
                }
            }
        }
        
        ProductImage savedImage = productImageRepository.save(productImage);
        
        return convertToDto(savedImage);
    }

    public List<ProductImageDto> getProductImages(Long productId) {
        List<ProductImage> images = productImageRepository.findByProductId(productId);
        return images.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteProductImage(Long imageId, Long userId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Imagem não encontrada com id: " + imageId));
        
        // Verificar se o usuário é o dono da loja ou um admin
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + userId));
        
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName() == ERole.ROLE_ADMIN);
        
        if (!image.getProduct().getStore().getOwner().getId().equals(userId) && !isAdmin) {
            throw new AccessDeniedException("Você não tem permissão para excluir esta imagem");
        }
        
        // Se esta for a imagem primária, definir outra imagem como primária se existir
        if (Boolean.TRUE.equals(image.getIsPrimary())) {
            List<ProductImage> otherImages = productImageRepository.findByProductId(image.getProduct().getId()).stream()
                    .filter(img -> !img.getId().equals(imageId))
                    .collect(Collectors.toList());
            
            if (!otherImages.isEmpty()) {
                ProductImage newPrimary = otherImages.get(0);
                newPrimary.setIsPrimary(true);
                productImageRepository.save(newPrimary);
            }
        }
        
        productImageRepository.delete(image);
    }

    @Transactional
    public ProductImageDto setPrimaryImage(Long imageId, Long userId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Imagem não encontrada com id: " + imageId));
        
        // Verificar se o usuário é o dono da loja ou um admin
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + userId));
        
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName() == ERole.ROLE_ADMIN);
        
        if (!image.getProduct().getStore().getOwner().getId().equals(userId) && !isAdmin) {
            throw new AccessDeniedException("Você não tem permissão para modificar esta imagem");
        }
        
        // Remover a flag de imagem primária de outras imagens
        List<ProductImage> otherImages = productImageRepository.findByProductId(image.getProduct().getId()).stream()
                .filter(img -> !img.getId().equals(imageId) && Boolean.TRUE.equals(img.getIsPrimary()))
                .collect(Collectors.toList());
        
        for (ProductImage otherImage : otherImages) {
            otherImage.setIsPrimary(false);
            productImageRepository.save(otherImage);
        }
        
        // Definir esta imagem como primária
        image.setIsPrimary(true);
        ProductImage updatedImage = productImageRepository.save(image);
        
        return convertToDto(updatedImage);
    }

    private ProductImageDto convertToDto(ProductImage image) {
        ProductImageDto dto = new ProductImageDto();
        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setIsPrimary(image.getIsPrimary());
        dto.setDisplayOrder(image.getDisplayOrder());
        return dto;
    }
}