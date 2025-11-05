package com.delivery_trujillo.app_trujillo_services.controllers;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.FavoriteEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.entities.RestaurantEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.entities.UserEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.repositories.FavoriteRepository;
import com.delivery_trujillo.app_trujillo_services.persistence.repositories.RestaurantRepository;
import com.delivery_trujillo.app_trujillo_services.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para favoritos (RF-11)
 */
@RestController
@RequestMapping("/v1/favorites")
public class FavoriteController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FavoriteController.class);

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    /**
     * Obtener restaurantes favoritos del usuario
     * GET /v1/favorites
     */
    @GetMapping
    public ResponseEntity<List<FavoriteEntity>> getUserFavorites() {
        try {
            UserEntity user = SecurityUtils.getCurrentUserOrThrow();
            LOGGER.info("Fetching favorites for user ID: {}", user.getId());
            List<FavoriteEntity> favorites = favoriteRepository.findByUser(user);
            return new ResponseEntity<>(favorites, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Error fetching favorites: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Agregar restaurante a favoritos
     * POST /v1/favorites/{restaurantId}
     */
    @PostMapping("/{restaurantId}")
    public ResponseEntity<FavoriteEntity> addFavorite(@PathVariable Long restaurantId) {
        try {
            UserEntity user = SecurityUtils.getCurrentUserOrThrow();
            LOGGER.info("Adding favorite restaurant ID: {} for user ID: {}", restaurantId, user.getId());
            
            Optional<RestaurantEntity> restaurant = restaurantRepository.findById(restaurantId);
            if (restaurant.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            if (favoriteRepository.existsByUserAndRestaurant(user, restaurant.get())) {
                return new ResponseEntity<>(HttpStatus.CONFLICT); // Ya está en favoritos
            }

            FavoriteEntity favorite = new FavoriteEntity();
            favorite.setUser(user);
            favorite.setRestaurant(restaurant.get());
            FavoriteEntity saved = favoriteRepository.save(favorite);
            
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            LOGGER.error("Error adding favorite: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Eliminar restaurante de favoritos
     * DELETE /v1/favorites/{restaurantId}
     */
    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<HashMap<String, String>> removeFavorite(@PathVariable Long restaurantId) {
        try {
            UserEntity user = SecurityUtils.getCurrentUserOrThrow();
            LOGGER.info("Removing favorite restaurant ID: {} for user ID: {}", restaurantId, user.getId());
            
            Optional<RestaurantEntity> restaurant = restaurantRepository.findById(restaurantId);
            if (restaurant.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            favoriteRepository.deleteByUserAndRestaurant(user, restaurant.get());
            HashMap<String, String> response = new HashMap<>();
            response.put("message", "Favorite removed successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Error removing favorite: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

