package com.marketplace.salesapi.store.service;

import com.marketplace.salesapi.store.dto.CreateStoreRequest;
import com.marketplace.salesapi.store.dto.StoreDto;
import com.marketplace.salesapi.store.dto.UpdateStoreRequest;
import com.marketplace.salesapi.store.model.Store;
import com.marketplace.salesapi.store.model.StoreStatus;
import com.marketplace.salesapi.store.repository.StoreRepository;
import com.marketplace.salesapi.user.model.ERole;
import com.marketplace.salesapi.user.model.Role;
import com.marketplace.salesapi.user.model.User;
import com.marketplace.salesapi.user.repository.RoleRepository;
import com.marketplace.salesapi.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public StoreDto createStore(CreateStoreRequest request, Long userId) {
        // Verificar se o nome da loja já existe
        if (storeRepository.existsByName(request.getName())) {
            throw new RuntimeException("Nome de loja já está em uso!");
        }

        // Buscar o usuário
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + userId));

        // Adicionar role de vendedor ao usuário se ainda não tiver
        boolean isSellerAlready = owner.getRoles().stream()
                .anyMatch(role -> role.getName() == ERole.ROLE_SELLER);
        
        if (!isSellerAlready) {
            Role sellerRole = roleRepository.findByName(ERole.ROLE_SELLER)
                    .orElseThrow(() -> new RuntimeException("Role não encontrada: SELLER"));
            
            Set<Role> roles = new HashSet<>(owner.getRoles());
            roles.add(sellerRole);
            owner.setRoles(roles);
            userRepository.save(owner);
        }

        // Criar nova loja
        Store store = new Store();
        store.setName(request.getName());
        store.setDescription(request.getDescription());
        store.setLogoUrl(request.getLogoUrl());
        store.setBannerUrl(request.getBannerUrl());
        store.setOwner(owner);
        store.setStatus(StoreStatus.PENDING); // Lojas começam como pendentes até aprovação

        Store savedStore = storeRepository.save(store);
        
        return convertToDto(savedStore);
    }

    public StoreDto getStoreById(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Loja não encontrada com id: " + id));
        return convertToDto(store);
    }

    public List<StoreDto> getAllStores() {
        return storeRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<StoreDto> getStoresByOwnerId(Long ownerId) {
        return storeRepository.findByOwnerId(ownerId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<StoreDto> getStoresByStatus(StoreStatus status) {
        return storeRepository.findByStatus(status).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public StoreDto updateStore(Long id, UpdateStoreRequest request, Long userId) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Loja não encontrada com id: " + id));
        
        // Verificar se o usuário é o dono da loja ou um admin
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + userId));
        
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName() == ERole.ROLE_ADMIN);
        
        if (!store.getOwner().getId().equals(userId) && !isAdmin) {
            throw new AccessDeniedException("Você não tem permissão para atualizar esta loja");
        }
        
        // Atualizar campos
        if (request.getName() != null && !request.getName().isEmpty()) {
            // Verificar se o novo nome já existe (se for diferente do atual)
            if (!request.getName().equals(store.getName()) && storeRepository.existsByName(request.getName())) {
                throw new RuntimeException("Nome de loja já está em uso!");
            }
            store.setName(request.getName());
        }
        
        if (request.getDescription() != null) {
            store.setDescription(request.getDescription());
        }
        
        if (request.getLogoUrl() != null) {
            store.setLogoUrl(request.getLogoUrl());
        }
        
        if (request.getBannerUrl() != null) {
            store.setBannerUrl(request.getBannerUrl());
        }
        
        // Apenas admins podem alterar o status da loja
        if (request.getStatus() != null && isAdmin) {
            store.setStatus(request.getStatus());
        }
        
        Store updatedStore = storeRepository.save(store);
        return convertToDto(updatedStore);
    }

    @Transactional
    public void deleteStore(Long id, Long userId) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Loja não encontrada com id: " + id));
        
        // Verificar se o usuário é o dono da loja ou um admin
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + userId));
        
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName() == ERole.ROLE_ADMIN);
        
        if (!store.getOwner().getId().equals(userId) && !isAdmin) {
            throw new AccessDeniedException("Você não tem permissão para excluir esta loja");
        }
        
        storeRepository.delete(store);
    }

    private StoreDto convertToDto(Store store) {
        StoreDto dto = new StoreDto();
        dto.setId(store.getId());
        dto.setName(store.getName());
        dto.setDescription(store.getDescription());
        dto.setLogoUrl(store.getLogoUrl());
        dto.setBannerUrl(store.getBannerUrl());
        dto.setStatus(store.getStatus());
        dto.setOwnerId(store.getOwner().getId());
        dto.setOwnerName(store.getOwner().getName());
        dto.setCreatedAt(store.getCreatedAt());
        dto.setUpdatedAt(store.getUpdatedAt());
        
        return dto;
    }
}