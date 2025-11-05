package com.delivery_trujillo.app_trujillo_services.persistence.repositories;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.ChatEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.entities.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    
    List<MessageEntity> findByChat(ChatEntity chat);
    
    List<MessageEntity> findByChatOrderByCreatedAtAsc(ChatEntity chat);
}

