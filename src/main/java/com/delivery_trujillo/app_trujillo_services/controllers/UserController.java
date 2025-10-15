package com.delivery_trujillo.app_trujillo_services.controllers;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.UserEntity;
import com.delivery_trujillo.app_trujillo_services.services.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private IUserService userService;

    /**
     * Obtener todos los usuarios
     * GET /v1/users
     */
    @GetMapping
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        try {
            LOGGER.info("Fetching all users");
            List<UserEntity> users = userService.getAllUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Error fetching all users: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtener usuario por ID
     * GET /v1/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable Long id) {
        try {
            LOGGER.info("Fetching user with ID: {}", id);
            Optional<UserEntity> user = userService.getUserById(id);

            if (user.isPresent()) {
                return new ResponseEntity<>(user.get(), HttpStatus.OK);
            } else {
                LOGGER.warn("User with ID {} not found", id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            LOGGER.error("Error fetching user by ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtener usuario por email
     * GET /v1/users/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserEntity> getUserByEmail(@PathVariable String email) {
        try {
            LOGGER.info("Fetching user with email: {}", email);
            Optional<UserEntity> user = userService.findByEmail(email);

            if (user.isPresent()) {
                return new ResponseEntity<>(user.get(), HttpStatus.OK);
            } else {
                LOGGER.warn("User with email {} not found", email);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            LOGGER.error("Error fetching user by email {}: {}", email, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Crear nuevo usuario
     * POST /v1/users
     * Nota: Para registro público usar /v1/auth/register
     */
    @PostMapping
    public ResponseEntity<UserEntity> createUser(@RequestBody UserEntity user) {
        try {
            LOGGER.info("Creating new user with email: {}", user.getEmail());
            UserEntity createdUser = userService.createUser(user);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (Exception e) {
            LOGGER.error("Error creating user: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Actualizar usuario existente
     * PUT /v1/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserEntity> updateUser(
            @PathVariable Long id,
            @RequestBody UserEntity user) {
        try {
            LOGGER.info("Updating user with ID: {}", id);
            UserEntity updatedUser = userService.updateUser(id, user);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                LOGGER.warn("User with ID {} not found for update", id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            LOGGER.error("Error updating user with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Eliminar usuario
     * DELETE /v1/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HashMap<String, String>> deleteUser(@PathVariable Long id) {
        try {
            LOGGER.info("Deleting user with ID: {}", id);
            HashMap<String, String> response = userService.deleteUser(id);

            if (response.containsKey("error")) {
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Error deleting user with ID {}: {}", id, e.getMessage());
            HashMap<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error deleting user");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}