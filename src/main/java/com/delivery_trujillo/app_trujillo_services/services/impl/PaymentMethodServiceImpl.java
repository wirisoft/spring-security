package com.delivery_trujillo.app_trujillo_services.services.impl;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.PaymentMethodEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.entities.UserEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.repositories.PaymentMethodRepository;
import com.delivery_trujillo.app_trujillo_services.services.IPaymentMethodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentMethodServiceImpl implements IPaymentMethodService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentMethodServiceImpl.class);

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Override
    @Transactional
    public PaymentMethodEntity createPaymentMethod(PaymentMethodEntity paymentMethod, UserEntity user) {
        LOGGER.info("Creating payment method for user ID: {}", user.getId());
        paymentMethod.setUser(user);
        
        // Si se marca como default, establecerla como única default
        if (paymentMethod.getIsDefault() != null && paymentMethod.getIsDefault()) {
            setAsOnlyDefault(user);
        }
        
        return paymentMethodRepository.save(paymentMethod);
    }

    @Override
    public List<PaymentMethodEntity> getUserPaymentMethods(UserEntity user) {
        LOGGER.info("Fetching payment methods for user ID: {}", user.getId());
        return paymentMethodRepository.findByUserAndIsActiveTrue(user);
    }

    @Override
    public Optional<PaymentMethodEntity> getPaymentMethodById(Long id, UserEntity user) {
        LOGGER.info("Fetching payment method ID: {} for user ID: {}", id, user.getId());
        return paymentMethodRepository.findByIdAndUser(id, user);
    }

    @Override
    @Transactional
    public PaymentMethodEntity updatePaymentMethod(Long id, PaymentMethodEntity paymentMethodDetails, UserEntity user) {
        LOGGER.info("Updating payment method ID: {} for user ID: {}", id, user.getId());
        PaymentMethodEntity paymentMethod = paymentMethodRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Payment method not found"));

        paymentMethod.setType(paymentMethodDetails.getType());
        paymentMethod.setCardholderName(paymentMethodDetails.getCardholderName());
        paymentMethod.setLastFourDigits(paymentMethodDetails.getLastFourDigits());
        paymentMethod.setIsActive(paymentMethodDetails.getIsActive());

        if (paymentMethodDetails.getIsDefault() != null && paymentMethodDetails.getIsDefault()) {
            setAsOnlyDefault(user);
            paymentMethod.setIsDefault(true);
        }

        return paymentMethodRepository.save(paymentMethod);
    }

    @Override
    @Transactional
    public void deletePaymentMethod(Long id, UserEntity user) {
        LOGGER.info("Deleting payment method ID: {} for user ID: {}", id, user.getId());
        PaymentMethodEntity paymentMethod = paymentMethodRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Payment method not found"));
        
        // Soft delete
        paymentMethod.setIsActive(false);
        paymentMethodRepository.save(paymentMethod);
    }

    @Override
    @Transactional
    public PaymentMethodEntity setDefaultPaymentMethod(Long id, UserEntity user) {
        LOGGER.info("Setting payment method ID: {} as default for user ID: {}", id, user.getId());
        PaymentMethodEntity paymentMethod = paymentMethodRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Payment method not found"));

        setAsOnlyDefault(user);
        paymentMethod.setIsDefault(true);
        return paymentMethodRepository.save(paymentMethod);
    }

    private void setAsOnlyDefault(UserEntity user) {
        Optional<PaymentMethodEntity> currentDefault = paymentMethodRepository.findByUserAndIsDefaultTrue(user);
        if (currentDefault.isPresent()) {
            currentDefault.get().setIsDefault(false);
            paymentMethodRepository.save(currentDefault.get());
        }
    }
}

