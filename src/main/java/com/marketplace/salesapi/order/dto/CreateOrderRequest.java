package com.marketplace.salesapi.order.dto;

import com.marketplace.salesapi.order.model.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    @NotEmpty(message = "Itens do pedido são obrigatórios")
    private List<@Valid CreateOrderItemRequest> items;
    
    @NotNull
    @Valid
    private ShippingInfoRequest shippingInfo;
    
    @NotNull(message = "Método de pagamento é obrigatório")
    private PaymentMethod paymentMethod;
    
    private String paymentDetails;
    
    private String notes;
    
    public CreateOrderRequest() {
    }
    
    public List<CreateOrderItemRequest> getItems() {
        return items;
    }
    
    public void setItems(List<CreateOrderItemRequest> items) {
        this.items = items;
    }
    
    public ShippingInfoRequest getShippingInfo() {
        return shippingInfo;
    }
    
    public void setShippingInfo(ShippingInfoRequest shippingInfo) {
        this.shippingInfo = shippingInfo;
    }
    
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getPaymentDetails() {
        return paymentDetails;
    }
    
    public void setPaymentDetails(String paymentDetails) {
        this.paymentDetails = paymentDetails;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}