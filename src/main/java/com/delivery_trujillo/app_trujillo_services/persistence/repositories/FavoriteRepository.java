package com.delivery_trujillo.app_trujillo_services.persistence.repositories;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.FavoriteEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.entities.RestaurantEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<FavoriteEntity, Long> {
    
    List<FavoriteEntity> findByUser(UserEntity user);
    
    Optional<FavoriteEntity> findByUserAndRestaurant(UserEntity user, RestaurantEntity restaurant);
    
    void deleteByUserAndRestaurant(UserEntity user, RestaurantEntity restaurant);
    
    boolean existsByUserAndRestaurant(UserEntity user, RestaurantEntity restaurant);
}

