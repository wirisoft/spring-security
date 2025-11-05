package com.delivery_trujillo.app_trujillo_services.persistence.repositories;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.PromotionEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.entities.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<PromotionEntity, Long> {
    
    List<PromotionEntity> findByIsActiveTrueAndStartDateBeforeAndEndDateAfter(
        LocalDateTime now, LocalDateTime now2);
    
    List<PromotionEntity> findByRestaurantAndIsActiveTrue(RestaurantEntity restaurant);
    
    List<PromotionEntity> findByRestaurantIsNullAndIsActiveTrue(); // Promociones globales
}

