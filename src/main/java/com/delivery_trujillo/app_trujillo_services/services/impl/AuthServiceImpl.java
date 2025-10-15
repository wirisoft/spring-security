package com.delivery_trujillo.app_trujillo_services.services.impl;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.UserEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.repositories.UserRepository;
import com.delivery_trujillo.app_trujillo_services.services.IAuthService;
import com.delivery_trujillo.app_trujillo_services.services.IJWTUtilityService;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.LoginDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.ResponseDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.validations.UserValidations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
public class AuthServiceImpl implements IAuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IJWTUtilityService jwtUtilityService;

    @Autowired
    private UserValidations userValidation;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public HashMap<String, Object> login(LoginDTO login) throws Exception {
        try {
            LOGGER.info("Processing login for email: {}", login.getEmail());
            HashMap<String, Object> jwt = new HashMap<>();
            Optional<UserEntity> users = userRepository.findByEmail(login.getEmail());

            if (users.isEmpty()) {
                LOGGER.warn("Login failed: User not registered for email: {}", login.getEmail());
                jwt.put("error", "User not registered!");
                return jwt;
            }

            UserEntity user = users.get();
            LOGGER.info("User found: ID={}, email={}", user.getId(), user.getEmail());

            // Verificar la contraseña usando el PasswordEncoder inyectado
            if (passwordEncoder.matches(login.getPassword(), user.getPassword())) {
                LOGGER.info("Password verification successful for user: {}", user.getId());
                String token = jwtUtilityService.generateJWT(user.getId());
                LOGGER.info("JWT token generated successfully for user: {}", user.getId());

                jwt.put("jwt", token);
                jwt.put("userId", user.getId());
                jwt.put("email", user.getEmail());
                jwt.put("firstName", user.getFirstName());
                jwt.put("lastName", user.getLastName());
            } else {
                LOGGER.warn("Login failed: Password verification failed for email: {}", login.getEmail());
                jwt.put("error", "Authentication failed");
            }
            return jwt;
        } catch (Exception e) {
            LOGGER.error("Error during login process: {}", e.getMessage(), e);
            throw new Exception("Login process failed: " + e.getMessage());
        }
    }

    @Override
    public ResponseDTO register(UserEntity users) throws Exception {
        try {
            LOGGER.info("Processing registration for email: {}", users.getEmail());
            ResponseDTO response = userValidation.validate(users);

            if (response.getNumOfErrors() > 0) {
                LOGGER.warn("Registration validation failed with {} errors", response.getNumOfErrors());
                return response;
            }

            // Verificar si el email ya existe
            Optional<UserEntity> existingUser = userRepository.findByEmail(users.getEmail());
            if (existingUser.isPresent()) {
                LOGGER.warn("Registration failed: Email already exists: {}", users.getEmail());
                response.setMessage("Email already exists!");
                response.setNumOfErrors(1);
                return response;
            }

            // Encriptar contraseña usando el PasswordEncoder inyectado
            users.setPassword(passwordEncoder.encode(users.getPassword()));

            UserEntity savedUser = userRepository.save(users);
            LOGGER.info("User created successfully with ID: {}", savedUser.getId());

            response.setMessage("User created successfully!");
            return response;
        } catch (Exception e) {
            LOGGER.error("Error during registration process: {}", e.getMessage(), e);
            throw new Exception("Registration process failed: " + e.getMessage());
        }
    }
}