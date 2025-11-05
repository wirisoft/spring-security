package com.delivery_trujillo.app_trujillo_services.services.impl;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.*;
import com.delivery_trujillo.app_trujillo_services.persistence.repositories.*;
import com.delivery_trujillo.app_trujillo_services.services.INotificationService;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.ResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements INotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    @Transactional
    public void createNotification(Long userId, String title, String message,
                                   NotificationEntity.NotificationType type, Long orderId) throws Exception {
        try {
            LOGGER.info("Creating notification for user: {} with type: {}", userId, type);
            
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));

            NotificationEntity notification = new NotificationEntity();
            notification.setUser(user);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setType(type);
            notification.setIsRead(false);

            if (orderId != null) {
                Optional<OrderEntity> orderOpt = orderRepository.findById(orderId);
                if (orderOpt.isPresent()) {
                    notification.setRelatedOrder(orderOpt.get());
                    notification.setActionUrl("/v1/orders/" + orderId + "/status");
                }
            }

            notificationRepository.save(notification);
            LOGGER.info("Notification created successfully for user: {}", userId);
        } catch (Exception e) {
            LOGGER.error("Error creating notification: {}", e.getMessage(), e);
            throw new Exception("Error al crear notificación: " + e.getMessage());
        }
    }

    @Override
    public List<HashMap<String, Object>> getUserNotifications(Long userId) throws Exception {
        try {
            LOGGER.info("Getting notifications for user: {}", userId);
            
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));

            List<NotificationEntity> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
            
            List<HashMap<String, Object>> notificationsData = new ArrayList<>();
            
            for (NotificationEntity notification : notifications) {
                HashMap<String, Object> notificationData = new HashMap<>();
                notificationData.put("id", notification.getId());
                notificationData.put("title", notification.getTitle());
                notificationData.put("message", notification.getMessage());
                notificationData.put("type", notification.getType().name());
                notificationData.put("isRead", notification.getIsRead());
                notificationData.put("actionUrl", notification.getActionUrl());
                notificationData.put("createdAt", notification.getCreatedAt());
                if (notification.getRelatedOrder() != null) {
                    notificationData.put("orderId", notification.getRelatedOrder().getId());
                }
                notificationsData.add(notificationData);
            }

            return notificationsData;
        } catch (Exception e) {
            LOGGER.error("Error getting notifications: {}", e.getMessage(), e);
            throw new Exception("Error al obtener notificaciones: " + e.getMessage());
        }
    }

    @Override
    public Long getUnreadCount(Long userId) throws Exception {
        try {
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));

            return notificationRepository.countByUserAndIsReadFalse(user);
        } catch (Exception e) {
            LOGGER.error("Error getting unread count: {}", e.getMessage(), e);
            throw new Exception("Error al obtener contador de no leídas: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseDTO markAsRead(Long userId, Long notificationId) throws Exception {
        try {
            LOGGER.info("Marking notification as read: {} for user: {}", notificationId, userId);
            ResponseDTO response = new ResponseDTO();

            Optional<NotificationEntity> notificationOpt = notificationRepository.findById(notificationId);
            if (notificationOpt.isEmpty()) {
                response.setMessage("Notificación no encontrada.");
                response.setNumOfErrors(1);
                return response;
            }

            NotificationEntity notification = notificationOpt.get();
            if (!notification.getUser().getId().equals(userId)) {
                response.setMessage("La notificación no pertenece al usuario.");
                response.setNumOfErrors(1);
                return response;
            }

            notification.setIsRead(true);
            notificationRepository.save(notification);

            response.setMessage("Notificación marcada como leída.");
            response.setNumOfErrors(0);
            return response;
        } catch (Exception e) {
            LOGGER.error("Error marking notification as read: {}", e.getMessage(), e);
            throw new Exception("Error al marcar notificación: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseDTO markAllAsRead(Long userId) throws Exception {
        try {
            LOGGER.info("Marking all notifications as read for user: {}", userId);
            ResponseDTO response = new ResponseDTO();

            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));

            List<NotificationEntity> unreadNotifications = notificationRepository.findByUserAndIsReadFalse(user);
            unreadNotifications.forEach(notification -> {
                notification.setIsRead(true);
                notificationRepository.save(notification);
            });

            response.setMessage("Todas las notificaciones marcadas como leídas.");
            response.setNumOfErrors(0);
            return response;
        } catch (Exception e) {
            LOGGER.error("Error marking all notifications as read: {}", e.getMessage(), e);
            throw new Exception("Error al marcar todas las notificaciones: " + e.getMessage());
        }
    }
}

