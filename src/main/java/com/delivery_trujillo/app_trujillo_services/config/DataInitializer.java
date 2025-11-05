package com.delivery_trujillo.app_trujillo_services.config;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.RoleEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.repositories.RoleRepository;
import com.delivery_trujillo.app_trujillo_services.services.models.enums.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Componente de inicialización de datos.
 * Carga los roles en la base de datos al iniciar la aplicación si no existen.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("Iniciando inicialización de roles...");

        // Inicializar todos los roles definidos en el enum
        for (Role role : Role.values()) {
            if (!roleRepository.existsByRoleName(role)) {
                RoleEntity roleEntity = new RoleEntity(role, role.getDescription());
                roleRepository.save(roleEntity);
                LOGGER.info("✅ Rol creado: {} - {}", role.name(), role.getDescription());
            } else {
                LOGGER.debug("Rol ya existe: {} - {}", role.name(), role.getDescription());
            }
        }

        LOGGER.info("✅ Inicialización de roles completada.");
    }
}

