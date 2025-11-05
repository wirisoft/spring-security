package com.delivery_trujillo.app_trujillo_services.services;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.UserEntity;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.LoginDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.PasswordResetDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.PasswordResetRequestDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.ResponseDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.enums.Role;

import java.util.HashMap;

public interface IAuthService {

    HashMap<String, Object> login(LoginDTO login) throws Exception;
    ResponseDTO register(UserEntity users, Role role) throws Exception;
    ResponseDTO requestPasswordReset(PasswordResetRequestDTO request) throws Exception;
    ResponseDTO resetPassword(PasswordResetDTO resetDTO) throws Exception;
}
