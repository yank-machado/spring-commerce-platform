package com.marketplace.salesapi.config;

import com.marketplace.salesapi.user.model.ERole;
import com.marketplace.salesapi.user.model.Role;
import com.marketplace.salesapi.user.model.User;
import com.marketplace.salesapi.user.model.UserStatus;
import com.marketplace.salesapi.user.repository.RoleRepository;
import com.marketplace.salesapi.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Configuration
public class TestDataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initTestData() {
        return args -> {
            // Verifica se o usuário de teste já existe
            if (userRepository.count() == 0) {
                System.out.println("Criando usuário de teste para desenvolvimento...");
                
                // Obtém ou cria as roles
                Role customerRole = getRoleByName(ERole.ROLE_CUSTOMER);
                Role sellerRole = getRoleByName(ERole.ROLE_SELLER);
                Role adminRole = getRoleByName(ERole.ROLE_ADMIN);
                
                // Cria conjunto de roles para o usuário
                Set<Role> roles = new HashSet<>();
                roles.add(customerRole);
                roles.add(sellerRole);
                roles.add(adminRole);
                
                // Cria o usuário de teste
                User testUser = new User();
                testUser.setName("Usuário de Teste");
                testUser.setEmail("teste@example.com");
                testUser.setPassword(passwordEncoder.encode("senha123"));
                testUser.setPhoneNumber("123456789");
                testUser.setStatus(UserStatus.ACTIVE);
                testUser.setRoles(roles);
                
                // Salva o usuário
                userRepository.save(testUser);
                
                System.out.println("Usuário de teste criado com ID: " + testUser.getId());
            } else {
                System.out.println("Usuários já existem no banco de dados.");
            }
        };
    }
    
    private Role getRoleByName(ERole name) {
        Optional<Role> roleOpt = roleRepository.findByName(name);
        return roleOpt.orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName(name);
            return roleRepository.save(newRole);
        });
    }
} 