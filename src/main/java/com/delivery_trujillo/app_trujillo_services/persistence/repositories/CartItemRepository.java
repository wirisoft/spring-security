package com.delivery_trujillo.app_trujillo_services.persistence.repositories;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.CartEntity;
import com.delivery_trujillo.app_trujillo_services.persistence.entities.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    
    List<CartItemEntity> findByCart(CartEntity cart);
    
    Optional<CartItemEntity> findByCartAndMenuItem_Id(CartEntity cart, Long menuItemId);
    
    void deleteByCart(CartEntity cart);
}

