package com.marketplace.salesapi.store.repository;

import com.marketplace.salesapi.store.model.StoreAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreAddressRepository extends JpaRepository<StoreAddress, Long> {
    Optional<StoreAddress> findByStoreId(Long storeId);
}