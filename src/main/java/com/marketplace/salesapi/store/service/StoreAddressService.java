package com.marketplace.salesapi.store.service;

import com.marketplace.salesapi.store.dto.CreateStoreAddressRequest;
import com.marketplace.salesapi.store.dto.StoreAddressDto;
import com.marketplace.salesapi.store.model.Store;
import com.marketplace.salesapi.store.model.StoreAddress;
import com.marketplace.salesapi.store.repository.StoreAddressRepository;
import com.marketplace.salesapi.store.repository.StoreRepository;
import com.marketplace.salesapi.user.model.ERole;
import com.marketplace.salesapi.user.model.User;
import com.marketplace.salesapi.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoreAddressService {

    @Autowired
    private StoreAddressRepository storeAddressRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public StoreAddressDto createOrUpdateStoreAddress(Long storeId, CreateStoreAddressRequest request, Long userId) {
        // Buscar a loja
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Loja não encontrada com id: " + storeId));
        
        // Verificar se o usuário é o dono da loja ou um admin
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + userId));
        
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName() == ERole.ROLE_ADMIN);
        
        if (!store.getOwner().getId().equals(userId) && !isAdmin) {
            throw new AccessDeniedException("Você não tem permissão para atualizar o endereço desta loja");
        }
        
        // Verificar se já existe um endereço para esta loja
        StoreAddress storeAddress = storeAddressRepository.findByStoreId(storeId)
                .orElse(new StoreAddress());
        
        storeAddress.setStore(store);
        storeAddress.setStreet(request.getStreet());
        storeAddress.setNumber(request.getNumber());
        storeAddress.setComplement(request.getComplement());
        storeAddress.setNeighborhood(request.getNeighborhood());
        storeAddress.setCity(request.getCity());
        storeAddress.setState(request.getState());
        storeAddress.setZipCode(request.getZipCode());
        storeAddress.setCountry(request.getCountry());
        storeAddress.setLatitude(request.getLatitude());
        storeAddress.setLongitude(request.getLongitude());
        
        StoreAddress savedAddress = storeAddressRepository.save(storeAddress);
        
        return convertToDto(savedAddress);
    }

    public StoreAddressDto getStoreAddressByStoreId(Long storeId) {
        StoreAddress storeAddress = storeAddressRepository.findByStoreId(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Endereço não encontrado para a loja com id: " + storeId));
        
        return convertToDto(storeAddress);
    }

    private StoreAddressDto convertToDto(StoreAddress storeAddress) {
        StoreAddressDto dto = new StoreAddressDto();
        dto.setId(storeAddress.getId());
        dto.setStoreId(storeAddress.getStore().getId());
        dto.setStreet(storeAddress.getStreet());
        dto.setNumber(storeAddress.getNumber());
        dto.setComplement(storeAddress.getComplement());
        dto.setNeighborhood(storeAddress.getNeighborhood());
        dto.setCity(storeAddress.getCity());
        dto.setState(storeAddress.getState());
        dto.setZipCode(storeAddress.getZipCode());
        dto.setCountry(storeAddress.getCountry());
        dto.setLatitude(storeAddress.getLatitude());
        dto.setLongitude(storeAddress.getLongitude());
        
        return dto;
    }
}