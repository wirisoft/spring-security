package com.delivery_trujillo.app_trujillo_services.config;

import com.delivery_trujillo.app_trujillo_services.services.models.validations.UserValidations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
@EnableScheduling
public class ValidationsConfig {

    @Bean
    public UserValidations userValidations(){
        return new UserValidations();
    }
}
