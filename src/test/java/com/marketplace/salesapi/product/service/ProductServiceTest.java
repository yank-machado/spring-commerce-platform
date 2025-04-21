package com.marketplace.salesapi.product.service;

import com.marketplace.salesapi.exception.ResourceNotFoundException;
import com.marketplace.salesapi.product.dto.ProductDto;
import com.marketplace.salesapi.product.model.Product;
import com.marketplace.salesapi.product.model.ProductStatus;
import com.marketplace.salesapi.product.repository.ProductRepository;
import com.marketplace.salesapi.store.model.Store;
import com.marketplace.salesapi.store.repository.StoreRepository;
import com.marketplace.salesapi.user.model.ERole;
import com.marketplace.salesapi.user.model.User;
import com.marketplace.salesapi.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private ProductService productService;

    private User seller;
    private Store store;
    private Product product;

    @BeforeEach
    void setUp() {
        seller = TestUtil.createTestUser("seller@example.com", ERole.ROLE_SELLER);
        seller.setId(1L);

        store = TestUtil.createTestStore(seller);
        store.setId(1L);

        product = TestUtil.createTestProduct(store);
        product.setId(1L);
    }

    @Test
    @DisplayName("Should return product by ID when it exists")
    void getProductById_ShouldReturnProduct_WhenProductExists() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        // Act
        ProductDto result = productService.getProductById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(product.getId(), result.getId());
        assertEquals(product.getName(), result.getName());
        verify(productRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when getting product by non-existent ID")
    void getProductById_ShouldThrowException_WhenProductDoesNotExist() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));
        verify(productRepository).findById(99L);
    }

    @Test
    @DisplayName("Should get all products paginated")
    void getAllProducts_ShouldReturnPageOfProducts() {
        // Arrange
        Page<Product> productPage = new PageImpl<>(List.of(product));
        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);

        // Act
        Page<ProductDto> result = productService.getAllProducts(Pageable.unpaged());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should find products by category")
    void getProductsByCategory_ShouldReturnProductsInCategory() {
        // Arrange
        Page<Product> productPage = new PageImpl<>(List.of(product));
        when(productRepository.findByCategoryId(anyLong(), any(Pageable.class))).thenReturn(productPage);

        // Act
        Page<ProductDto> result = productService.getProductsByCategory(1L, Pageable.unpaged());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productRepository).findByCategoryId(eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("Should search products by keyword")
    void searchProducts_ShouldReturnMatchingProducts() {
        // Arrange
        Page<Product> productPage = new PageImpl<>(List.of(product));
        when(productRepository.searchProducts(anyString(), any(Pageable.class))).thenReturn(productPage);

        // Act
        Page<ProductDto> result = productService.searchProducts("test", Pageable.unpaged());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productRepository).searchProducts(eq("test"), any(Pageable.class));
    }
} 