package com.delivery_trujillo.app_trujillo_services.services.models.enums;

public enum Role {
    CUSTOMER("Cliente"),
    DELIVERY("Repartidor"),
    SUPPORT("Personal de Soporte"),
    OWNER("Dueño"),
    RESTAURANT("Restaurante");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
