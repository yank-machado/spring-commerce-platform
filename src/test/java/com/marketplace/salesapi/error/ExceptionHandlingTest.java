package com.marketplace.salesapi.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import com.marketplace.salesapi.exception.GlobalExceptionHandler;
import com.marketplace.salesapi.exception.UnauthorizedException;
import com.marketplace.salesapi.product.controller.ProductController;
import com.marketplace.salesapi.product.dto.CreateProductRequest;
import com.marketplace.salesapi.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import com.marketplace.salesapi.config.TestSecurityConfig;
import org.springframework.context.annotation.Import;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes para verificar o correto tratamento de exceções pelo GlobalExceptionHandler
 */
@WebMvcTest(controllers = {ProductController.class, GlobalExceptionHandler.class}, 
            properties = {"spring.main.allow-bean-definition-overriding=true"})
@Import(TestSecurityConfig.class)
class ExceptionHandlingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private CreateProductRequest invalidProductRequest;

    @BeforeEach
    void setUp() {
        // Criar um request de produto inválido para testar validação
        invalidProductRequest = new CreateProductRequest();
        // Deixar os campos obrigatórios vazios para gerar erros de validação
    }

    @Test
    @WithMockUser(roles = "SELLER")
    @DisplayName("GET /api/products/{id} should return 404 when product not found")
    void getProduct_ShouldReturn404_WhenProductNotFound() throws Exception {
        // Arrange
        when(productService.getProductById(anyLong()))
                .thenThrow(new EntityNotFoundException("Produto não encontrado com ID: 999"));

        // Act & Assert
        mockMvc.perform(get("/api/products/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Produto não encontrado com ID: 999"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("GET /api/products/{id} should return 403 when access unauthorized")
    void getProduct_ShouldReturn403_WhenAccessUnauthorized() throws Exception {
        // Arrange
        when(productService.getProductById(anyLong()))
                .thenThrow(new UnauthorizedException("Acesso não autorizado ao produto"));

        // Act & Assert
        mockMvc.perform(get("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Acesso não autorizado ao produto"));
    }

    @Test
    @WithMockUser(roles = "SELLER")
    @DisplayName("POST /api/products should return 400 with validation errors when request is invalid")
    void createProduct_ShouldReturn400WithValidationErrors_WhenRequestIsInvalid() throws Exception {
        // Mock request body with missing required fields
        String requestBody = "{\"name\":\"\",\"price\":-1}";
        
        // Act & Assert
        mockMvc.perform(post("/api/products")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @WithMockUser(roles = "SELLER")
    @DisplayName("POST /api/products should return 415 when content type is not JSON")
    void createProduct_ShouldReturn415_WhenContentTypeIsNotJson() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/products")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.TEXT_PLAIN)
                .content("Invalid content"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @WithMockUser(roles = "SELLER")
    @DisplayName("POST /api/products should return 400 when JSON is malformed")
    void createProduct_ShouldReturn400_WhenJsonIsMalformed() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/products")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest());
    }
} 