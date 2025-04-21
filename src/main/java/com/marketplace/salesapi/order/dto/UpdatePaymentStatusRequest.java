package com.marketplace.salesapi.order.dto;

import com.marketplace.salesapi.order.model.PaymentStatus;
import jakarta.validation.constraints.NotNull;

public class UpdatePaymentStatusRequest {
    @NotNull(message = "Status de pagamento é obrigatório")
    private PaymentStatus paymentStatus;
    
    private String transactionId;
    
    private String paymentDetails;
    
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public String getPaymentDetails() {
        return paymentDetails;
    }
    
    public void setPaymentDetails(String paymentDetails) {
        this.paymentDetails = paymentDetails;
    }
}