package com.delivery_trujillo.app_trujillo_services.persistence.repositories;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.NotificationEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    
    List<NotificationEntity> findByUser(UserEntity user);
    
    List<NotificationEntity> findByUserAndIsReadFalse(UserEntity user);
    
    List<NotificationEntity> findByUserOrderByCreatedAtDesc(UserEntity user);
    
    Long countByUserAndIsReadFalse(UserEntity user);
}

