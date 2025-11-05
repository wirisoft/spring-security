package com.delivery_trujillo.app_trujillo_services.persistence.repositories;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.OrderEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    
    List<OrderEntity> findByUser(UserEntity user);
    
    List<OrderEntity> findByUser_Id(Long userId);
    
    List<OrderEntity> findByUser_IdOrderByOrderDateDesc(Long userId);
    
    Optional<OrderEntity> findByIdAndUser_Id(Long orderId, Long userId);
}

