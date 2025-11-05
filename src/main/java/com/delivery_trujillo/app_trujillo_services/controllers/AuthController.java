package com.delivery_trujillo.app_trujillo_services.controllers;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.UserEntity;
import com.delivery_trujillo.app_trujillo_services.services.IAuthService;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.LoginDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.PasswordResetDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.PasswordResetRequestDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.ResponseDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.enums.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private IAuthService authService;

    /**
     * Registro de Cliente
     * POST /v1/auth/register/customer
     */
    @PostMapping("/register/customer")
    private ResponseEntity<ResponseDTO> registerCustomer(@RequestBody UserEntity user) throws Exception {
        LOGGER.info("Registering new customer with email: {}", user.getEmail());
        return new ResponseEntity<>(authService.register(user, Role.CUSTOMER), HttpStatus.CREATED);
    }

    /**
     * Registro de Repartidor
     * POST /v1/auth/register/delivery
     */
    @PostMapping("/register/delivery")
    private ResponseEntity<ResponseDTO> registerDelivery(@RequestBody UserEntity user) throws Exception {
        LOGGER.info("Registering new delivery person with email: {}", user.getEmail());
        return new ResponseEntity<>(authService.register(user, Role.DELIVERY), HttpStatus.CREATED);
    }

    /**
     * Registro de Personal de Soporte
     * POST /v1/auth/register/support
     */
    @PostMapping("/register/support")
    private ResponseEntity<ResponseDTO> registerSupport(@RequestBody UserEntity user) throws Exception {
        LOGGER.info("Registering new support person with email: {}", user.getEmail());
        return new ResponseEntity<>(authService.register(user, Role.SUPPORT), HttpStatus.CREATED);
    }

    /**
     * Registro de Dueño
     * POST /v1/auth/register/owner
     */
    @PostMapping("/register/owner")
    private ResponseEntity<ResponseDTO> registerOwner(@RequestBody UserEntity user) throws Exception {
        LOGGER.info("Registering new owner with email: {}", user.getEmail());
        return new ResponseEntity<>(authService.register(user, Role.OWNER), HttpStatus.CREATED);
    }

    /**
     * Registro de Restaurante
     * POST /v1/auth/register/restaurant
     */
    @PostMapping("/register/restaurant")
    private ResponseEntity<ResponseDTO> registerRestaurant(@RequestBody UserEntity user) throws Exception {
        LOGGER.info("Registering new restaurant with email: {}", user.getEmail());
        return new ResponseEntity<>(authService.register(user, Role.RESTAURANT), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    private ResponseEntity<HashMap<String, Object>> login(@RequestBody LoginDTO loginRequest) throws Exception {
        HashMap<String, Object> loginResponse = authService.login(loginRequest);

        if (loginResponse.containsKey("jwt")) {
            return new ResponseEntity<>(loginResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(loginResponse, HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Solicitar recuperación de contraseña (RF-03)
     * POST /v1/auth/forgot-password
     */
    @PostMapping("/forgot-password")
    private ResponseEntity<ResponseDTO> forgotPassword(@RequestBody PasswordResetRequestDTO request) throws Exception {
        LOGGER.info("Password reset request for email: {}", request.getEmail());
        return new ResponseEntity<>(authService.requestPasswordReset(request), HttpStatus.OK);
    }

    /**
     * Restablecer contraseña con token (RF-03)
     * POST /v1/auth/reset-password
     */
    @PostMapping("/reset-password")
    private ResponseEntity<ResponseDTO> resetPassword(@RequestBody PasswordResetDTO resetDTO) throws Exception {
        LOGGER.info("Password reset with token");
        return new ResponseEntity<>(authService.resetPassword(resetDTO), HttpStatus.OK);
    }
}