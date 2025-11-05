package com.delivery_trujillo.app_trujillo_services.services;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.NotificationEntity;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.ResponseDTO;

import java.util.HashMap;
import java.util.List;

public interface INotificationService {
    
    void createNotification(Long userId, String title, String message, 
                           NotificationEntity.NotificationType type, Long orderId) throws Exception;
    
    List<HashMap<String, Object>> getUserNotifications(Long userId) throws Exception;
    
    Long getUnreadCount(Long userId) throws Exception;
    
    ResponseDTO markAsRead(Long userId, Long notificationId) throws Exception;
    
    ResponseDTO markAllAsRead(Long userId) throws Exception;
}

