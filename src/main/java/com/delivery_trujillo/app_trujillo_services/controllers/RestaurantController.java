package com.delivery_trujillo.app_trujillo_services.controllers;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.RestaurantEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.repositories.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador para restaurantes (RF-07, RF-08, RF-09, RF-10)
 */
@RestController
@RequestMapping("/v1/restaurants")
public class RestaurantController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantController.class);

    @Autowired
    private RestaurantRepository restaurantRepository;

    /**
     * Obtener todos los restaurantes activos
     * GET /v1/restaurants
     */
    @GetMapping
    public ResponseEntity<List<RestaurantEntity>> getAllRestaurants() {
        try {
            LOGGER.info("Fetching all active restaurants");
            List<RestaurantEntity> restaurants = restaurantRepository.findByIsActiveTrue();
            return new ResponseEntity<>(restaurants, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Error fetching restaurants: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtener restaurantes destacados (RF-07)
     * GET /v1/restaurants/featured
     */
    @GetMapping("/featured")
    public ResponseEntity<List<RestaurantEntity>> getFeaturedRestaurants() {
        try {
            LOGGER.info("Fetching featured restaurants");
            List<RestaurantEntity> restaurants = restaurantRepository.findByIsFeaturedTrueAndIsActiveTrue();
            return new ResponseEntity<>(restaurants, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Error fetching featured restaurants: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtener restaurante por ID
     * GET /v1/restaurants/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantEntity> getRestaurantById(@PathVariable Long id) {
        try {
            LOGGER.info("Fetching restaurant ID: {}", id);
            Optional<RestaurantEntity> restaurant = restaurantRepository.findByIdAndIsActiveTrue(id);
            if (restaurant.isPresent()) {
                return new ResponseEntity<>(restaurant.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            LOGGER.error("Error fetching restaurant: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Buscar restaurantes (RF-08)
     * GET /v1/restaurants/search?q={searchTerm}
     */
    @GetMapping("/search")
    public ResponseEntity<List<RestaurantEntity>> searchRestaurants(@RequestParam String q) {
        try {
            LOGGER.info("Searching restaurants with term: {}", q);
            List<RestaurantEntity> restaurants = restaurantRepository.searchRestaurants(q);
            return new ResponseEntity<>(restaurants, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Error searching restaurants: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtener restaurantes cercanos (RF-07, RF-10)
     * GET /v1/restaurants/nearby?lat={latitude}&lng={longitude}&radius={radiusInKm}
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<RestaurantEntity>> getNearbyRestaurants(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "5.0") Double radius) {
        try {
            LOGGER.info("Fetching nearby restaurants: lat={}, lng={}, radius={}km", lat, lng, radius);
            List<RestaurantEntity> restaurants = restaurantRepository.findNearbyRestaurants(lat, lng, radius);
            return new ResponseEntity<>(restaurants, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Error fetching nearby restaurants: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Dashboard principal (RF-07) - Carrusel de promociones, categorías, restaurantes destacados
     * GET /v1/restaurants/home
     */
    @GetMapping("/home")
    public ResponseEntity<Map<String, Object>> getHomeDashboard() {
        try {
            LOGGER.info("Fetching home dashboard");
            Map<String, Object> dashboard = new HashMap<>();
            
            // Restaurantes destacados
            dashboard.put("featuredRestaurants", restaurantRepository.findByIsFeaturedTrueAndIsActiveTrue());
            
            // Restaurantes activos (cercanos)
            dashboard.put("activeRestaurants", restaurantRepository.findByIsActiveTrue());
            
            return new ResponseEntity<>(dashboard, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Error fetching home dashboard: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

