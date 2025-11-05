package com.delivery_trujillo.app_trujillo_services.services.impl;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.*;
import com.delivery_trujillo.app_trujillo_services.persistence.repositories.*;
import com.delivery_trujillo.app_trujillo_services.services.IOrderService;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.CheckoutDTO;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.ResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements IOrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);
    private static final BigDecimal TAX_RATE = new BigDecimal("0.18"); // 18% IGV

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired(required = false)
    private com.delivery_trujillo.app_trujillo_services.services.INotificationService notificationService;

    @Override
    @Transactional
    public HashMap<String, Object> checkout(Long userId, CheckoutDTO checkoutDTO) throws Exception {
        try {
            LOGGER.info("Processing checkout for user: {}", userId);
            
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));

            // Obtener carrito
            Optional<CartEntity> cartOpt = cartRepository.findByUser_Id(userId);
            if (cartOpt.isEmpty()) {
                throw new Exception("El carrito está vacío");
            }

            CartEntity cart = cartOpt.get();
            List<CartItemEntity> cartItems = cartItemRepository.findByCart(cart);

            if (cartItems.isEmpty()) {
                throw new Exception("El carrito está vacío");
            }

            // Validar dirección
            AddressEntity address = addressRepository.findByIdAndUser(checkoutDTO.getAddressId(), user)
                    .orElseThrow(() -> new Exception("Dirección no encontrada o no pertenece al usuario"));

            // Validar método de pago
            PaymentMethodEntity paymentMethod = paymentMethodRepository.findByIdAndUser(checkoutDTO.getPaymentMethodId(), user)
                    .orElseThrow(() -> new Exception("Método de pago no encontrado o no pertenece al usuario"));

            if (!paymentMethod.getIsActive()) {
                throw new Exception("El método de pago no está activo");
            }

            // Crear pedido
            OrderEntity order = new OrderEntity();
            order.setUser(user);
            order.setRestaurant(cart.getRestaurant());
            order.setDeliveryAddress(address);
            order.setPaymentMethod(paymentMethod);
            order.setStatus(OrderEntity.OrderStatus.PENDING);
            order.setSubtotal(cart.getSubtotal());
            order.setDeliveryFee(cart.getDeliveryFee());
            
            // Calcular impuestos
            BigDecimal tax = cart.getSubtotal().multiply(TAX_RATE);
            order.setTax(tax);
            
            // Calcular total
            BigDecimal total = cart.getSubtotal().add(cart.getDeliveryFee()).add(tax);
            order.setTotal(total);
            
            order.setNotes(checkoutDTO.getNotes());
            order.setOrderDate(LocalDateTime.now());
            
            // Manejar pedidos programados
            if (checkoutDTO.getScheduledDeliveryTime() != null) {
                if (checkoutDTO.getScheduledDeliveryTime().isBefore(LocalDateTime.now())) {
                    throw new Exception("La fecha de entrega programada debe ser en el futuro");
                }
                order.setScheduledDeliveryTime(checkoutDTO.getScheduledDeliveryTime());
                order.setIsScheduled(true);
            } else {
                order.setIsScheduled(false);
            }

            order = orderRepository.save(order);

            // Crear items del pedido
            List<OrderItemEntity> orderItems = new ArrayList<>();
            for (CartItemEntity cartItem : cartItems) {
                OrderItemEntity orderItem = new OrderItemEntity();
                orderItem.setOrder(order);
                orderItem.setMenuItem(cartItem.getMenuItem());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setUnitPrice(cartItem.getUnitPrice());
                orderItem.setTotalPrice(cartItem.getTotalPrice());
                orderItem.setCustomizationNotes(cartItem.getCustomizationNotes());
                orderItems.add(orderItem);
            }

            orderItemRepository.saveAll(orderItems);

            // Limpiar carrito
            cartItemRepository.deleteByCart(cart);
            cartRepository.delete(cart);

            // Construir respuesta
            HashMap<String, Object> response = new HashMap<>();
            response.put("orderId", order.getId());
            response.put("status", order.getStatus().name());
            response.put("subtotal", order.getSubtotal());
            response.put("deliveryFee", order.getDeliveryFee());
            response.put("tax", order.getTax());
            response.put("total", order.getTotal());
            response.put("orderDate", order.getOrderDate());
            response.put("message", "Pedido creado exitosamente");

            LOGGER.info("Order created successfully: {}", order.getId());
            return response;
        } catch (Exception e) {
            LOGGER.error("Error during checkout: {}", e.getMessage(), e);
            throw new Exception("Error en el proceso de checkout: " + e.getMessage());
        }
    }

    @Override
    public List<HashMap<String, Object>> getOrderHistory(Long userId) throws Exception {
        try {
            LOGGER.info("Getting order history for user: {}", userId);
            
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));

            List<OrderEntity> orders = orderRepository.findByUser_IdOrderByOrderDateDesc(userId);
            
            List<HashMap<String, Object>> orderHistory = new ArrayList<>();
            
            for (OrderEntity order : orders) {
                HashMap<String, Object> orderData = buildOrderResponse(order);
                orderHistory.add(orderData);
            }

            return orderHistory;
        } catch (Exception e) {
            LOGGER.error("Error getting order history: {}", e.getMessage(), e);
            throw new Exception("Error al obtener historial de pedidos: " + e.getMessage());
        }
    }

    @Override
    public HashMap<String, Object> getOrderStatus(Long userId, Long orderId) throws Exception {
        try {
            LOGGER.info("Getting order status for order: {} and user: {}", orderId, userId);
            
            Optional<OrderEntity> orderOpt = orderRepository.findByIdAndUser_Id(orderId, userId);
            if (orderOpt.isEmpty()) {
                throw new Exception("Pedido no encontrado o no pertenece al usuario");
            }

            OrderEntity order = orderOpt.get();
            return buildOrderResponse(order);
        } catch (Exception e) {
            LOGGER.error("Error getting order status: {}", e.getMessage(), e);
            throw new Exception("Error al obtener estado del pedido: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseDTO updateOrderStatus(Long orderId, OrderEntity.OrderStatus newStatus) throws Exception {
        try {
            LOGGER.info("Updating order status for order: {} to status: {}", orderId, newStatus);
            ResponseDTO response = new ResponseDTO();

            OrderEntity order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new Exception("Pedido no encontrado"));

            // Validar transición de estado
            if (!isValidStatusTransition(order.getStatus(), newStatus)) {
                response.setMessage("Transición de estado inválida: " + order.getStatus() + " -> " + newStatus);
                response.setNumOfErrors(1);
                return response;
            }

            order.setStatus(newStatus);
            orderRepository.save(order);

            // Crear notificación automática para el usuario (RF-19)
            if (notificationService != null) {
                try {
                    String title = "Actualización de Pedido #" + order.getId();
                    String message = getStatusNotificationMessage(newStatus);
                    com.delivery_trujillo.app_trujillo_services.persistence.entities.NotificationEntity.NotificationType type = 
                        getNotificationTypeForStatus(newStatus);
                    notificationService.createNotification(
                        order.getUser().getId(), 
                        title, 
                        message, 
                        type, 
                        order.getId()
                    );
                } catch (Exception e) {
                    LOGGER.warn("Error creating notification: {}", e.getMessage());
                    // No fallar si la notificación no se puede crear
                }
            }

            response.setMessage("Estado del pedido actualizado exitosamente.");
            response.setNumOfErrors(0);
            return response;
        } catch (Exception e) {
            LOGGER.error("Error updating order status: {}", e.getMessage(), e);
            throw new Exception("Error al actualizar estado del pedido: " + e.getMessage());
        }
    }

    private HashMap<String, Object> buildOrderResponse(OrderEntity order) {
        HashMap<String, Object> orderData = new HashMap<>();
        
        orderData.put("orderId", order.getId());
        orderData.put("status", order.getStatus().name());
        orderData.put("statusDescription", getStatusDescription(order.getStatus()));
        orderData.put("restaurantId", order.getRestaurant().getId());
        orderData.put("restaurantName", order.getRestaurant().getName());
        orderData.put("subtotal", order.getSubtotal());
        orderData.put("deliveryFee", order.getDeliveryFee());
        orderData.put("tax", order.getTax());
        orderData.put("total", order.getTotal());
        orderData.put("orderDate", order.getOrderDate());
        orderData.put("scheduledDeliveryTime", order.getScheduledDeliveryTime());
        orderData.put("isScheduled", order.getIsScheduled());
        orderData.put("notes", order.getNotes());
        
        // Obtener items del pedido
        List<OrderItemEntity> orderItems = orderItemRepository.findByOrder(order);
        List<HashMap<String, Object>> itemsData = new ArrayList<>();
        for (OrderItemEntity item : orderItems) {
            HashMap<String, Object> itemData = new HashMap<>();
            itemData.put("menuItemId", item.getMenuItem().getId());
            itemData.put("menuItemName", item.getMenuItem().getName());
            itemData.put("quantity", item.getQuantity());
            itemData.put("unitPrice", item.getUnitPrice());
            itemData.put("totalPrice", item.getTotalPrice());
            itemData.put("customizationNotes", item.getCustomizationNotes());
            itemsData.add(itemData);
        }
        orderData.put("items", itemsData);
        
        // Información de dirección
        if (order.getDeliveryAddress() != null) {
            HashMap<String, Object> addressData = new HashMap<>();
            addressData.put("addressId", order.getDeliveryAddress().getId());
            addressData.put("name", order.getDeliveryAddress().getName());
            addressData.put("street", order.getDeliveryAddress().getStreet());
            addressData.put("city", order.getDeliveryAddress().getCity());
            addressData.put("state", order.getDeliveryAddress().getState());
            addressData.put("zipCode", order.getDeliveryAddress().getZipCode());
            orderData.put("deliveryAddress", addressData);
        }
        
        return orderData;
    }

    private String getStatusDescription(OrderEntity.OrderStatus status) {
        return switch (status) {
            case PENDING -> "Pendiente";
            case CONFIRMED -> "Confirmado";
            case PREPARING -> "En preparación";
            case READY -> "Listo";
            case ON_THE_WAY -> "En camino";
            case DELIVERED -> "Entregado";
            case CANCELLED -> "Cancelado";
        };
    }

    private boolean isValidStatusTransition(OrderEntity.OrderStatus current, OrderEntity.OrderStatus newStatus) {
        // Definir transiciones válidas
        return switch (current) {
            case PENDING -> newStatus == OrderEntity.OrderStatus.CONFIRMED || 
                           newStatus == OrderEntity.OrderStatus.CANCELLED;
            case CONFIRMED -> newStatus == OrderEntity.OrderStatus.PREPARING || 
                             newStatus == OrderEntity.OrderStatus.CANCELLED;
            case PREPARING -> newStatus == OrderEntity.OrderStatus.READY || 
                             newStatus == OrderEntity.OrderStatus.CANCELLED;
            case READY -> newStatus == OrderEntity.OrderStatus.ON_THE_WAY || 
                         newStatus == OrderEntity.OrderStatus.CANCELLED;
            case ON_THE_WAY -> newStatus == OrderEntity.OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false; // Estados finales
        };
    }

    private String getStatusNotificationMessage(OrderEntity.OrderStatus status) {
        return switch (status) {
            case CONFIRMED -> "Tu pedido ha sido confirmado y está siendo preparado.";
            case PREPARING -> "Tu pedido está en preparación.";
            case READY -> "Tu pedido está listo y en camino a tu dirección.";
            case ON_THE_WAY -> "Tu pedido está en camino. ¡Prepárate para recibirlo!";
            case DELIVERED -> "Tu pedido ha sido entregado. ¡Gracias por tu compra!";
            case CANCELLED -> "Tu pedido ha sido cancelado.";
            default -> "Tu pedido ha sido actualizado.";
        };
    }

    private com.delivery_trujillo.app_trujillo_services.persistence.entities.NotificationEntity.NotificationType 
        getNotificationTypeForStatus(OrderEntity.OrderStatus status) {
        return switch (status) {
            case CONFIRMED -> com.delivery_trujillo.app_trujillo_services.persistence.entities.NotificationEntity.NotificationType.ORDER_CONFIRMED;
            case PREPARING -> com.delivery_trujillo.app_trujillo_services.persistence.entities.NotificationEntity.NotificationType.ORDER_STATUS_CHANGE;
            case READY -> com.delivery_trujillo.app_trujillo_services.persistence.entities.NotificationEntity.NotificationType.ORDER_READY;
            case ON_THE_WAY -> com.delivery_trujillo.app_trujillo_services.persistence.entities.NotificationEntity.NotificationType.ORDER_ON_THE_WAY;
            case DELIVERED -> com.delivery_trujillo.app_trujillo_services.persistence.entities.NotificationEntity.NotificationType.ORDER_DELIVERED;
            default -> com.delivery_trujillo.app_trujillo_services.persistence.entities.NotificationEntity.NotificationType.ORDER_STATUS_CHANGE;
        };
    }
}

