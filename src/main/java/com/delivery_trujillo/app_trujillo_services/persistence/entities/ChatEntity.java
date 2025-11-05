package com.delivery_trujillo.app_trujillo_services.persistence.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad para chats de soporte al cliente (RF-21)
 */
@Entity
@Table(name = "chats")
public class ChatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user; // Cliente que inicia el chat

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "support_user_id")
    private UserEntity supportUser; // Personal de soporte asignado

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity relatedOrder; // Pedido relacionado (opcional)

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ChatStatus status;

    @Column(length = 200)
    private String subject; // Asunto del chat

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime closedAt; // Fecha de cierre del chat

    // Enum para estados del chat
    public enum ChatStatus {
        OPEN,           // Abierto
        IN_PROGRESS,    // En progreso
        RESOLVED,       // Resuelto
        CLOSED          // Cerrado
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

    public UserEntity getSupportUser() {
        return supportUser;
    }

    public void setSupportUser(UserEntity supportUser) {
        this.supportUser = supportUser;
    }

    public OrderEntity getRelatedOrder() {
        return relatedOrder;
    }

    public void setRelatedOrder(OrderEntity relatedOrder) {
        this.relatedOrder = relatedOrder;
    }

    public ChatStatus getStatus() {
        return status;
    }

    public void setStatus(ChatStatus status) {
        this.status = status;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }
}

