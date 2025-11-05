package com.delivery_trujillo.app_trujillo_services.services.models.dtos;

import java.math.BigDecimal;

public class CartItemDTO {
    
    private Long menuItemId;
    private Integer quantity;
    private String customizationNotes;
    
    public Long getMenuItemId() {
        return menuItemId;
    }
    
    public void setMenuItemId(Long menuItemId) {
        this.menuItemId = menuItemId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public String getCustomizationNotes() {
        return customizationNotes;
    }
    
    public void setCustomizationNotes(String customizationNotes) {
        this.customizationNotes = customizationNotes;
    }
}

