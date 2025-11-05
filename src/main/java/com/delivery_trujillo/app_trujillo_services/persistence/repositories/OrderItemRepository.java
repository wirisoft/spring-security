package com.delivery_trujillo.app_trujillo_services.persistence.repositories;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.OrderEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.entities.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
    
    List<OrderItemEntity> findByOrder(OrderEntity order);
}

