package com.delivery_trujillo.app_trujillo_services.persistence.repositories;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.AddressEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, Long> {
    
    List<AddressEntity> findByUser(UserEntity user);
    
    Optional<AddressEntity> findByIdAndUser(Long id, UserEntity user);
    
    Optional<AddressEntity> findByUserAndIsDefaultTrue(UserEntity user);
    
    void deleteByIdAndUser(Long id, UserEntity user);
}

