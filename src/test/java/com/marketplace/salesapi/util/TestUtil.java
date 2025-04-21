package com.marketplace.salesapi.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.salesapi.order.model.Order;
import com.marketplace.salesapi.order.model.OrderStatus;
import com.marketplace.salesapi.product.model.Product;
import com.marketplace.salesapi.product.model.ProductStatus;
import com.marketplace.salesapi.store.model.Store;
import com.marketplace.salesapi.store.model.StoreStatus;
import com.marketplace.salesapi.user.model.ERole;
import com.marketplace.salesapi.user.model.Role;
import com.marketplace.salesapi.user.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class TestUtil {

    public static final ObjectMapper objectMapper = new ObjectMapper();

    // User creation helpers
    public static User createTestUser(String email, ERole role) {
        User user = new User();
        user.setName("Test User");
        user.setEmail(email);
        user.setPassword("$2a$10$eDhbhXVVXfZbAJ.1Xm6o/Os9z.HPBYmykxLYsSthZD5JQWMqEUvAe"); // hashed "password"
        user.setPhoneNumber("11999999999");
        
        Set<Role> roles = new HashSet<>();
        Role roleObj = new Role();
        roleObj.setName(role);
        roles.add(roleObj);
        user.setRoles(roles);
        
        return user;
    }
    
    // Store creation helper
    public static Store createTestStore(User owner) {
        Store store = new Store();
        store.setName("Test Store");
        store.setDescription("Store for testing");
        store.setStatus(StoreStatus.ACTIVE);
        store.setOwner(owner);
        store.setLogoUrl("https://example.com/logo.png");
        store.setBannerUrl("https://example.com/banner.jpg");
        store.setCreatedAt(LocalDateTime.now());
        store.setUpdatedAt(LocalDateTime.now());
        return store;
    }
    
    // Product creation helper
    public static Product createTestProduct(Store store) {
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Product for testing");
        product.setSku("TEST-SKU-123");
        product.setPrice(new BigDecimal("99.99"));
        product.setStockQuantity(100);
        product.setStatus(ProductStatus.ACTIVE);
        product.setStore(store);
        return product;
    }
    
    // Order creation helper
    public static Order createTestOrder(User user) {
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setSubtotal(new BigDecimal("199.98"));
        order.setTotal(new BigDecimal("214.98"));
        order.setShippingCost(new BigDecimal("15.00"));
        return order;
    }
} 