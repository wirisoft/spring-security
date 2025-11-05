package com.delivery_trujillo.app_trujillo_services.controllers;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.OrderEntity;
import com.delivery_trujillo.app_trujillo_services.services.IOrderService;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.CheckoutDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.ResponseDTO;
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
 * Controlador para pedidos (RF-15, RF-17, RF-18)
 */
@RestController
@RequestMapping("/v1/orders")
public class OrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService orderService;

    /**
     * Proceso de checkout (RF-15)
     * POST /v1/orders/checkout
     */
    @PostMapping("/checkout")
    private ResponseEntity<HashMap<String, Object>> checkout(@RequestBody CheckoutDTO checkoutDTO) throws Exception {
        Long userId = SecurityUtils.getCurrentUserOrThrow().getId();
        LOGGER.info("Processing checkout for user: {}", userId);
        return new ResponseEntity<>(orderService.checkout(userId, checkoutDTO), HttpStatus.CREATED);
    }

    /**
     * Historial de pedidos del usuario (RF-17)
     * GET /v1/orders/history
     */
    @GetMapping("/history")
    private ResponseEntity<List<HashMap<String, Object>>> getOrderHistory() throws Exception {
        Long userId = SecurityUtils.getCurrentUserOrThrow().getId();
        LOGGER.info("Getting order history for user: {}", userId);
        return new ResponseEntity<>(orderService.getOrderHistory(userId), HttpStatus.OK);
    }

    /**
     * Seguimiento en tiempo real de pedido (RF-18)
     * GET /v1/orders/{orderId}/status
     */
    @GetMapping("/{orderId}/status")
    private ResponseEntity<HashMap<String, Object>> getOrderStatus(@PathVariable Long orderId) throws Exception {
        Long userId = SecurityUtils.getCurrentUserOrThrow().getId();
        LOGGER.info("Getting order status for order: {} and user: {}", orderId, userId);
        return new ResponseEntity<>(orderService.getOrderStatus(userId, orderId), HttpStatus.OK);
    }

    /**
     * Actualizar estado del pedido (para restaurantes/repartidores)
     * PUT /v1/orders/{orderId}/status
     */
    @PutMapping("/{orderId}/status")
    private ResponseEntity<ResponseDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) throws Exception {
        LOGGER.info("Updating order status for order: {} to status: {}", orderId, status);
        OrderEntity.OrderStatus orderStatus = OrderEntity.OrderStatus.valueOf(status.toUpperCase());
        return new ResponseEntity<>(orderService.updateOrderStatus(orderId, orderStatus), HttpStatus.OK);
    }
}

