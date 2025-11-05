package com.delivery_trujillo.app_trujillo_services.controllers;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.MenuItemEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.entities.RestaurantEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.repositories.MenuItemRepository;
import com.delivery_trujillo.app_trujillo_services.persistence.repositories.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para menú de restaurantes (RF-12)
 */
@RestController
@RequestMapping("/v1/restaurants")
public class MenuItemController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuItemController.class);

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    /**
     * Obtener menú de un restaurante
     * GET /v1/restaurants/{restaurantId}/menu
     */
    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<List<MenuItemEntity>> getRestaurantMenu(@PathVariable Long restaurantId) {
        try {
            LOGGER.info("Fetching menu for restaurant ID: {}", restaurantId);
            Optional<RestaurantEntity> restaurant = restaurantRepository.findById(restaurantId);
            if (restaurant.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            List<MenuItemEntity> menuItems = menuItemRepository.findByRestaurantAndIsAvailableTrue(restaurant.get());
            return new ResponseEntity<>(menuItems, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Error fetching menu: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Buscar platos en el menú (RF-08)
     * GET /v1/restaurants/{restaurantId}/menu/search?q={searchTerm}
     */
    @GetMapping("/{restaurantId}/menu/search")
    public ResponseEntity<List<MenuItemEntity>> searchMenuItems(
            @PathVariable Long restaurantId,
            @RequestParam String q) {
        try {
            LOGGER.info("Searching menu items for restaurant ID: {} with term: {}", restaurantId, q);
            Optional<RestaurantEntity> restaurant = restaurantRepository.findById(restaurantId);
            if (restaurant.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            List<MenuItemEntity> menuItems = menuItemRepository.searchMenuItems(restaurant.get(), q);
            return new ResponseEntity<>(menuItems, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Error searching menu items: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

