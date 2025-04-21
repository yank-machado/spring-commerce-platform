package com.marketplace.salesapi.store.repository;

import com.marketplace.salesapi.store.model.Store;
import com.marketplace.salesapi.store.model.StoreStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByName(String name);
    Boolean existsByName(String name);
    List<Store> findByOwnerId(Long ownerId);
    List<Store> findByStatus(StoreStatus status);
}