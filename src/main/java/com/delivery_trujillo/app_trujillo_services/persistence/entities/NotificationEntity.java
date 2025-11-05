package com.delivery_trujillo.app_trujillo_services.persistence.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad para notificaciones push (RF-19)
 */
@Entity
@Table(name = "notifications")
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false, length = 100)
    private String title; // Título de la notificación

    @Column(nullable = false, length = 500)
    private String message; // Mensaje de la notificación

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private NotificationType type; // Tipo de notificación

    @Column(nullable = false)
    private Boolean isRead = false;

    @Column(length = 500)
    private String actionUrl; // URL de acción (opcional)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity relatedOrder; // Pedido relacionado (opcional)

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Enum para tipos de notificación
    public enum NotificationType {
        ORDER_STATUS_CHANGE,    // Cambio de estado del pedido
        ORDER_CONFIRMED,        // Pedido confirmado
        ORDER_READY,            // Pedido listo
        ORDER_ON_THE_WAY,       // Pedido en camino
        ORDER_DELIVERED,        // Pedido entregado
        PROMOTION,              // Promoción especial
        REMINDER,               // Recordatorio
        SYSTEM                  // Notificación del sistema
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public OrderEntity getRelatedOrder() {
        return relatedOrder;
    }

    public void setRelatedOrder(OrderEntity relatedOrder) {
        this.relatedOrder = relatedOrder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

