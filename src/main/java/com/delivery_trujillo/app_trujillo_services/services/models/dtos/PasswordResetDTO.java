package com.delivery_trujillo.app_trujillo_services.services.models.dtos;

public class PasswordResetDTO {
    
    private String token;
    private String newPassword;
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getNewPassword() {
        return newPassword;
    }
    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}

