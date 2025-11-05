package com.delivery_trujillo.app_trujillo_services.controllers;

import com.delivery_trujillo.app_trujillo_services.services.IReviewService;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.ResponseDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.ReviewDTO;
import com.delivery_trujillo.app_trujillo_services.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * Controlador para calificaciones y reseñas (RF-20)
 */
@RestController
@RequestMapping("/v1/reviews")
public class ReviewController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewController.class);

    @Autowired
    private IReviewService reviewService;

    /**
     * Crear reseña (RF-20)
     * POST /v1/reviews
     */
    @PostMapping
    private ResponseEntity<ResponseDTO> createReview(@RequestBody ReviewDTO reviewDTO) throws Exception {
        Long userId = SecurityUtils.getCurrentUserOrThrow().getId();
        LOGGER.info("Creating review for user: {}", userId);
        return new ResponseEntity<>(reviewService.createReview(userId, reviewDTO), HttpStatus.CREATED);
    }

    /**
     * Obtener reseñas de un restaurante (RF-20)
     * GET /v1/reviews/restaurant/{restaurantId}
     */
    @GetMapping("/restaurant/{restaurantId}")
    private ResponseEntity<List<HashMap<String, Object>>> getRestaurantReviews(@PathVariable Long restaurantId) throws Exception {
        LOGGER.info("Getting reviews for restaurant: {}", restaurantId);
        return new ResponseEntity<>(reviewService.getRestaurantReviews(restaurantId), HttpStatus.OK);
    }

    /**
     * Obtener reseña del usuario para un pedido (RF-20)
     * GET /v1/reviews/order/{orderId}
     */
    @GetMapping("/order/{orderId}")
    private ResponseEntity<HashMap<String, Object>> getUserReview(@PathVariable Long orderId) throws Exception {
        Long userId = SecurityUtils.getCurrentUserOrThrow().getId();
        LOGGER.info("Getting review for order: {} and user: {}", orderId, userId);
        return new ResponseEntity<>(reviewService.getUserReview(userId, orderId), HttpStatus.OK);
    }
}

