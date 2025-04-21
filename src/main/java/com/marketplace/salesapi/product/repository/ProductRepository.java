package com.marketplace.salesapi.product.repository;

import com.marketplace.salesapi.product.model.Product;
import com.marketplace.salesapi.product.model.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStoreId(Long storeId);
    
    Page<Product> findByStoreId(Long storeId, Pageable pageable);
    
    Page<Product> findByStatus(ProductStatus status, Pageable pageable);
    
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    
    Optional<Product> findBySku(String sku);
    
    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT p FROM Product p JOIN p.tags t WHERE t.name = :tagName")
    Page<Product> findByTagName(@Param("tagName") String tagName, Pageable pageable);
}