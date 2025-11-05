package com.delivery_trujillo.app_trujillo_services.persistence.repositories;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.RoleEntity;
import com.delivery_trujillo.app_trujillo_services.services.models.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    
    Optional<RoleEntity> findByRoleName(Role roleName);
    
    boolean existsByRoleName(Role roleName);
}

