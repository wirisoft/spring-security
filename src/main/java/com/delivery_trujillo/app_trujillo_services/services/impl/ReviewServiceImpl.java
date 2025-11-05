package com.delivery_trujillo.app_trujillo_services.services.impl;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.*;
import com.delivery_trujillo.app_trujillo_services.persistence.repositories.*;
import com.delivery_trujillo.app_trujillo_services.services.IReviewService;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.ResponseDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.ReviewDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements IReviewService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewServiceImpl.class);

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Override
    @Transactional
    public ResponseDTO createReview(Long userId, ReviewDTO reviewDTO) throws Exception {
        try {
            LOGGER.info("Creating review for user: {} and order: {}", userId, reviewDTO.getOrderId());
            ResponseDTO response = new ResponseDTO();

            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));

            OrderEntity order = orderRepository.findById(reviewDTO.getOrderId())
                    .orElseThrow(() -> new Exception("Pedido no encontrado"));

            // Verificar que el pedido pertenezca al usuario
            if (!order.getUser().getId().equals(userId)) {
                response.setMessage("El pedido no pertenece al usuario.");
                response.setNumOfErrors(1);
                return response;
            }

            // Verificar que el pedido esté entregado
            if (order.getStatus() != OrderEntity.OrderStatus.DELIVERED) {
                response.setMessage("Solo se pueden calificar pedidos entregados.");
                response.setNumOfErrors(1);
                return response;
            }

            // Verificar si ya existe una reseña para este pedido
            Optional<ReviewEntity> existingReview = reviewRepository.findByOrder(order);
            if (existingReview.isPresent()) {
                response.setMessage("Ya existe una reseña para este pedido.");
                response.setNumOfErrors(1);
                return response;
            }

            // Validar calificaciones
            if (reviewDTO.getRestaurantRating() == null || 
                reviewDTO.getRestaurantRating() < 1 || 
                reviewDTO.getRestaurantRating() > 5) {
                response.setMessage("La calificación del restaurante debe estar entre 1 y 5.");
                response.setNumOfErrors(1);
                return response;
            }

            if (reviewDTO.getDeliveryRating() != null && 
                (reviewDTO.getDeliveryRating() < 1 || reviewDTO.getDeliveryRating() > 5)) {
                response.setMessage("La calificación del repartidor debe estar entre 1 y 5.");
                response.setNumOfErrors(1);
                return response;
            }

            // Crear reseña
            ReviewEntity review = new ReviewEntity();
            review.setUser(user);
            review.setOrder(order);
            review.setRestaurant(order.getRestaurant());
            review.setRestaurantRating(reviewDTO.getRestaurantRating());
            review.setDeliveryRating(reviewDTO.getDeliveryRating());
            review.setComment(reviewDTO.getComment());
            review.setPhotoUrl(reviewDTO.getPhotoUrl());

            if (order.getDeliveryPerson() != null) {
                review.setDeliveryPerson(order.getDeliveryPerson());
            }

            reviewRepository.save(review);

            // Actualizar calificación promedio del restaurante
            updateRestaurantRating(order.getRestaurant());

            response.setMessage("Reseña creada exitosamente.");
            response.setNumOfErrors(0);
            return response;
        } catch (Exception e) {
            LOGGER.error("Error creating review: {}", e.getMessage(), e);
            throw new Exception("Error al crear reseña: " + e.getMessage());
        }
    }

    @Override
    public List<HashMap<String, Object>> getRestaurantReviews(Long restaurantId) throws Exception {
        try {
            LOGGER.info("Getting reviews for restaurant: {}", restaurantId);
            
            RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
                    .orElseThrow(() -> new Exception("Restaurante no encontrado"));

            List<ReviewEntity> reviews = reviewRepository.findByRestaurantOrderByCreatedAtDesc(restaurant);
            
            List<HashMap<String, Object>> reviewsData = new ArrayList<>();
            
            for (ReviewEntity review : reviews) {
                HashMap<String, Object> reviewData = new HashMap<>();
                reviewData.put("id", review.getId());
                reviewData.put("restaurantRating", review.getRestaurantRating());
                reviewData.put("deliveryRating", review.getDeliveryRating());
                reviewData.put("comment", review.getComment());
                reviewData.put("photoUrl", review.getPhotoUrl());
                reviewData.put("userName", review.getUser().getFirstName() + " " + review.getUser().getLastName());
                reviewData.put("createdAt", review.getCreatedAt());
                reviewsData.add(reviewData);
            }

            return reviewsData;
        } catch (Exception e) {
            LOGGER.error("Error getting restaurant reviews: {}", e.getMessage(), e);
            throw new Exception("Error al obtener reseñas: " + e.getMessage());
        }
    }

    @Override
    public HashMap<String, Object> getUserReview(Long userId, Long orderId) throws Exception {
        try {
            LOGGER.info("Getting review for user: {} and order: {}", userId, orderId);
            
            OrderEntity order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new Exception("Pedido no encontrado"));

            if (!order.getUser().getId().equals(userId)) {
                throw new Exception("El pedido no pertenece al usuario");
            }

            Optional<ReviewEntity> reviewOpt = reviewRepository.findByOrder(order);
            
            HashMap<String, Object> response = new HashMap<>();
            if (reviewOpt.isPresent()) {
                ReviewEntity review = reviewOpt.get();
                response.put("id", review.getId());
                response.put("restaurantRating", review.getRestaurantRating());
                response.put("deliveryRating", review.getDeliveryRating());
                response.put("comment", review.getComment());
                response.put("photoUrl", review.getPhotoUrl());
                response.put("createdAt", review.getCreatedAt());
            } else {
                response.put("review", null);
            }

            return response;
        } catch (Exception e) {
            LOGGER.error("Error getting user review: {}", e.getMessage(), e);
            throw new Exception("Error al obtener reseña: " + e.getMessage());
        }
    }

    private void updateRestaurantRating(RestaurantEntity restaurant) {
        List<ReviewEntity> reviews = reviewRepository.findByRestaurant(restaurant);
        if (!reviews.isEmpty()) {
            double averageRating = reviews.stream()
                    .mapToInt(ReviewEntity::getRestaurantRating)
                    .average()
                    .orElse(0.0);
            
            restaurant.setRating(averageRating);
            restaurant.setTotalRatings(reviews.size());
            restaurantRepository.save(restaurant);
        }
    }
}

