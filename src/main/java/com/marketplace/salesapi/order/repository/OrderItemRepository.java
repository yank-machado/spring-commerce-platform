package com.marketplace.salesapi.order.repository;

import com.marketplace.salesapi.order.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);
    
    @Query("SELECT oi FROM OrderItem oi JOIN oi.product p WHERE p.id = :productId")
    List<OrderItem> findByProductId(@Param("productId") Long productId);
    
    @Query("SELECT oi FROM OrderItem oi JOIN oi.product p WHERE p.store.id = :storeId")
    List<OrderItem> findByStoreId(@Param("storeId") Long storeId);
}