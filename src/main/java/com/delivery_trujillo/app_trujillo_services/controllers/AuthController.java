package com.delivery_trujillo.app_trujillo_services.controllers;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.UserEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.repositories.UserRepository;
import com.delivery_trujillo.app_trujillo_services.services.IAuthService;
import com.delivery_trujillo.app_trujillo_services.services.IUserService;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.LoginDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.ResponseDTO;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IUserService userService;

    @PostMapping("/register")
    private ResponseEntity<ResponseDTO> addUser(@RequestBody UserEntity user) throws Exception {
        return new ResponseEntity<>(authService.register(user), HttpStatus.OK);
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
}