package com.delivery_trujillo.app_trujillo_services.persistence.repositories;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    // Verificar si existe por email (útil para validaciones)
    boolean existsByEmail(String email);
}