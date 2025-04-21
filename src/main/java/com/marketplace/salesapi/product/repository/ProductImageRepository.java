package com.marketplace.salesapi.product.repository;

import com.marketplace.salesapi.product.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductId(Long productId);
    Optional<ProductImage> findByProductIdAndIsPrimaryTrue(Long productId);
}