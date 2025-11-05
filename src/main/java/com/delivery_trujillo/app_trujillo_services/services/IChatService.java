package com.delivery_trujillo.app_trujillo_services.services;

import com.delivery_trujillo.app_trujillo_services.services.models.dtos.ChatDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.MessageDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.ResponseDTO;

import java.util.HashMap;
import java.util.List;

public interface IChatService {
    
    HashMap<String, Object> createChat(Long userId, ChatDTO chatDTO) throws Exception;
    
    List<HashMap<String, Object>> getUserChats(Long userId) throws Exception;
    
    HashMap<String, Object> getChat(Long userId, Long chatId) throws Exception;
    
    ResponseDTO sendMessage(Long userId, Long chatId, MessageDTO messageDTO) throws Exception;
    
    ResponseDTO assignSupportUser(Long chatId, Long supportUserId) throws Exception;
    
    ResponseDTO closeChat(Long chatId, Long userId) throws Exception;
}

