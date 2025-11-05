package com.delivery_trujillo.app_trujillo_services.services.models.dtos;

import java.time.LocalDateTime;

public class CheckoutDTO {
    
    private Long addressId;
    private Long paymentMethodId;
    private String notes;
    private LocalDateTime scheduledDeliveryTime; // Opcional para pedidos programados
    
    public Long getAddressId() {
        return addressId;
    }
    
    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }
    
    public Long getPaymentMethodId() {
        return paymentMethodId;
    }
    
    public void setPaymentMethodId(Long paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getScheduledDeliveryTime() {
        return scheduledDeliveryTime;
    }
    
    public void setScheduledDeliveryTime(LocalDateTime scheduledDeliveryTime) {
        this.scheduledDeliveryTime = scheduledDeliveryTime;
    }
}

