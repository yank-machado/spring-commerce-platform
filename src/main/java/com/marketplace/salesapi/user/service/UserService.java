package com.marketplace.salesapi.user.service;

import com.marketplace.salesapi.user.dto.CreateUserRequest;
import com.marketplace.salesapi.user.dto.UserDto;
import com.marketplace.salesapi.user.model.ERole;
import com.marketplace.salesapi.user.model.Role;
import com.marketplace.salesapi.user.model.User;
import com.marketplace.salesapi.user.repository.RoleRepository;
import com.marketplace.salesapi.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        // Verificar se o email já existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já está em uso!");
        }

        // Criar novo usuário
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());

        // Atribuir roles
        Set<Role> roles = new HashSet<>();
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            // Se nenhuma role for especificada, atribui ROLE_CUSTOMER por padrão
            Role customerRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                    .orElseThrow(() -> new RuntimeException("Role não encontrada: CUSTOMER"));
            roles.add(customerRole);
        } else {
            request.getRoles().forEach(roleName -> {
                switch (roleName.toUpperCase()) {
                    case "ADMIN" -> {
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Role não encontrada: ADMIN"));
                        roles.add(adminRole);
                    }
                    case "SELLER" -> {
                        Role sellerRole = roleRepository.findByName(ERole.ROLE_SELLER)
                                .orElseThrow(() -> new RuntimeException("Role não encontrada: SELLER"));
                        roles.add(sellerRole);
                    }
                    default -> {
                        Role customerRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                                .orElseThrow(() -> new RuntimeException("Role não encontrada: CUSTOMER"));
                        roles.add(customerRole);
                    }
                }
            });
        }

        user.setRoles(roles);
        User savedUser = userRepository.save(user);
        
        return convertToDto(savedUser);
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + id));
        return convertToDto(user);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto updateUser(Long id, CreateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + id));
        
        user.setName(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());
        
        // Atualizar senha apenas se fornecida
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        // Atualizar roles se fornecidas
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            request.getRoles().forEach(roleName -> {
                switch (roleName.toUpperCase()) {
                    case "ADMIN" -> {
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Role não encontrada: ADMIN"));
                        roles.add(adminRole);
                    }
                    case "SELLER" -> {
                        Role sellerRole = roleRepository.findByName(ERole.ROLE_SELLER)
                                .orElseThrow(() -> new RuntimeException("Role não encontrada: SELLER"));
                        roles.add(sellerRole);
                    }
                    default -> {
                        Role customerRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                                .orElseThrow(() -> new RuntimeException("Role não encontrada: CUSTOMER"));
                        roles.add(customerRole);
                    }
                }
            });
            user.setRoles(roles);
        }
        
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuário não encontrado com id: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setStatus(user.getStatus());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
        dto.setRoles(roles);
        
        return dto;
    }
}