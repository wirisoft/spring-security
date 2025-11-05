package com.delivery_trujillo.app_trujillo_services.controllers;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.PaymentMethodEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.entities.UserEntity;
import com.delivery_trujillo.app_trujillo_services.services.IPaymentMethodService;
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
 * Controlador para gestión de métodos de pago (RF-06)
 */
@RestController
@RequestMapping("/v1/payment-methods")
public class PaymentMethodController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentMethodController.class);

    @Autowired
    private IPaymentMethodService paymentMethodService;

    /**
     * Obtener todos los métodos de pago del usuario autenticado
     * GET /v1/payment-methods
     */
    @GetMapping
    public ResponseEntity<List<PaymentMethodEntity>> getUserPaymentMethods() {
        try {
            UserEntity user = SecurityUtils.getCurrentUserOrThrow();
            LOGGER.info("Fetching payment methods for user ID: {}", user.getId());
            List<PaymentMethodEntity> paymentMethods = paymentMethodService.getUserPaymentMethods(user);
            return new ResponseEntity<>(paymentMethods, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Error fetching payment methods: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtener método de pago por ID
     * GET /v1/payment-methods/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentMethodEntity> getPaymentMethodById(@PathVariable Long id) {
        try {
            UserEntity user = SecurityUtils.getCurrentUserOrThrow();
            Optional<PaymentMethodEntity> paymentMethod = paymentMethodService.getPaymentMethodById(id, user);
            if (paymentMethod.isPresent()) {
                return new ResponseEntity<>(paymentMethod.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            LOGGER.error("Error fetching payment method: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Crear nuevo método de pago
     * POST /v1/payment-methods
     */
    @PostMapping
    public ResponseEntity<PaymentMethodEntity> createPaymentMethod(@RequestBody PaymentMethodEntity paymentMethod) {
        try {
            UserEntity user = SecurityUtils.getCurrentUserOrThrow();
            LOGGER.info("Creating payment method for user ID: {}", user.getId());
            PaymentMethodEntity createdPaymentMethod = paymentMethodService.createPaymentMethod(paymentMethod, user);
            return new ResponseEntity<>(createdPaymentMethod, HttpStatus.CREATED);
        } catch (Exception e) {
            LOGGER.error("Error creating payment method: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Actualizar método de pago
     * PUT /v1/payment-methods/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<PaymentMethodEntity> updatePaymentMethod(@PathVariable Long id, @RequestBody PaymentMethodEntity paymentMethod) {
        try {
            UserEntity user = SecurityUtils.getCurrentUserOrThrow();
            LOGGER.info("Updating payment method ID: {} for user ID: {}", id, user.getId());
            PaymentMethodEntity updatedPaymentMethod = paymentMethodService.updatePaymentMethod(id, paymentMethod, user);
            return new ResponseEntity<>(updatedPaymentMethod, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            LOGGER.error("Error updating payment method: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Eliminar método de pago (soft delete)
     * DELETE /v1/payment-methods/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HashMap<String, String>> deletePaymentMethod(@PathVariable Long id) {
        try {
            UserEntity user = SecurityUtils.getCurrentUserOrThrow();
            LOGGER.info("Deleting payment method ID: {} for user ID: {}", id, user.getId());
            paymentMethodService.deletePaymentMethod(id, user);
            HashMap<String, String> response = new HashMap<>();
            response.put("message", "Payment method deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            LOGGER.error("Error deleting payment method: {}", e.getMessage());
            HashMap<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error deleting payment method");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Establecer método de pago como predeterminado
     * PUT /v1/payment-methods/{id}/default
     */
    @PutMapping("/{id}/default")
    public ResponseEntity<PaymentMethodEntity> setDefaultPaymentMethod(@PathVariable Long id) {
        try {
            UserEntity user = SecurityUtils.getCurrentUserOrThrow();
            LOGGER.info("Setting payment method ID: {} as default for user ID: {}", id, user.getId());
            PaymentMethodEntity paymentMethod = paymentMethodService.setDefaultPaymentMethod(id, user);
            return new ResponseEntity<>(paymentMethod, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            LOGGER.error("Error setting default payment method: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

