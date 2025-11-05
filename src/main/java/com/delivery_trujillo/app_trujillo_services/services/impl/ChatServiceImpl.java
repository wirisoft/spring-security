package com.delivery_trujillo.app_trujillo_services.services.impl;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.*;
import com.delivery_trujillo.app_trujillo_services.persistence.repositories.*;
import com.delivery_trujillo.app_trujillo_services.services.IChatService;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.ChatDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.MessageDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.ResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class ChatServiceImpl implements IChatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatServiceImpl.class);

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    @Transactional
    public HashMap<String, Object> createChat(Long userId, ChatDTO chatDTO) throws Exception {
        try {
            LOGGER.info("Creating chat for user: {}", userId);
            
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));

            ChatEntity chat = new ChatEntity();
            chat.setUser(user);
            chat.setStatus(ChatEntity.ChatStatus.OPEN);
            chat.setSubject(chatDTO.getSubject());

            if (chatDTO.getOrderId() != null) {
                Optional<OrderEntity> orderOpt = orderRepository.findById(chatDTO.getOrderId());
                if (orderOpt.isPresent()) {
                    OrderEntity order = orderOpt.get();
                    if (!order.getUser().getId().equals(userId)) {
                        throw new Exception("El pedido no pertenece al usuario");
                    }
                    chat.setRelatedOrder(order);
                }
            }

            chat = chatRepository.save(chat);

            HashMap<String, Object> response = new HashMap<>();
            response.put("chatId", chat.getId());
            response.put("status", chat.getStatus().name());
            response.put("message", "Chat creado exitosamente");

            return response;
        } catch (Exception e) {
            LOGGER.error("Error creating chat: {}", e.getMessage(), e);
            throw new Exception("Error al crear chat: " + e.getMessage());
        }
    }

    @Override
    public List<HashMap<String, Object>> getUserChats(Long userId) throws Exception {
        try {
            LOGGER.info("Getting chats for user: {}", userId);
            
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));

            List<ChatEntity> chats = chatRepository.findByUserOrderByUpdatedAtDesc(user);
            
            List<HashMap<String, Object>> chatsData = new ArrayList<>();
            
            for (ChatEntity chat : chats) {
                HashMap<String, Object> chatData = buildChatResponse(chat);
                chatsData.add(chatData);
            }

            return chatsData;
        } catch (Exception e) {
            LOGGER.error("Error getting user chats: {}", e.getMessage(), e);
            throw new Exception("Error al obtener chats: " + e.getMessage());
        }
    }

    @Override
    public HashMap<String, Object> getChat(Long userId, Long chatId) throws Exception {
        try {
            LOGGER.info("Getting chat: {} for user: {}", chatId, userId);
            
            Optional<ChatEntity> chatOpt = chatRepository.findByIdAndUser(chatId, 
                    userRepository.findById(userId)
                            .orElseThrow(() -> new Exception("Usuario no encontrado")));

            if (chatOpt.isEmpty()) {
                throw new Exception("Chat no encontrado o no pertenece al usuario");
            }

            ChatEntity chat = chatOpt.get();
            HashMap<String, Object> chatData = buildChatResponse(chat);

            // Obtener mensajes
            List<MessageEntity> messages = messageRepository.findByChatOrderByCreatedAtAsc(chat);
            List<HashMap<String, Object>> messagesData = new ArrayList<>();
            
            for (MessageEntity message : messages) {
                HashMap<String, Object> messageData = new HashMap<>();
                messageData.put("id", message.getId());
                messageData.put("content", message.getContent());
                messageData.put("isFromSupport", message.getIsFromSupport());
                messageData.put("senderName", message.getSender().getFirstName() + " " + message.getSender().getLastName());
                messageData.put("attachmentUrl", message.getAttachmentUrl());
                messageData.put("createdAt", message.getCreatedAt());
                messagesData.add(messageData);
            }

            chatData.put("messages", messagesData);
            return chatData;
        } catch (Exception e) {
            LOGGER.error("Error getting chat: {}", e.getMessage(), e);
            throw new Exception("Error al obtener chat: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseDTO sendMessage(Long userId, Long chatId, MessageDTO messageDTO) throws Exception {
        try {
            LOGGER.info("Sending message to chat: {} from user: {}", chatId, userId);
            ResponseDTO response = new ResponseDTO();

            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));

            Optional<ChatEntity> chatOpt = chatRepository.findById(chatId);
            if (chatOpt.isEmpty()) {
                response.setMessage("Chat no encontrado.");
                response.setNumOfErrors(1);
                return response;
            }

            ChatEntity chat = chatOpt.get();
            
            // Verificar permisos: el usuario debe ser el dueño del chat o el soporte asignado
            if (!chat.getUser().getId().equals(userId) && 
                (chat.getSupportUser() == null || !chat.getSupportUser().getId().equals(userId))) {
                response.setMessage("No tienes permiso para enviar mensajes en este chat.");
                response.setNumOfErrors(1);
                return response;
            }

            // Verificar que el chat no esté cerrado
            if (chat.getStatus() == ChatEntity.ChatStatus.CLOSED) {
                response.setMessage("No se pueden enviar mensajes a un chat cerrado.");
                response.setNumOfErrors(1);
                return response;
            }

            MessageEntity message = new MessageEntity();
            message.setChat(chat);
            message.setSender(user);
            message.setContent(messageDTO.getContent());
            message.setAttachmentUrl(messageDTO.getAttachmentUrl());
            
            // Determinar si el mensaje es del equipo de soporte
            boolean isFromSupport = chat.getSupportUser() != null && 
                                   chat.getSupportUser().getId().equals(userId);
            message.setIsFromSupport(isFromSupport);

            messageRepository.save(message);

            // Actualizar estado del chat si es necesario
            if (chat.getStatus() == ChatEntity.ChatStatus.OPEN && isFromSupport) {
                chat.setStatus(ChatEntity.ChatStatus.IN_PROGRESS);
            }
            chatRepository.save(chat);

            response.setMessage("Mensaje enviado exitosamente.");
            response.setNumOfErrors(0);
            return response;
        } catch (Exception e) {
            LOGGER.error("Error sending message: {}", e.getMessage(), e);
            throw new Exception("Error al enviar mensaje: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseDTO assignSupportUser(Long chatId, Long supportUserId) throws Exception {
        try {
            LOGGER.info("Assigning support user: {} to chat: {}", supportUserId, chatId);
            ResponseDTO response = new ResponseDTO();

            Optional<ChatEntity> chatOpt = chatRepository.findById(chatId);
            if (chatOpt.isEmpty()) {
                response.setMessage("Chat no encontrado.");
                response.setNumOfErrors(1);
                return response;
            }

            UserEntity supportUser = userRepository.findById(supportUserId)
                    .orElseThrow(() -> new Exception("Usuario de soporte no encontrado"));

            // Verificar que el usuario tenga rol SUPPORT
            if (supportUser.getRole() == null || 
                !supportUser.getRole().getRoleName().name().equals("SUPPORT")) {
                response.setMessage("El usuario no tiene rol de soporte.");
                response.setNumOfErrors(1);
                return response;
            }

            ChatEntity chat = chatOpt.get();
            chat.setSupportUser(supportUser);
            if (chat.getStatus() == ChatEntity.ChatStatus.OPEN) {
                chat.setStatus(ChatEntity.ChatStatus.IN_PROGRESS);
            }
            chatRepository.save(chat);

            response.setMessage("Usuario de soporte asignado exitosamente.");
            response.setNumOfErrors(0);
            return response;
        } catch (Exception e) {
            LOGGER.error("Error assigning support user: {}", e.getMessage(), e);
            throw new Exception("Error al asignar usuario de soporte: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseDTO closeChat(Long chatId, Long userId) throws Exception {
        try {
            LOGGER.info("Closing chat: {} by user: {}", chatId, userId);
            ResponseDTO response = new ResponseDTO();

            Optional<ChatEntity> chatOpt = chatRepository.findById(chatId);
            if (chatOpt.isEmpty()) {
                response.setMessage("Chat no encontrado.");
                response.setNumOfErrors(1);
                return response;
            }

            ChatEntity chat = chatOpt.get();
            
            // Verificar permisos: solo el dueño del chat o el soporte asignado pueden cerrarlo
            if (!chat.getUser().getId().equals(userId) && 
                (chat.getSupportUser() == null || !chat.getSupportUser().getId().equals(userId))) {
                response.setMessage("No tienes permiso para cerrar este chat.");
                response.setNumOfErrors(1);
                return response;
            }

            chat.setStatus(ChatEntity.ChatStatus.CLOSED);
            chat.setClosedAt(LocalDateTime.now());
            chatRepository.save(chat);

            response.setMessage("Chat cerrado exitosamente.");
            response.setNumOfErrors(0);
            return response;
        } catch (Exception e) {
            LOGGER.error("Error closing chat: {}", e.getMessage(), e);
            throw new Exception("Error al cerrar chat: " + e.getMessage());
        }
    }

    private HashMap<String, Object> buildChatResponse(ChatEntity chat) {
        HashMap<String, Object> chatData = new HashMap<>();
        chatData.put("id", chat.getId());
        chatData.put("status", chat.getStatus().name());
        chatData.put("subject", chat.getSubject());
        chatData.put("createdAt", chat.getCreatedAt());
        chatData.put("updatedAt", chat.getUpdatedAt());
        chatData.put("closedAt", chat.getClosedAt());
        
        if (chat.getRelatedOrder() != null) {
            chatData.put("orderId", chat.getRelatedOrder().getId());
        }
        
        if (chat.getSupportUser() != null) {
            HashMap<String, Object> supportUserData = new HashMap<>();
            supportUserData.put("id", chat.getSupportUser().getId());
            supportUserData.put("name", chat.getSupportUser().getFirstName() + " " + chat.getSupportUser().getLastName());
            chatData.put("supportUser", supportUserData);
        }
        
        return chatData;
    }
}

