package com.delivery_trujillo.app_trujillo_services.services.impl;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.UserEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.repositories.UserRepository;
import com.delivery_trujillo.app_trujillo_services.services.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserEntity createUser(UserEntity userEntity) {
        try {
            LOGGER.info("Creating new user with email: {}", userEntity.getEmail());
            return userRepository.save(userEntity);
        } catch (Exception e) {
            LOGGER.error("Error while creating user: {}", e.getMessage());
            throw new RuntimeException("Error creating user", e);
        }
    }

    @Override
    public List<UserEntity> getAllUsers() {
        try {
            LOGGER.info("Fetching all users");
            return userRepository.findAll();
        } catch (Exception e) {
            LOGGER.error("Error while fetching all users: {}", e.getMessage());
            throw new RuntimeException("Error fetching users", e);
        }
    }

    @Override
    public Optional<UserEntity> getUserById(Long id) {
        try {
            LOGGER.info("Fetching user by ID: {}", id);
            return userRepository.findById(id);
        } catch (Exception e) {
            LOGGER.error("Error while fetching user by ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error fetching user by ID", e);
        }
    }

    @Override
    public UserEntity updateUser(Long id, UserEntity newUser) {
        try {
            LOGGER.info("Updating user with ID: {}", id);
            Optional<UserEntity> existingUser = userRepository.findById(id);

            if (existingUser.isEmpty()) {
                LOGGER.warn("User with ID {} not found", id);
                throw new RuntimeException("User not found with ID: " + id);
            }

            UserEntity userToUpdate = existingUser.get();

            // Actualizar campos (sin actualizar la contraseña aquí)
            if (newUser.getFirstName() != null) {
                userToUpdate.setFirstName(newUser.getFirstName());
            }
            if (newUser.getLastName() != null) {
                userToUpdate.setLastName(newUser.getLastName());
            }
            if (newUser.getEmail() != null) {
                userToUpdate.setEmail(newUser.getEmail());
            }
            if (newUser.getPhoneNumber() != null) {
                userToUpdate.setPhoneNumber(newUser.getPhoneNumber());
            }

            return userRepository.save(userToUpdate);
        } catch (Exception e) {
            LOGGER.error("Error while updating user with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error updating user", e);
        }
    }

    @Override
    public HashMap<String, String> deleteUser(Long id) {
        try {
            LOGGER.info("Attempting to delete user with ID: {}", id);
            HashMap<String, String> response = new HashMap<>();

            Optional<UserEntity> user = userRepository.findById(id);

            if (user.isEmpty()) {
                LOGGER.warn("User with ID {} not found for deletion", id);
                response.put("error", "User not found");
                return response;
            }

            userRepository.deleteById(id);
            LOGGER.info("User with ID {} deleted successfully", id);
            response.put("message", "User deleted successfully");
            return response;
        } catch (Exception e) {
            LOGGER.error("Error while deleting user with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error deleting user", e);
        }
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        try {
            LOGGER.info("Finding user by email: {}", email);
            return userRepository.findByEmail(email);
        } catch (Exception e) {
            LOGGER.error("Error while finding user by email {}: {}", email, e.getMessage());
            throw new RuntimeException("Error finding user by email", e);
        }
    }
}