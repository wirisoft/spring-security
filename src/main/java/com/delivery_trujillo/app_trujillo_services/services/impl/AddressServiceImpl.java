package com.delivery_trujillo.app_trujillo_services.services.impl;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.AddressEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.entities.UserEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.repositories.AddressRepository;
import com.delivery_trujillo.app_trujillo_services.services.IAddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AddressServiceImpl implements IAddressService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddressServiceImpl.class);

    @Autowired
    private AddressRepository addressRepository;

    @Override
    @Transactional
    public AddressEntity createAddress(AddressEntity address, UserEntity user) {
        LOGGER.info("Creating address for user ID: {}", user.getId());
        address.setUser(user);
        
        // Si es la primera dirección o se marca como default, establecerla como default
        if (address.getIsDefault() == null || address.getIsDefault()) {
            setAsOnlyDefault(user);
        }
        
        return addressRepository.save(address);
    }

    @Override
    public List<AddressEntity> getUserAddresses(UserEntity user) {
        LOGGER.info("Fetching addresses for user ID: {}", user.getId());
        return addressRepository.findByUser(user);
    }

    @Override
    public Optional<AddressEntity> getAddressById(Long id, UserEntity user) {
        LOGGER.info("Fetching address ID: {} for user ID: {}", id, user.getId());
        return addressRepository.findByIdAndUser(id, user);
    }

    @Override
    @Transactional
    public AddressEntity updateAddress(Long id, AddressEntity addressDetails, UserEntity user) {
        LOGGER.info("Updating address ID: {} for user ID: {}", id, user.getId());
        AddressEntity address = addressRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        address.setName(addressDetails.getName());
        address.setStreet(addressDetails.getStreet());
        address.setCity(addressDetails.getCity());
        address.setState(addressDetails.getState());
        address.setZipCode(addressDetails.getZipCode());
        address.setReference(addressDetails.getReference());
        address.setLatitude(addressDetails.getLatitude());
        address.setLongitude(addressDetails.getLongitude());

        if (addressDetails.getIsDefault() != null && addressDetails.getIsDefault()) {
            setAsOnlyDefault(user);
            address.setIsDefault(true);
        }

        return addressRepository.save(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long id, UserEntity user) {
        LOGGER.info("Deleting address ID: {} for user ID: {}", id, user.getId());
        if (!addressRepository.findByIdAndUser(id, user).isPresent()) {
            throw new RuntimeException("Address not found");
        }
        addressRepository.deleteByIdAndUser(id, user);
    }

    @Override
    @Transactional
    public AddressEntity setDefaultAddress(Long id, UserEntity user) {
        LOGGER.info("Setting address ID: {} as default for user ID: {}", id, user.getId());
        AddressEntity address = addressRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        setAsOnlyDefault(user);
        address.setIsDefault(true);
        return addressRepository.save(address);
    }

    private void setAsOnlyDefault(UserEntity user) {
        Optional<AddressEntity> currentDefault = addressRepository.findByUserAndIsDefaultTrue(user);
        if (currentDefault.isPresent()) {
            currentDefault.get().setIsDefault(false);
            addressRepository.save(currentDefault.get());
        }
    }
}

