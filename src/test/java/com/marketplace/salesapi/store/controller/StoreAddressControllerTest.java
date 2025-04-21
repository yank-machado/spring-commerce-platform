package com.marketplace.salesapi.store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.salesapi.security.UserDetailsImpl;
import com.marketplace.salesapi.store.dto.CreateStoreAddressRequest;
import com.marketplace.salesapi.store.dto.StoreAddressDto;
import com.marketplace.salesapi.store.service.StoreAddressService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import com.marketplace.salesapi.config.TestSecurityConfig;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = StoreAddressController.class, 
            properties = {"spring.main.allow-bean-definition-overriding=true"})
@Import(TestSecurityConfig.class)
class StoreAddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StoreAddressService storeAddressService;

    private CreateStoreAddressRequest validAddressRequest;
    private CreateStoreAddressRequest invalidAddressRequest;
    private StoreAddressDto storeAddressDto;
    private MockUserDetails sellerUserDetails;
    private MockUserDetails adminUserDetails;
    private MockUserDetails customerUserDetails;

    // Mock UserDetails implementation
    static class MockUserDetails implements UserDetails {
        private Long id;
        private String username;
        private String password;
        private Collection<SimpleGrantedAuthority> authorities;
        
        public MockUserDetails(Long id, String username, String password, List<SimpleGrantedAuthority> authorities) {
            this.id = id;
            this.username = username;
            this.password = password;
            this.authorities = authorities;
        }
        
        public Long getId() { return id; }
        
        @Override
        public Collection<SimpleGrantedAuthority> getAuthorities() { return authorities; }
        
        @Override
        public String getPassword() { return password; }
        
        @Override
        public String getUsername() { return username; }
        
        @Override
        public boolean isAccountNonExpired() { return true; }
        
        @Override
        public boolean isAccountNonLocked() { return true; }
        
        @Override
        public boolean isCredentialsNonExpired() { return true; }
        
        @Override
        public boolean isEnabled() { return true; }
    }

    @BeforeEach
    void setUp() {
        // Criar dados válidos para o request
        validAddressRequest = new CreateStoreAddressRequest();
        validAddressRequest.setStreet("Rua Exemplo");
        validAddressRequest.setNumber("123");
        validAddressRequest.setNeighborhood("Centro");
        validAddressRequest.setCity("São Paulo");
        validAddressRequest.setState("SP");
        validAddressRequest.setZipCode("01001-000");
        validAddressRequest.setCountry("Brasil");
        validAddressRequest.setLatitude(-23.5505);
        validAddressRequest.setLongitude(-46.6333);

        // Criar dados inválidos para testar validação
        invalidAddressRequest = new CreateStoreAddressRequest();
        invalidAddressRequest.setStreet("");  // Campo obrigatório vazio
        invalidAddressRequest.setNumber("");  // Campo obrigatório vazio
        invalidAddressRequest.setCity("");    // Campo obrigatório vazio

        // Criar resposta esperada
        storeAddressDto = new StoreAddressDto();
        storeAddressDto.setId(1L);
        storeAddressDto.setStoreId(1L);
        storeAddressDto.setStreet(validAddressRequest.getStreet());
        storeAddressDto.setNumber(validAddressRequest.getNumber());
        storeAddressDto.setComplement(validAddressRequest.getComplement());
        storeAddressDto.setNeighborhood(validAddressRequest.getNeighborhood());
        storeAddressDto.setCity(validAddressRequest.getCity());
        storeAddressDto.setState(validAddressRequest.getState());
        storeAddressDto.setZipCode(validAddressRequest.getZipCode());
        storeAddressDto.setCountry(validAddressRequest.getCountry());
        storeAddressDto.setLatitude(validAddressRequest.getLatitude());
        storeAddressDto.setLongitude(validAddressRequest.getLongitude());

        // Criar usuários para testes de autorização
        List<SimpleGrantedAuthority> sellerAuthorities = new ArrayList<>();
        sellerAuthorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        sellerUserDetails = new MockUserDetails(
                1L,
                "seller@example.com",
                "password",
                sellerAuthorities
        );

        List<SimpleGrantedAuthority> adminAuthorities = new ArrayList<>();
        adminAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        adminUserDetails = new MockUserDetails(
                2L,
                "admin@example.com",
                "password",
                adminAuthorities
        );

        List<SimpleGrantedAuthority> customerAuthorities = new ArrayList<>();
        customerAuthorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        customerUserDetails = new MockUserDetails(
                3L,
                "customer@example.com",
                "password",
                customerAuthorities
        );
    }

    @Test
    @DisplayName("POST /api/stores/{storeId}/address should create address when seller is authorized")
    void createAddress_ShouldCreateAddress_WhenSellerIsAuthorized() throws Exception {
        // Arrange
        when(storeAddressService.createOrUpdateStoreAddress(anyLong(), any(CreateStoreAddressRequest.class), anyLong()))
                .thenReturn(storeAddressDto);

        // Act & Assert
        mockMvc.perform(post("/api/stores/1/address")
                .with(user(sellerUserDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAddressRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.storeId", is(1)))
                .andExpect(jsonPath("$.street", is("Rua Exemplo")));

        verify(storeAddressService).createOrUpdateStoreAddress(eq(1L), any(CreateStoreAddressRequest.class), eq(1L));
    }

    @Test
    @DisplayName("POST /api/stores/{storeId}/address should create address when admin is authorized")
    void createAddress_ShouldCreateAddress_WhenAdminIsAuthorized() throws Exception {
        // Arrange
        when(storeAddressService.createOrUpdateStoreAddress(anyLong(), any(CreateStoreAddressRequest.class), anyLong()))
                .thenReturn(storeAddressDto);

        // Act & Assert
        mockMvc.perform(post("/api/stores/1/address")
                .with(user(adminUserDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAddressRequest)))
                .andExpect(status().isCreated());

        verify(storeAddressService).createOrUpdateStoreAddress(eq(1L), any(CreateStoreAddressRequest.class), eq(2L));
    }

    @Test
    @DisplayName("POST /api/stores/{storeId}/address should return 403 when customer tries to create address")
    void createAddress_ShouldReturn403_WhenCustomerTriesToCreate() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/stores/1/address")
                .with(user(customerUserDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAddressRequest)))
                .andExpect(status().isForbidden());

        verify(storeAddressService, never()).createOrUpdateStoreAddress(anyLong(), any(CreateStoreAddressRequest.class), anyLong());
    }

    @Test
    @DisplayName("POST /api/stores/{storeId}/address should return 400 when request is invalid")
    @WithMockUser(roles = "SELLER")
    void createAddress_ShouldReturn400_WhenRequestIsInvalid() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/stores/1/address")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAddressRequest)))
                .andExpect(status().isBadRequest());

        verify(storeAddressService, never()).createOrUpdateStoreAddress(anyLong(), any(CreateStoreAddressRequest.class), anyLong());
    }

    @Test
    @DisplayName("POST /api/stores/{storeId}/address should return 415 when content type is not JSON")
    @WithMockUser(roles = "SELLER")
    void createAddress_ShouldReturn415_WhenContentTypeIsNotJson() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/stores/1/address")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.TEXT_PLAIN)
                .content("invalid content"))
                .andExpect(status().isUnsupportedMediaType());

        verify(storeAddressService, never()).createOrUpdateStoreAddress(anyLong(), any(CreateStoreAddressRequest.class), anyLong());
    }

    @Test
    @DisplayName("POST /api/stores/{storeId}/address should return 403 when CSRF token is missing")
    @WithMockUser(roles = "SELLER")
    void createAddress_ShouldReturn403_WhenCsrfTokenIsMissing() throws Exception {
        // Act & Assert - Note the missing CSRF token
        mockMvc.perform(post("/api/stores/1/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAddressRequest)))
                .andExpect(status().isForbidden());

        verify(storeAddressService, never()).createOrUpdateStoreAddress(anyLong(), any(CreateStoreAddressRequest.class), anyLong());
    }

    @Test
    @DisplayName("GET /api/stores/{storeId}/address should return address when it exists")
    void getAddress_ShouldReturnAddress_WhenItExists() throws Exception {
        // Arrange
        when(storeAddressService.getStoreAddressByStoreId(anyLong())).thenReturn(storeAddressDto);

        // Act & Assert
        mockMvc.perform(get("/api/stores/1/address")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.storeId", is(1)))
                .andExpect(jsonPath("$.street", is("Rua Exemplo")));

        verify(storeAddressService).getStoreAddressByStoreId(1L);
    }

    @Test
    @DisplayName("GET /api/stores/{storeId}/address should return 404 when address does not exist")
    void getAddress_ShouldReturn404_WhenAddressDoesNotExist() throws Exception {
        // Arrange
        when(storeAddressService.getStoreAddressByStoreId(anyLong())).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/stores/1/address")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(storeAddressService).getStoreAddressByStoreId(1L);
    }

    @Test
    @DisplayName("GET /api/stores/{storeId}/address should return 404 when EntityNotFoundException is thrown")
    void getAddress_ShouldReturn404_WhenEntityNotFoundExceptionIsThrown() throws Exception {
        // Arrange
        when(storeAddressService.getStoreAddressByStoreId(anyLong())).thenThrow(new EntityNotFoundException("Store not found"));

        // Act & Assert
        mockMvc.perform(get("/api/stores/1/address")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(storeAddressService).getStoreAddressByStoreId(1L);
    }
} 