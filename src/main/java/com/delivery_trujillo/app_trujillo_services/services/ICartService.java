package com.delivery_trujillo.app_trujillo_services.services;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.CartEntity;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.CartItemDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.ResponseDTO;

import java.util.HashMap;

public interface ICartService {
    
    HashMap<String, Object> getCart(Long userId) throws Exception;
    
    ResponseDTO addItemToCart(Long userId, CartItemDTO item) throws Exception;
    
    ResponseDTO updateCartItem(Long userId, Long cartItemId, Integer quantity) throws Exception;
    
    ResponseDTO removeCartItem(Long userId, Long cartItemId) throws Exception;
    
    ResponseDTO clearCart(Long userId) throws Exception;
}

