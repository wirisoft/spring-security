package com.delivery_trujillo.app_trujillo_services.services;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.UserEntity;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.LoginDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.ResponseDTO;

import java.util.HashMap;

public interface IAuthService {

    HashMap<String, Object> login(LoginDTO login) throws Exception;
    ResponseDTO register(UserEntity users) throws Exception;
}
