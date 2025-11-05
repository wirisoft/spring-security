package com.delivery_trujillo.app_trujillo_services.persistence.repositories;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.RestaurantEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long> {
    
    List<RestaurantEntity> findByIsActiveTrue();
    
    List<RestaurantEntity> findByIsFeaturedTrueAndIsActiveTrue();
    
    List<RestaurantEntity> findByOwner(UserEntity owner);
    
    Optional<RestaurantEntity> findByIdAndIsActiveTrue(Long id);
    
    @Query("SELECT r FROM RestaurantEntity r WHERE r.isActive = true AND " +
           "(6371 * acos(cos(radians(:lat)) * cos(radians(r.latitude)) * " +
           "cos(radians(r.longitude) - radians(:lng)) + sin(radians(:lat)) * " +
           "sin(radians(r.latitude)))) <= :radius")
    List<RestaurantEntity> findNearbyRestaurants(@Param("lat") Double latitude, 
                                                 @Param("lng") Double longitude, 
                                                 @Param("radius") Double radiusInKm);
    
    @Query("SELECT r FROM RestaurantEntity r WHERE r.isActive = true AND " +
           "(LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<RestaurantEntity> searchRestaurants(@Param("search") String searchTerm);
}

