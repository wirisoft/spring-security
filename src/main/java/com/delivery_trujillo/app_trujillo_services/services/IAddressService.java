package com.delivery_trujillo.app_trujillo_services.services;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.AddressEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.entities.UserEntity;

import java.util.List;
import java.util.Optional;

public interface IAddressService {
    
    AddressEntity createAddress(AddressEntity address, UserEntity user);
    
    List<AddressEntity> getUserAddresses(UserEntity user);
    
    Optional<AddressEntity> getAddressById(Long id, UserEntity user);
    
    AddressEntity updateAddress(Long id, AddressEntity address, UserEntity user);
    
    void deleteAddress(Long id, UserEntity user);
    
    AddressEntity setDefaultAddress(Long id, UserEntity user);
}

