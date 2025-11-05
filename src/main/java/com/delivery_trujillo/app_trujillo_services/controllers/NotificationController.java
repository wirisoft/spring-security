package com.delivery_trujillo.app_trujillo_services.controllers;

import com.delivery_trujillo.app_trujillo_services.services.INotificationService;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.ResponseDTO;
import com.delivery_trujillo.app_trujillo_services.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * Controlador para notificaciones push (RF-19)
 */
@RestController
@RequestMapping("/v1/notifications")
public class NotificationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private INotificationService notificationService;

    /**
     * Obtener notificaciones del usuario (RF-19)
     * GET /v1/notifications
     */
    @GetMapping
    private ResponseEntity<List<HashMap<String, Object>>> getNotifications() throws Exception {
        Long userId = SecurityUtils.getCurrentUserOrThrow().getId();
        LOGGER.info("Getting notifications for user: {}", userId);
        return new ResponseEntity<>(notificationService.getUserNotifications(userId), HttpStatus.OK);
    }

    /**
     * Obtener contador de notificaciones no leídas (RF-19)
     * GET /v1/notifications/unread-count
     */
    @GetMapping("/unread-count")
    private ResponseEntity<HashMap<String, Object>> getUnreadCount() throws Exception {
        Long userId = SecurityUtils.getCurrentUserOrThrow().getId();
        LOGGER.info("Getting unread count for user: {}", userId);
        Long count = notificationService.getUnreadCount(userId);
        HashMap<String, Object> response = new HashMap<>();
        response.put("unreadCount", count);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Marcar notificación como leída (RF-19)
     * PUT /v1/notifications/{notificationId}/read
     */
    @PutMapping("/{notificationId}/read")
    private ResponseEntity<ResponseDTO> markAsRead(@PathVariable Long notificationId) throws Exception {
        Long userId = SecurityUtils.getCurrentUserOrThrow().getId();
        LOGGER.info("Marking notification as read: {} for user: {}", notificationId, userId);
        return new ResponseEntity<>(notificationService.markAsRead(userId, notificationId), HttpStatus.OK);
    }

    /**
     * Marcar todas las notificaciones como leídas (RF-19)
     * PUT /v1/notifications/read-all
     */
    @PutMapping("/read-all")
    private ResponseEntity<ResponseDTO> markAllAsRead() throws Exception {
        Long userId = SecurityUtils.getCurrentUserOrThrow().getId();
        LOGGER.info("Marking all notifications as read for user: {}", userId);
        return new ResponseEntity<>(notificationService.markAllAsRead(userId), HttpStatus.OK);
    }
}

