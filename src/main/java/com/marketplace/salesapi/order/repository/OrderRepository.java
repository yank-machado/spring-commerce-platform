package com.marketplace.salesapi.order.repository;

import com.marketplace.salesapi.order.model.Order;
import com.marketplace.salesapi.order.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    
    Page<Order> findByUserId(Long userId, Pageable pageable);
    
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    
    @Query("SELECT o FROM Order o JOIN o.items i JOIN i.product p WHERE p.store.id = :storeId")
    Page<Order> findByStoreId(@Param("storeId") Long storeId, Pageable pageable);
    
    @Query("SELECT o FROM Order o JOIN o.items i JOIN i.product p WHERE p.store.id = :storeId AND o.status = :status")
    Page<Order> findByStoreIdAndStatus(@Param("storeId") Long storeId, @Param("status") OrderStatus status, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}