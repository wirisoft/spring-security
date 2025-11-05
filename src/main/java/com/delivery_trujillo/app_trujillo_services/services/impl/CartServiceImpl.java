package com.delivery_trujillo.app_trujillo_services.services.impl;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.*;
import com.delivery_trujillo.app_trujillo_services.persistence.repositories.*;
import com.delivery_trujillo.app_trujillo_services.services.ICartService;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.CartItemDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.ResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements ICartService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CartServiceImpl.class);

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Override
    public HashMap<String, Object> getCart(Long userId) throws Exception {
        try {
            LOGGER.info("Getting cart for user: {}", userId);
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));

            Optional<CartEntity> cartOpt = cartRepository.findByUser(user);
            HashMap<String, Object> response = new HashMap<>();

            if (cartOpt.isEmpty()) {
                response.put("cart", null);
                response.put("items", new ArrayList<>());
                response.put("subtotal", BigDecimal.ZERO);
                response.put("deliveryFee", BigDecimal.ZERO);
                response.put("total", BigDecimal.ZERO);
                return response;
            }

            CartEntity cart = cartOpt.get();
            List<CartItemEntity> items = cartItemRepository.findByCart(cart);

            List<HashMap<String, Object>> itemsData = new ArrayList<>();
            for (CartItemEntity item : items) {
                HashMap<String, Object> itemData = new HashMap<>();
                itemData.put("id", item.getId());
                itemData.put("menuItemId", item.getMenuItem().getId());
                itemData.put("menuItemName", item.getMenuItem().getName());
                itemData.put("quantity", item.getQuantity());
                itemData.put("unitPrice", item.getUnitPrice());
                itemData.put("totalPrice", item.getTotalPrice());
                itemData.put("customizationNotes", item.getCustomizationNotes());
                itemsData.add(itemData);
            }

            response.put("cart", cart);
            response.put("items", itemsData);
            response.put("restaurant", cart.getRestaurant());
            response.put("subtotal", cart.getSubtotal());
            response.put("deliveryFee", cart.getDeliveryFee());
            response.put("total", cart.getTotal());

            return response;
        } catch (Exception e) {
            LOGGER.error("Error getting cart: {}", e.getMessage(), e);
            throw new Exception("Error al obtener el carrito: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseDTO addItemToCart(Long userId, CartItemDTO itemDTO) throws Exception {
        try {
            LOGGER.info("Adding item to cart for user: {}, menuItemId: {}", userId, itemDTO.getMenuItemId());
            ResponseDTO response = new ResponseDTO();

            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));

            MenuItemEntity menuItem = menuItemRepository.findById(itemDTO.getMenuItemId())
                    .orElseThrow(() -> new Exception("Plato no encontrado"));

            if (!menuItem.getIsAvailable()) {
                response.setMessage("El plato no está disponible.");
                response.setNumOfErrors(1);
                return response;
            }

            RestaurantEntity restaurant = menuItem.getRestaurant();

            // Obtener o crear carrito
            CartEntity cart = cartRepository.findByUser(user).orElse(null);

            if (cart != null) {
                // Verificar que el carrito sea del mismo restaurante
                if (!cart.getRestaurant().getId().equals(restaurant.getId())) {
                    // Limpiar carrito anterior y crear uno nuevo para el nuevo restaurante
                    cartItemRepository.deleteByCart(cart);
                    cartRepository.delete(cart);
                    cart = null;
                }
            }

            if (cart == null) {
                cart = new CartEntity();
                cart.setUser(user);
                cart.setRestaurant(restaurant);
                cart.setSubtotal(BigDecimal.ZERO);
                cart.setDeliveryFee(BigDecimal.valueOf(restaurant.getDeliveryFee()));
                cart.setTotal(BigDecimal.ZERO);
                cart = cartRepository.save(cart);
            }

            // Buscar si el item ya existe en el carrito
            Optional<CartItemEntity> existingItemOpt = cartItemRepository.findByCartAndMenuItem_Id(cart, menuItem.getId());

            if (existingItemOpt.isPresent()) {
                // Actualizar cantidad
                CartItemEntity existingItem = existingItemOpt.get();
                int newQuantity = existingItem.getQuantity() + itemDTO.getQuantity();
                existingItem.setQuantity(newQuantity);
                existingItem.setTotalPrice(existingItem.getUnitPrice().multiply(BigDecimal.valueOf(newQuantity)));
                if (itemDTO.getCustomizationNotes() != null) {
                    existingItem.setCustomizationNotes(itemDTO.getCustomizationNotes());
                }
                cartItemRepository.save(existingItem);
            } else {
                // Crear nuevo item
                CartItemEntity cartItem = new CartItemEntity();
                cartItem.setCart(cart);
                cartItem.setMenuItem(menuItem);
                cartItem.setQuantity(itemDTO.getQuantity());
                cartItem.setUnitPrice(menuItem.getPrice());
                cartItem.setTotalPrice(menuItem.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
                cartItem.setCustomizationNotes(itemDTO.getCustomizationNotes());
                cartItemRepository.save(cartItem);
            }

            // Recalcular totales del carrito
            recalculateCartTotals(cart);

            response.setMessage("Item agregado al carrito exitosamente.");
            response.setNumOfErrors(0);
            return response;
        } catch (Exception e) {
            LOGGER.error("Error adding item to cart: {}", e.getMessage(), e);
            throw new Exception("Error al agregar item al carrito: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseDTO updateCartItem(Long userId, Long cartItemId, Integer quantity) throws Exception {
        try {
            LOGGER.info("Updating cart item: {} for user: {} with quantity: {}", cartItemId, userId, quantity);
            ResponseDTO response = new ResponseDTO();

            if (quantity <= 0) {
                response.setMessage("La cantidad debe ser mayor a 0.");
                response.setNumOfErrors(1);
                return response;
            }

            CartItemEntity cartItem = cartItemRepository.findById(cartItemId)
                    .orElseThrow(() -> new Exception("Item del carrito no encontrado"));

            // Verificar que el item pertenezca al usuario
            if (!cartItem.getCart().getUser().getId().equals(userId)) {
                response.setMessage("No tienes permiso para modificar este item.");
                response.setNumOfErrors(1);
                return response;
            }

            cartItem.setQuantity(quantity);
            cartItem.setTotalPrice(cartItem.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));
            cartItemRepository.save(cartItem);

            // Recalcular totales del carrito
            recalculateCartTotals(cartItem.getCart());

            response.setMessage("Item actualizado exitosamente.");
            response.setNumOfErrors(0);
            return response;
        } catch (Exception e) {
            LOGGER.error("Error updating cart item: {}", e.getMessage(), e);
            throw new Exception("Error al actualizar item del carrito: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseDTO removeCartItem(Long userId, Long cartItemId) throws Exception {
        try {
            LOGGER.info("Removing cart item: {} for user: {}", cartItemId, userId);
            ResponseDTO response = new ResponseDTO();

            CartItemEntity cartItem = cartItemRepository.findById(cartItemId)
                    .orElseThrow(() -> new Exception("Item del carrito no encontrado"));

            // Verificar que el item pertenezca al usuario
            if (!cartItem.getCart().getUser().getId().equals(userId)) {
                response.setMessage("No tienes permiso para eliminar este item.");
                response.setNumOfErrors(1);
                return response;
            }

            CartEntity cart = cartItem.getCart();
            cartItemRepository.delete(cartItem);

            // Si el carrito queda vacío, eliminarlo
            List<CartItemEntity> remainingItems = cartItemRepository.findByCart(cart);
            if (remainingItems.isEmpty()) {
                cartRepository.delete(cart);
            } else {
                // Recalcular totales del carrito
                recalculateCartTotals(cart);
            }

            response.setMessage("Item eliminado del carrito exitosamente.");
            response.setNumOfErrors(0);
            return response;
        } catch (Exception e) {
            LOGGER.error("Error removing cart item: {}", e.getMessage(), e);
            throw new Exception("Error al eliminar item del carrito: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseDTO clearCart(Long userId) throws Exception {
        try {
            LOGGER.info("Clearing cart for user: {}", userId);
            ResponseDTO response = new ResponseDTO();

            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));

            Optional<CartEntity> cartOpt = cartRepository.findByUser(user);
            if (cartOpt.isPresent()) {
                CartEntity cart = cartOpt.get();
                cartItemRepository.deleteByCart(cart);
                cartRepository.delete(cart);
            }

            response.setMessage("Carrito vaciado exitosamente.");
            response.setNumOfErrors(0);
            return response;
        } catch (Exception e) {
            LOGGER.error("Error clearing cart: {}", e.getMessage(), e);
            throw new Exception("Error al vaciar el carrito: " + e.getMessage());
        }
    }

    private void recalculateCartTotals(CartEntity cart) {
        List<CartItemEntity> items = cartItemRepository.findByCart(cart);
        BigDecimal subtotal = items.stream()
                .map(CartItemEntity::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setSubtotal(subtotal);
        cart.setTotal(subtotal.add(cart.getDeliveryFee()));
        cartRepository.save(cart);
    }
}

