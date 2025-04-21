package com.marketplace.salesapi.order.repository;

import com.marketplace.salesapi.order.model.OrderPayment;
import com.marketplace.salesapi.order.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderPaymentRepository extends JpaRepository<OrderPayment, Long> {
    Optional<OrderPayment> findByOrderId(Long orderId);
    
    List<OrderPayment> findByPaymentStatus(PaymentStatus status);
    
    Optional<OrderPayment> findByTransactionId(String transactionId);
}