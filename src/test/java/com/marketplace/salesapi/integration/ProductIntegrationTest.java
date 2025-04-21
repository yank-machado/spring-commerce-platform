package com.marketplace.salesapi.integration;

import com.marketplace.salesapi.BaseIntegrationTest;
import com.marketplace.salesapi.product.dto.ProductDto;
import com.marketplace.salesapi.product.model.Category;
import com.marketplace.salesapi.product.model.Product;
import com.marketplace.salesapi.product.model.ProductStatus;
import com.marketplace.salesapi.product.repository.CategoryRepository;
import com.marketplace.salesapi.product.repository.ProductRepository;
import com.marketplace.salesapi.store.model.Store;
import com.marketplace.salesapi.store.model.StoreStatus;
import com.marketplace.salesapi.store.repository.StoreRepository;
import com.marketplace.salesapi.user.model.ERole;
import com.marketplace.salesapi.user.model.Role;
import com.marketplace.salesapi.user.model.User;
import com.marketplace.salesapi.user.repository.RoleRepository;
import com.marketplace.salesapi.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
class ProductIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User sellerUser;
    private Store store;
    private Category category;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        // Create roles if not exist
        if (roleRepository.findByName(ERole.ROLE_SELLER).isEmpty()) {
            Role sellerRole = new Role();
            sellerRole.setName(ERole.ROLE_SELLER);
            roleRepository.save(sellerRole);
        }

        // Create a seller user
        sellerUser = new User();
        sellerUser.setName("Integration Test Seller");
        sellerUser.setEmail("integration-seller@example.com");
        sellerUser.setPassword(passwordEncoder.encode("password123"));
        sellerUser.setPhoneNumber("11999999999");
        
        Role sellerRole = roleRepository.findByName(ERole.ROLE_SELLER)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        
        Set<Role> roles = new HashSet<>();
        roles.add(sellerRole);
        sellerUser.setRoles(roles);
        
        userRepository.save(sellerUser);

        // Create a store
        store = new Store();
        store.setName("Integration Test Store");
        store.setDescription("Store for integration testing");
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
        product1.setName("Integration Test Product 1");
        product1.setDescription("First test product");
        product1.setSku("INT-TEST-001");
        product1.setPrice(new BigDecimal("149.99"));
        product1.setStockQuantity(100);
        product1.setStatus(ProductStatus.ACTIVE);
        product1.setStore(store);
        product1.setCategory(category);
        
        product2 = new Product();
        product2.setName("Integration Test Product 2");
        product2.setDescription("Second test product");
        product2.setSku("INT-TEST-002");
        product2.setPrice(new BigDecimal("249.99"));
        product2.setStockQuantity(50);
        product2.setStatus(ProductStatus.ACTIVE);
        product2.setStore(store);
        product2.setCategory(category);
        
        productRepository.save(product1);
        productRepository.save(product2);
    }

    @Test
    @DisplayName("GET /api/products should return all products")
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void getAllProducts_ShouldReturnAllProducts() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name").exists())
                .andExpect(jsonPath("$.content[0].price").exists());
    }

    @Test
    @DisplayName("GET /api/products/{id} should return product by ID")
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void getProductById_ShouldReturnProduct() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/products/" + product1.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(product1.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Integration Test Product 1")))
                .andExpect(jsonPath("$.price", is(149.99)));
    }

    @Test
    @DisplayName("GET /api/products/category/{id} should return products by category")
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void getProductsByCategory_ShouldReturnProductsInCategory() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/products/category/" + category.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].categoryId", is(category.getId().intValue())));
    }

    @Test
    @DisplayName("GET /api/products/search should return products matching search query")
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void searchProducts_ShouldReturnMatchingProducts() throws Exception {
        // Act & Assert - Search by name
        mockMvc.perform(get("/api/products/search")
                .param("keyword", "Product 1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Integration Test Product 1")));
    }

    @Test
    @DisplayName("GET /api/products/store/{id} should return store products")
    @WithMockUser(username = "integration-seller@example.com", roles = "SELLER")
    void getProductsByStore_ShouldReturnStoreProducts() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/products/store/" + store.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].storeId", is(store.getId().intValue())));
    }
} 