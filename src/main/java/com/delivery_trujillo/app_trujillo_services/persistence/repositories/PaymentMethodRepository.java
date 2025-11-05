package com.delivery_trujillo.app_trujillo_services.persistence.repositories;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.PaymentMethodEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethodEntity, Long> {
    
    List<PaymentMethodEntity> findByUserAndIsActiveTrue(UserEntity user);
    
    Optional<PaymentMethodEntity> findByIdAndUser(Long id, UserEntity user);
    
    Optional<PaymentMethodEntity> findByUserAndIsDefaultTrue(UserEntity user);
    
    void deleteByIdAndUser(Long id, UserEntity user);
}

