package com.delivery_trujillo.app_trujillo_services.services;

import com.delivery_trujillo.app_trujillo_services.services.models.dtos.ResponseDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.ReviewDTO;

import java.util.HashMap;
import java.util.List;

public interface IReviewService {
    
    ResponseDTO createReview(Long userId, ReviewDTO reviewDTO) throws Exception;
    
    List<HashMap<String, Object>> getRestaurantReviews(Long restaurantId) throws Exception;
    
    HashMap<String, Object> getUserReview(Long userId, Long orderId) throws Exception;
}

