package com.marketplace.salesapi.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateShippingInfoRequest {
    @NotBlank(message = "Nome do destinatário é obrigatório")
    private String recipientName;
    
    @NotBlank(message = "Rua é obrigatória")
    private String street;
    
    @NotBlank(message = "Número é obrigatório")
    private String number;
    
    private String complement;
    
    @NotBlank(message = "Bairro é obrigatório")
    private String neighborhood;
    
    @NotBlank(message = "Cidade é obrigatória")
    private String city;
    
    @NotBlank(message = "Estado é obrigatório")
    private String state;
    
    @NotBlank(message = "CEP é obrigatório")
    private String zipCode;
    
    @NotBlank(message = "País é obrigatório")
    private String country;
    
    @NotBlank(message = "Telefone é obrigatório")
    private String phoneNumber;
    
    @NotBlank(message = "Método de envio é obrigatório")
    private String shippingMethod;
}