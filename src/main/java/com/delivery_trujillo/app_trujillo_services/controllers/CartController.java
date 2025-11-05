package com.delivery_trujillo.app_trujillo_services.controllers;

import com.delivery_trujillo.app_trujillo_services.services.ICartService;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.CartItemDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.ResponseDTO;
import com.delivery_trujillo.app_trujillo_services.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * Controlador para carrito de compras (RF-13)
 */
@RestController
@RequestMapping("/v1/cart")
public class CartController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private ICartService cartService;

    /**
     * Obtener carrito del usuario actual
     * GET /v1/cart
     */
    @GetMapping
    private ResponseEntity<HashMap<String, Object>> getCart() throws Exception {
        Long userId = SecurityUtils.getCurrentUserOrThrow().getId();
        LOGGER.info("Getting cart for user: {}", userId);
        return new ResponseEntity<>(cartService.getCart(userId), HttpStatus.OK);
    }

    /**
     * Agregar item al carrito
     * POST /v1/cart/items
     */
    @PostMapping("/items")
    private ResponseEntity<ResponseDTO> addItemToCart(@RequestBody CartItemDTO item) throws Exception {
        Long userId = SecurityUtils.getCurrentUserOrThrow().getId();
        LOGGER.info("Adding item to cart for user: {}", userId);
        return new ResponseEntity<>(cartService.addItemToCart(userId, item), HttpStatus.OK);
    }

    /**
     * Actualizar cantidad de un item en el carrito
     * PUT /v1/cart/items/{cartItemId}
     */
    @PutMapping("/items/{cartItemId}")
    private ResponseEntity<ResponseDTO> updateCartItem(
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) throws Exception {
        Long userId = SecurityUtils.getCurrentUserOrThrow().getId();
        LOGGER.info("Updating cart item: {} for user: {}", cartItemId, userId);
        return new ResponseEntity<>(cartService.updateCartItem(userId, cartItemId, quantity), HttpStatus.OK);
    }

    /**
     * Eliminar item del carrito
     * DELETE /v1/cart/items/{cartItemId}
     */
    @DeleteMapping("/items/{cartItemId}")
    private ResponseEntity<ResponseDTO> removeCartItem(@PathVariable Long cartItemId) throws Exception {
        Long userId = SecurityUtils.getCurrentUserOrThrow().getId();
        LOGGER.info("Removing cart item: {} for user: {}", cartItemId, userId);
        return new ResponseEntity<>(cartService.removeCartItem(userId, cartItemId), HttpStatus.OK);
    }

    /**
     * Vaciar carrito
     * DELETE /v1/cart
     */
    @DeleteMapping
    private ResponseEntity<ResponseDTO> clearCart() throws Exception {
        Long userId = SecurityUtils.getCurrentUserOrThrow().getId();
        LOGGER.info("Clearing cart for user: {}", userId);
        return new ResponseEntity<>(cartService.clearCart(userId), HttpStatus.OK);
    }
}

