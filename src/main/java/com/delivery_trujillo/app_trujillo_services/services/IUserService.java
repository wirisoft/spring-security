package com.delivery_trujillo.app_trujillo_services.services;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.UserEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface IUserService {

    public UserEntity createUser(UserEntity userEntity);
    public List<UserEntity> getAllUsers();
    public Optional<UserEntity> getUserById(Long id);
    public UserEntity updateUser(Long id, UserEntity newUser);
    public HashMap<String,String> deleteUser(Long id);

    public Optional<UserEntity> findByEmail(String email);
}
