package com.delivery_trujillo.app_trujillo_services.controllers;

import com.delivery_trujillo.app_trujillo_services.services.IChatService;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.ChatDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.MessageDTO;
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
 * Controlador para chat de soporte al cliente (RF-21)
 */
@RestController
@RequestMapping("/v1/chat")
public class ChatController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private IChatService chatService;

    /**
     * Crear chat de soporte (RF-21)
     * POST /v1/chat
     */
    @PostMapping
    private ResponseEntity<HashMap<String, Object>> createChat(@RequestBody ChatDTO chatDTO) throws Exception {
        Long userId = SecurityUtils.getCurrentUserOrThrow().getId();
        LOGGER.info("Creating chat for user: {}", userId);
        return new ResponseEntity<>(chatService.createChat(userId, chatDTO), HttpStatus.CREATED);
    }

    /**
     * Obtener chats del usuario (RF-21)
     * GET /v1/chat
     */
    @GetMapping
    private ResponseEntity<List<HashMap<String, Object>>> getUserChats() throws Exception {
        Long userId = SecurityUtils.getCurrentUserOrThrow().getId();
        LOGGER.info("Getting chats for user: {}", userId);
        return new ResponseEntity<>(chatService.getUserChats(userId), HttpStatus.OK);
    }

    /**
     * Obtener chat específico con mensajes (RF-21)
     * GET /v1/chat/{chatId}
     */
    @GetMapping("/{chatId}")
    private ResponseEntity<HashMap<String, Object>> getChat(@PathVariable Long chatId) throws Exception {
        Long userId = SecurityUtils.getCurrentUserOrThrow().getId();
        LOGGER.info("Getting chat: {} for user: {}", chatId, userId);
        return new ResponseEntity<>(chatService.getChat(userId, chatId), HttpStatus.OK);
    }

    /**
     * Enviar mensaje en chat (RF-21)
     * POST /v1/chat/{chatId}/messages
     */
    @PostMapping("/{chatId}/messages")
    private ResponseEntity<ResponseDTO> sendMessage(@PathVariable Long chatId, @RequestBody MessageDTO messageDTO) throws Exception {
        Long userId = SecurityUtils.getCurrentUserOrThrow().getId();
        LOGGER.info("Sending message to chat: {} from user: {}", chatId, userId);
        return new ResponseEntity<>(chatService.sendMessage(userId, chatId, messageDTO), HttpStatus.CREATED);
    }

    /**
     * Cerrar chat (RF-21)
     * PUT /v1/chat/{chatId}/close
     */
    @PutMapping("/{chatId}/close")
    private ResponseEntity<ResponseDTO> closeChat(@PathVariable Long chatId) throws Exception {
        Long userId = SecurityUtils.getCurrentUserOrThrow().getId();
        LOGGER.info("Closing chat: {} by user: {}", chatId, userId);
        return new ResponseEntity<>(chatService.closeChat(chatId, userId), HttpStatus.OK);
    }

    /**
     * Asignar usuario de soporte a chat (RF-21)
     * PUT /v1/chat/{chatId}/assign
     */
    @PutMapping("/{chatId}/assign")
    private ResponseEntity<ResponseDTO> assignSupportUser(
            @PathVariable Long chatId,
            @RequestParam Long supportUserId) throws Exception {
        LOGGER.info("Assigning support user: {} to chat: {}", supportUserId, chatId);
        return new ResponseEntity<>(chatService.assignSupportUser(chatId, supportUserId), HttpStatus.OK);
    }
}

