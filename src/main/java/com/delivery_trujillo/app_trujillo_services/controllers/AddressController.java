package com.delivery_trujillo.app_trujillo_services.controllers;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.AddressEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.entities.UserEntity;
import com.delivery_trujillo.app_trujillo_services.services.IAddressService;
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
 * Controlador para gestión de direcciones (RF-05)
 */
@RestController
@RequestMapping("/v1/addresses")
public class AddressController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddressController.class);

    @Autowired
    private IAddressService addressService;

    /**
     * Obtener todas las direcciones del usuario autenticado
     * GET /v1/addresses
     */
    @GetMapping
    public ResponseEntity<List<AddressEntity>> getUserAddresses() {
        try {
            UserEntity user = SecurityUtils.getCurrentUserOrThrow();
            LOGGER.info("Fetching addresses for user ID: {}", user.getId());
            List<AddressEntity> addresses = addressService.getUserAddresses(user);
            return new ResponseEntity<>(addresses, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Error fetching addresses: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtener dirección por ID
     * GET /v1/addresses/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<AddressEntity> getAddressById(@PathVariable Long id) {
        try {
            UserEntity user = SecurityUtils.getCurrentUserOrThrow();
            Optional<AddressEntity> address = addressService.getAddressById(id, user);
            if (address.isPresent()) {
                return new ResponseEntity<>(address.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            LOGGER.error("Error fetching address: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Crear nueva dirección
     * POST /v1/addresses
     */
    @PostMapping
    public ResponseEntity<AddressEntity> createAddress(@RequestBody AddressEntity address) {
        try {
            UserEntity user = SecurityUtils.getCurrentUserOrThrow();
            LOGGER.info("Creating address for user ID: {}", user.getId());
            AddressEntity createdAddress = addressService.createAddress(address, user);
            return new ResponseEntity<>(createdAddress, HttpStatus.CREATED);
        } catch (Exception e) {
            LOGGER.error("Error creating address: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Actualizar dirección
     * PUT /v1/addresses/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<AddressEntity> updateAddress(@PathVariable Long id, @RequestBody AddressEntity address) {
        try {
            UserEntity user = SecurityUtils.getCurrentUserOrThrow();
            LOGGER.info("Updating address ID: {} for user ID: {}", id, user.getId());
            AddressEntity updatedAddress = addressService.updateAddress(id, address, user);
            return new ResponseEntity<>(updatedAddress, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            LOGGER.error("Error updating address: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Eliminar dirección
     * DELETE /v1/addresses/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HashMap<String, String>> deleteAddress(@PathVariable Long id) {
        try {
            UserEntity user = SecurityUtils.getCurrentUserOrThrow();
            LOGGER.info("Deleting address ID: {} for user ID: {}", id, user.getId());
            addressService.deleteAddress(id, user);
            HashMap<String, String> response = new HashMap<>();
            response.put("message", "Address deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            LOGGER.error("Error deleting address: {}", e.getMessage());
            HashMap<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error deleting address");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Establecer dirección como predeterminada
     * PUT /v1/addresses/{id}/default
     */
    @PutMapping("/{id}/default")
    public ResponseEntity<AddressEntity> setDefaultAddress(@PathVariable Long id) {
        try {
            UserEntity user = SecurityUtils.getCurrentUserOrThrow();
            LOGGER.info("Setting address ID: {} as default for user ID: {}", id, user.getId());
            AddressEntity address = addressService.setDefaultAddress(id, user);
            return new ResponseEntity<>(address, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            LOGGER.error("Error setting default address: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

