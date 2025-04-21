package com.marketplace.salesapi.product.repository;

import com.marketplace.salesapi.config.TestRepositoryConfig;
import com.marketplace.salesapi.product.model.Category;
import com.marketplace.salesapi.product.model.Product;
import com.marketplace.salesapi.product.model.ProductStatus;
import com.marketplace.salesapi.store.model.Store;
import com.marketplace.salesapi.store.model.StoreStatus;
import com.marketplace.salesapi.store.repository.StoreRepository;
import com.marketplace.salesapi.user.model.ERole;
import com.marketplace.salesapi.user.model.Role;
import com.marketplace.salesapi.user.model.User;
import com.marketplace.salesapi.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestRepositoryConfig.class)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User sellerUser;
    private Store store;
    private Category category;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        // Create a seller user
        sellerUser = new User();
        sellerUser.setName("Seller User");
        sellerUser.setEmail("seller@example.com");
        sellerUser.setPassword("password");
        sellerUser.setPhoneNumber("11999999999");
        
        Set<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setName(ERole.ROLE_SELLER);
        roles.add(role);
        sellerUser.setRoles(roles);
        
        userRepository.save(sellerUser);

        // Create a store
        store = new Store();
        store.setName("Test Store");
        store.setDescription("Store for testing");
        store.setStatus(StoreStatus.ACTIVE);
        store.setOwner(sellerUser);
        
        storeRepository.save(store);

        // Create a category
        category = new Category();
        category.setName("Electronics");
        category.setDescription("Electronic products");
        
        categoryRepository.save(category);

        // Create products
        product1 = new Product();
        product1.setName("Test Product 1");
        product1.setDescription("First test product");
        product1.setSku("TEST-SKU-001");
        product1.setPrice(new BigDecimal("99.99"));
        product1.setStockQuantity(100);
        product1.setStatus(ProductStatus.ACTIVE);
        product1.setStore(store);
        product1.setCategory(category);
        
        product2 = new Product();
        product2.setName("Test Product 2");
        product2.setDescription("Second test product with different category");
        product2.setSku("TEST-SKU-002");
        product2.setPrice(new BigDecimal("149.99"));
        product2.setStockQuantity(50);
        product2.setStatus(ProductStatus.ACTIVE);
        product2.setStore(store);
        
        productRepository.saveAll(List.of(product1, product2));
    }

    @Test
    @DisplayName("Should find products by store ID")
    void findByStoreId_ShouldReturnProductsFromStore() {
        // Act
        List<Product> products = productRepository.findByStoreId(store.getId());

        // Assert
        assertEquals(2, products.size());
        assertTrue(products.contains(product1));
        assertTrue(products.contains(product2));
    }

    @Test
    @DisplayName("Should find products by store ID with pagination")
    void findByStoreId_WithPagination_ShouldReturnPageOfProducts() {
        // Act
        Page<Product> productPage = productRepository.findByStoreId(
                store.getId(), PageRequest.of(0, 1));

        // Assert
        assertEquals(1, productPage.getContent().size());
        assertEquals(2, productPage.getTotalElements());
        assertEquals(2, productPage.getTotalPages());
    }

    @Test
    @DisplayName("Should find products by category ID")
    void findByCategoryId_ShouldReturnProductsInCategory() {
        // Act
        Page<Product> productPage = productRepository.findByCategoryId(
                category.getId(), PageRequest.of(0, 10));

        // Assert
        assertEquals(1, productPage.getTotalElements());
        assertEquals(product1.getId(), productPage.getContent().get(0).getId());
    }

    @Test
    @DisplayName("Should find product by SKU")
    void findBySku_ShouldReturnProduct_WhenSkuExists() {
        // Act
        Optional<Product> foundProduct = productRepository.findBySku("TEST-SKU-001");

        // Assert
        assertTrue(foundProduct.isPresent());
        assertEquals(product1.getId(), foundProduct.get().getId());
    }

    @Test
    @DisplayName("Should return empty when searching for non-existent SKU")
    void findBySku_ShouldReturnEmpty_WhenSkuDoesNotExist() {
        // Act
        Optional<Product> foundProduct = productRepository.findBySku("NON-EXISTENT-SKU");

        // Assert
        assertFalse(foundProduct.isPresent());
    }

    @Test
    @DisplayName("Should search products by keyword in name or description")
    void searchProducts_ShouldReturnMatchingProducts() {
        // Act - Search in name
        Page<Product> nameResults = productRepository.searchProducts(
                "Product 1", PageRequest.of(0, 10));
        
        // Act - Search in description
        Page<Product> descriptionResults = productRepository.searchProducts(
                "different category", PageRequest.of(0, 10));

        // Assert
        assertEquals(1, nameResults.getTotalElements());
        assertEquals(product1.getId(), nameResults.getContent().get(0).getId());
        
        assertEquals(1, descriptionResults.getTotalElements());
        assertEquals(product2.getId(), descriptionResults.getContent().get(0).getId());
    }
} 