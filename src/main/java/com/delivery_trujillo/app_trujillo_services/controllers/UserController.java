package com.delivery_trujillo.app_trujillo_services.controllers;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.UserEntity;
import com.delivery_trujillo.app_trujillo_services.services.IUserService;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.ResponseDTO;
import com.delivery_trujillo.app_trujillo_services.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * Subir foto de perfil (RF-04)
     * POST /v1/users/profile/photo
     */
    @PostMapping("/profile/photo")
    private ResponseEntity<ResponseDTO> uploadProfilePhoto(@RequestParam("file") MultipartFile file) throws Exception {
        try {
            Long userId = SecurityUtils.getCurrentUserOrThrow().getId();
            LOGGER.info("Uploading profile photo for user: {}", userId);
            
            ResponseDTO response = new ResponseDTO();

            if (file.isEmpty()) {
                response.setMessage("El archivo está vacío.");
                response.setNumOfErrors(1);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Validar tipo de archivo
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.setMessage("El archivo debe ser una imagen.");
                response.setNumOfErrors(1);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Validar tamaño (máximo 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                response.setMessage("El archivo no debe exceder 5MB.");
                response.setNumOfErrors(1);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // En una implementación real, aquí se guardaría el archivo en un servicio de almacenamiento
            // (S3, Google Cloud Storage, etc.) y se devolvería la URL
            // Por ahora, simulamos guardando la URL
            String fileName = "profile_" + userId + "_" + System.currentTimeMillis() + 
                             "." + contentType.split("/")[1];
            String photoUrl = "/uploads/profiles/" + fileName;

            // Actualizar usuario con la URL de la foto
            Optional<UserEntity> userOpt = userService.getUserById(userId);
            if (userOpt.isPresent()) {
                UserEntity user = userOpt.get();
                user.setProfilePhotoUrl(photoUrl);
                userService.updateUser(userId, user);
            }

            response.setMessage("Foto de perfil subida exitosamente.");
            response.setNumOfErrors(0);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Error uploading profile photo: {}", e.getMessage());
            ResponseDTO response = new ResponseDTO();
            response.setMessage("Error al subir foto de perfil: " + e.getMessage());
            response.setNumOfErrors(1);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtener perfil del usuario actual (RF-04)
     * GET /v1/users/profile
     */
    @GetMapping("/profile")
    private ResponseEntity<UserEntity> getProfile() throws Exception {
        Long userId = SecurityUtils.getCurrentUserOrThrow().getId();
        LOGGER.info("Getting profile for user: {}", userId);
        Optional<UserEntity> user = userService.getUserById(userId);
        if (user.isPresent()) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Obtener trabajadores por rol (RF-22)
     * GET /v1/users/workers?role=DELIVERY
     */
    @GetMapping("/workers")
    @PreAuthorize("hasRole('OWNER') or hasRole('SUPPORT')")
    private ResponseEntity<List<UserEntity>> getWorkers(@RequestParam(required = false) String role) {
        try {
            LOGGER.info("Fetching workers with role: {}", role);
            List<UserEntity> users = userService.getAllUsers();
            
            if (role != null && !role.isEmpty()) {
                users = users.stream()
                    .filter(user -> user.getRole() != null && 
                             user.getRole().getRoleName().name().equals(role.toUpperCase()))
                    .toList();
            } else {
                // Filtrar solo trabajadores (DELIVERY, SUPPORT, RESTAURANT)
                users = users.stream()
                    .filter(user -> user.getRole() != null && 
                             (user.getRole().getRoleName().name().equals("DELIVERY") ||
                              user.getRole().getRoleName().name().equals("SUPPORT") ||
                              user.getRole().getRoleName().name().equals("RESTAURANT")))
                    .toList();
            }
            
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Error fetching workers: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtener trabajador por ID (RF-22)
     * GET /v1/users/workers/{id}
     */
    @GetMapping("/workers/{id}")
    @PreAuthorize("hasRole('OWNER') or hasRole('SUPPORT')")
    private ResponseEntity<UserEntity> getWorkerById(@PathVariable Long id) {
        try {
            LOGGER.info("Fetching worker with ID: {}", id);
            Optional<UserEntity> user = userService.getUserById(id);
            
            if (user.isPresent()) {
                // Verificar que sea un trabajador
                String roleName = user.get().getRole() != null ? 
                    user.get().getRole().getRoleName().name() : "";
                if (roleName.equals("DELIVERY") || roleName.equals("SUPPORT") || 
                    roleName.equals("RESTAURANT") || roleName.equals("OWNER")) {
                    return new ResponseEntity<>(user.get(), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            LOGGER.error("Error fetching worker by ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}