package com.marketplace.salesapi.order.repository;

import com.marketplace.salesapi.order.model.ShippingInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShippingInfoRepository extends JpaRepository<ShippingInfo, Long> {
    Optional<ShippingInfo> findByOrderId(Long orderId);
    
    Optional<ShippingInfo> findByTrackingNumber(String trackingNumber);
}