package com.delivery_trujillo.app_trujillo_services.services.impl;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.PasswordResetTokenEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.entities.RoleEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.entities.UserEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.repositories.PasswordResetTokenRepository;
import com.delivery_trujillo.app_trujillo_services.persistence.repositories.RoleRepository;
import com.delivery_trujillo.app_trujillo_services.persistence.repositories.UserRepository;
import com.delivery_trujillo.app_trujillo_services.services.IAuthService;
import com.delivery_trujillo.app_trujillo_services.services.IJWTUtilityService;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.LoginDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.PasswordResetDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.PasswordResetRequestDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.ResponseDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.enums.Role;
import com.delivery_trujillo.app_trujillo_services.services.models.validations.UserValidations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired(required = false)
    private JavaMailSender mailSender;

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
                
                // Obtener roles del usuario
                List<String> roles = new ArrayList<>();
                if (user.getRole() != null) {
                    roles.add(user.getRole().getRoleName().name());
                }
                
                String token = jwtUtilityService.generateJWT(user.getId(), roles);
                LOGGER.info("JWT token generated successfully for user: {} with roles: {}", user.getId(), roles);

                jwt.put("jwt", token);
                jwt.put("userId", user.getId());
                jwt.put("email", user.getEmail());
                jwt.put("firstName", user.getFirstName());
                jwt.put("lastName", user.getLastName());
                jwt.put("role", user.getRole() != null ? user.getRole().getRoleName().name() : null);
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
    public ResponseDTO register(UserEntity users, Role role) throws Exception {
        try {
            LOGGER.info("Processing registration for email: {} with role: {}", users.getEmail(), role);
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

            // Buscar o crear el rol
            RoleEntity roleEntity = roleRepository.findByRoleName(role)
                    .orElseThrow(() -> new Exception("Role not found: " + role.name()));

            // Encriptar contraseña usando el PasswordEncoder inyectado
            users.setPassword(passwordEncoder.encode(users.getPassword()));
            
            // Asignar el rol al usuario
            users.setRole(roleEntity);

            UserEntity savedUser = userRepository.save(users);
            LOGGER.info("User created successfully with ID: {} and role: {}", savedUser.getId(), role);

            response.setMessage("User created successfully with role: " + role.getDescription() + "!");
            return response;
        } catch (Exception e) {
            LOGGER.error("Error during registration process: {}", e.getMessage(), e);
            throw new Exception("Registration process failed: " + e.getMessage());
        }
    }

    @Override
    public ResponseDTO requestPasswordReset(PasswordResetRequestDTO request) throws Exception {
        try {
            LOGGER.info("Processing password reset request for email: {}", request.getEmail());
            ResponseDTO response = new ResponseDTO();

            Optional<UserEntity> userOpt = userRepository.findByEmail(request.getEmail());
            if (userOpt.isEmpty()) {
                LOGGER.warn("Password reset request failed: User not found for email: {}", request.getEmail());
                // Por seguridad, no revelamos si el email existe o no
                response.setMessage("Si el email existe, se enviará un enlace de recuperación.");
                response.setNumOfErrors(0);
                return response;
            }

            UserEntity user = userOpt.get();

            // Generar token único
            String token = UUID.randomUUID().toString();
            LocalDateTime expiryDate = LocalDateTime.now().plusHours(24); // Token válido por 24 horas

            // Invalidar tokens anteriores del usuario (no usados)
            // Nota: Por simplicidad, solo invalidamos tokens no usados del mismo usuario
            // En producción, podrías agregar un método en el repositorio para buscar por usuario
            List<PasswordResetTokenEntity> existingTokens = passwordResetTokenRepository.findAll();
            existingTokens.stream()
                    .filter(t -> t.getUser().getId().equals(user.getId()) && !t.getUsed())
                    .forEach(t -> {
                        t.setUsed(true);
                        passwordResetTokenRepository.save(t);
                    });

            // Crear nuevo token
            PasswordResetTokenEntity resetToken = new PasswordResetTokenEntity();
            resetToken.setToken(token);
            resetToken.setUser(user);
            resetToken.setExpiryDate(expiryDate);
            resetToken.setUsed(false);
            passwordResetTokenRepository.save(resetToken);

            // Enviar email (si está configurado)
            if (mailSender != null) {
                try {
                    String resetUrl = "http://localhost:8080/v1/auth/reset-password?token=" + token;
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setTo(user.getEmail());
                    message.setSubject("Recuperación de Contraseña - Delivery Trujillo");
                    message.setText("Hola " + user.getFirstName() + ",\n\n" +
                            "Has solicitado restablecer tu contraseña. " +
                            "Haz clic en el siguiente enlace para continuar:\n\n" +
                            resetUrl + "\n\n" +
                            "Este enlace expirará en 24 horas.\n\n" +
                            "Si no solicitaste este cambio, ignora este mensaje.");
                    mailSender.send(message);
                    LOGGER.info("Password reset email sent successfully to: {}", user.getEmail());
                } catch (Exception e) {
                    LOGGER.error("Error sending password reset email: {}", e.getMessage());
                    // No fallar si el email no se puede enviar, el token ya está guardado
                }
            } else {
                LOGGER.warn("JavaMailSender not configured. Password reset token generated but email not sent. Token: {}", token);
            }

            response.setMessage("Si el email existe, se enviará un enlace de recuperación.");
            response.setNumOfErrors(0);
            return response;
        } catch (Exception e) {
            LOGGER.error("Error during password reset request: {}", e.getMessage(), e);
            throw new Exception("Password reset request failed: " + e.getMessage());
        }
    }

    @Override
    public ResponseDTO resetPassword(PasswordResetDTO resetDTO) throws Exception {
        try {
            LOGGER.info("Processing password reset with token");
            ResponseDTO response = new ResponseDTO();

            Optional<PasswordResetTokenEntity> tokenOpt = passwordResetTokenRepository.findByTokenAndUsedFalse(resetDTO.getToken());
            if (tokenOpt.isEmpty()) {
                LOGGER.warn("Password reset failed: Invalid or expired token");
                response.setMessage("Token inválido o expirado.");
                response.setNumOfErrors(1);
                return response;
            }

            PasswordResetTokenEntity resetToken = tokenOpt.get();

            // Verificar si el token ha expirado
            if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                LOGGER.warn("Password reset failed: Token expired");
                resetToken.setUsed(true);
                passwordResetTokenRepository.save(resetToken);
                response.setMessage("El token ha expirado. Por favor, solicita uno nuevo.");
                response.setNumOfErrors(1);
                return response;
            }

            // Validar nueva contraseña
            if (resetDTO.getNewPassword() == null || resetDTO.getNewPassword().trim().isEmpty()) {
                response.setMessage("La contraseña no puede estar vacía.");
                response.setNumOfErrors(1);
                return response;
            }

            if (resetDTO.getNewPassword().length() < 6) {
                response.setMessage("La contraseña debe tener al menos 6 caracteres.");
                response.setNumOfErrors(1);
                return response;
            }

            // Actualizar contraseña del usuario
            UserEntity user = resetToken.getUser();
            user.setPassword(passwordEncoder.encode(resetDTO.getNewPassword()));
            userRepository.save(user);

            // Marcar token como usado
            resetToken.setUsed(true);
            passwordResetTokenRepository.save(resetToken);

            LOGGER.info("Password reset successful for user: {}", user.getEmail());
            response.setMessage("Contraseña restablecida exitosamente.");
            response.setNumOfErrors(0);
            return response;
        } catch (Exception e) {
            LOGGER.error("Error during password reset: {}", e.getMessage(), e);
            throw new Exception("Password reset failed: " + e.getMessage());
        }
    }
}