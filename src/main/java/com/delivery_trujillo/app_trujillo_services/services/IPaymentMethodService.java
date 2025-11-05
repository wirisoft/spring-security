package com.delivery_trujillo.app_trujillo_services.services;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.PaymentMethodEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.entities.UserEntity;

import java.util.List;
import java.util.Optional;

public interface IPaymentMethodService {
    
    PaymentMethodEntity createPaymentMethod(PaymentMethodEntity paymentMethod, UserEntity user);
    
    List<PaymentMethodEntity> getUserPaymentMethods(UserEntity user);
    
    Optional<PaymentMethodEntity> getPaymentMethodById(Long id, UserEntity user);
    
    PaymentMethodEntity updatePaymentMethod(Long id, PaymentMethodEntity paymentMethod, UserEntity user);
    
    void deletePaymentMethod(Long id, UserEntity user);
    
    PaymentMethodEntity setDefaultPaymentMethod(Long id, UserEntity user);
}

