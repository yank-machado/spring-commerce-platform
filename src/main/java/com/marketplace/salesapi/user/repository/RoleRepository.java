package com.marketplace.salesapi.user.repository;

import com.marketplace.salesapi.user.model.ERole;
import com.marketplace.salesapi.user.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}