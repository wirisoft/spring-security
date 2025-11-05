package com.delivery_trujillo.app_trujillo_services.services;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.OrderEntity;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.CheckoutDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.ResponseDTO;

import java.util.HashMap;
import java.util.List;

public interface IOrderService {
    
    HashMap<String, Object> checkout(Long userId, CheckoutDTO checkoutDTO) throws Exception;
    
    List<HashMap<String, Object>> getOrderHistory(Long userId) throws Exception;
    
    HashMap<String, Object> getOrderStatus(Long userId, Long orderId) throws Exception;
    
    ResponseDTO updateOrderStatus(Long orderId, OrderEntity.OrderStatus newStatus) throws Exception;
}

