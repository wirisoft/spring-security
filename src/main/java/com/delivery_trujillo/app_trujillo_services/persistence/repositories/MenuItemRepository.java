package com.delivery_trujillo.app_trujillo_services.persistence.repositories;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.MenuItemEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.entities.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItemEntity, Long> {
    
    List<MenuItemEntity> findByRestaurantAndIsAvailableTrue(RestaurantEntity restaurant);
    
    @Query("SELECT m FROM MenuItemEntity m WHERE m.restaurant = :restaurant AND m.isAvailable = true " +
           "AND (LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(m.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<MenuItemEntity> searchMenuItems(@Param("restaurant") RestaurantEntity restaurant, 
                                        @Param("search") String searchTerm);
}

