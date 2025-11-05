package com.delivery_trujillo.app_trujillo_services.services.models.dtos;

public class ChatDTO {
    
    private Long orderId;
    private String subject;
    
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
}

