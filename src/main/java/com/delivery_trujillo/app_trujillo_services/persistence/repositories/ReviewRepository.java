package com.delivery_trujillo.app_trujillo_services.persistence.repositories;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.OrderEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.entities.RestaurantEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.entities.ReviewEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    
    List<ReviewEntity> findByRestaurant(RestaurantEntity restaurant);
    
    List<ReviewEntity> findByUser(UserEntity user);
    
    Optional<ReviewEntity> findByOrder(OrderEntity order);
    
    List<ReviewEntity> findByRestaurantOrderByCreatedAtDesc(RestaurantEntity restaurant);
}

