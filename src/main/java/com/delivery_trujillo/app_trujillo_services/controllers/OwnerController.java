package com.delivery_trujillo.app_trujillo_services.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para el Panel de Control del Dueño (RF-23)
 * Solo accesible para usuarios con rol OWNER
 */
@RestController
@RequestMapping("/v1/owner")
public class OwnerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerController.class);

    /**
     * Panel de Control del Dueño
     * GET /v1/owner/dashboard
     * Requiere rol: OWNER
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        LOGGER.info("Accessing owner dashboard");
        
        // Ejemplo de métricas (RF-23: pedidos diarios, ingresos, etc.)
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("dailyOrders", 150);
        dashboard.put("dailyRevenue", 12500.50);
        dashboard.put("totalUsers", 1250);
        dashboard.put("activeRestaurants", 45);
        dashboard.put("activeDeliveryPersons", 30);
        
        return new ResponseEntity<>(dashboard, HttpStatus.OK);
    }

    /**
     * Métricas de pedidos
     * GET /v1/owner/metrics/orders
     * Requiere rol: OWNER
     */
    @GetMapping("/metrics/orders")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Map<String, Object>> getOrderMetrics() {
        LOGGER.info("Accessing order metrics");
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("today", 150);
        metrics.put("thisWeek", 1050);
        metrics.put("thisMonth", 4500);
        
        return new ResponseEntity<>(metrics, HttpStatus.OK);
    }
}

