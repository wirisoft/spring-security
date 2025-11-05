package com.delivery_trujillo.app_trujillo_services.persistence.repositories;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.ChatEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, Long> {
    
    List<ChatEntity> findByUser(UserEntity user);
    
    List<ChatEntity> findBySupportUser(UserEntity supportUser);
    
    List<ChatEntity> findBySupportUserIsNull(); // Chats sin asignar
    
    Optional<ChatEntity> findByIdAndUser(Long chatId, UserEntity user);
    
    List<ChatEntity> findByUserOrderByUpdatedAtDesc(UserEntity user);
}

