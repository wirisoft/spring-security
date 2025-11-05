# 📋 Guía de Endpoints Secuenciales - Delivery Trujillo Services

## 🎯 Propósito

Este documento contiene todos los endpoints organizados de forma **lógica y secuencial** para usar la aplicación correctamente. El orden es crítico: **el administrador (OWNER) debe configurar todo primero** antes de que otros usuarios puedan usar el sistema.

---

## 📊 Orden de Ejecución General

```
1. ADMINISTRADOR (OWNER) - Configuración inicial
   ↓
2. RESTAURANTES - Registrar restaurantes y menús
   ↓
3. TRABAJADORES - Registrar repartidores y soporte
   ↓
4. CLIENTES - Registro y uso del sistema
   ↓
5. OPERACIONES - Pedidos, entregas, soporte
```

---

## 🔐 FASE 1: Autenticación (Pública - Sin Token)

### **1.1. Registro de Administrador (OWNER) - PRIMERO**

**⚠️ IMPORTANTE:** Este debe ser el primer usuario registrado para configurar el sistema.

```http
POST /v1/auth/register/owner
Content-Type: application/json

{
  "email": "admin@deliverytrujillo.com",
  "password": "Admin@123",
  "firstName": "Administrador",
  "lastName": "Sistema",
  "phoneNumber": "987654321"
}
```

**Respuesta (201 Created):**
```json
{
  "numOfErrors": 0,
  "message": "User created successfully with role: Dueño!"
}
```

### **1.2. Login del Administrador**

```http
POST /v1/auth/login
Content-Type: application/json

{
  "email": "admin@deliverytrujillo.com",
  "password": "Admin@123"
}
```

**Respuesta (200 OK):**
```json
{
  "jwt": "eyJhbGciOiJSUzI1NiJ9...",
  "userId": 1,
  "email": "admin@deliverytrujillo.com",
  "firstName": "Administrador",
  "lastName": "Sistema",
  "role": "OWNER"
}
```

**💾 Guardar el JWT token para usar en todas las peticiones siguientes:**
```
Authorization: Bearer {jwt-token}
```

---

## 👨‍💼 FASE 2: Configuración Inicial por Administrador (OWNER)

**⚠️ TODAS las peticiones requieren JWT del administrador**

### **2.1. Verificar Dashboard del Administrador**

```http
GET /v1/owner/dashboard
Authorization: Bearer {jwt-token-owner}
```

**Respuesta (200 OK):**
```json
{
  "dailyOrders": 0,
  "dailyRevenue": 0.0,
  "totalUsers": 1,
  "activeRestaurants": 0,
  "activeDeliveryPersons": 0
}
```

### **2.2. Ver Métricas del Administrador**

```http
GET /v1/owner/metrics/orders
Authorization: Bearer {jwt-token-owner}
```

---

## 🏪 FASE 3: Registro de Restaurantes y Trabajadores (OWNER)

### **3.1. Registrar Restaurante (Usuario con Rol RESTAURANT)**

```http
POST /v1/auth/register/restaurant
Content-Type: application/json

{
  "email": "pizzahouse@restaurant.com",
  "password": "Restaurant@123",
  "firstName": "Pizza",
  "lastName": "House",
  "phoneNumber": "987654321"
}
```

**Respuesta (201 Created):**
```json
{
  "numOfErrors": 0,
  "message": "User created successfully with role: Restaurante!"
}
```

### **3.2. Registrar Repartidor (Usuario con Rol DELIVERY)**

```http
POST /v1/auth/register/delivery
Content-Type: application/json

{
  "email": "repartidor1@delivery.com",
  "password": "Delivery@123",
  "firstName": "Carlos",
  "lastName": "Repartidor",
  "phoneNumber": "987654321"
}
```

### **3.3. Registrar Personal de Soporte (Usuario con Rol SUPPORT)**

```http
POST /v1/auth/register/support
Content-Type: application/json

{
  "email": "soporte@delivery.com",
  "password": "Support@123",
  "firstName": "Ana",
  "lastName": "Soporte",
  "phoneNumber": "987654321"
}
```

### **3.4. Verificar Trabajadores Registrados**

```http
GET /v1/users/workers?role=DELIVERY
Authorization: Bearer {jwt-token-owner}
```

```http
GET /v1/users/workers?role=SUPPORT
Authorization: Bearer {jwt-token-owner}
```

```http
GET /v1/users/workers?role=RESTAURANT
Authorization: Bearer {jwt-token-owner}
```

---

## 🍕 FASE 4: Configuración de Restaurantes (RESTAURANT)

**⚠️ Nota:** Los restaurantes y menús se deben crear directamente en la base de datos o mediante endpoints administrativos. Por ahora, asumimos que ya existen restaurantes creados.

### **4.1. Login del Restaurante**

```http
POST /v1/auth/login
Content-Type: application/json

{
  "email": "pizzahouse@restaurant.com",
  "password": "Restaurant@123"
}
```

**💾 Guardar el JWT token del restaurante**

---

## 👥 FASE 5: Registro y Uso de Clientes (CUSTOMER)

### **5.1. Registro de Cliente**

```http
POST /v1/auth/register/customer
Content-Type: application/json

{
  "email": "cliente@example.com",
  "password": "Cliente@123",
  "firstName": "Juan",
  "lastName": "Pérez",
  "phoneNumber": "987654321"
}
```

**Respuesta (201 Created):**
```json
{
  "numOfErrors": 0,
  "message": "User created successfully with role: Cliente!"
}
```

### **5.2. Login del Cliente**

```http
POST /v1/auth/login
Content-Type: application/json

{
  "email": "cliente@example.com",
  "password": "Cliente@123"
}
```

**Respuesta (200 OK):**
```json
{
  "jwt": "eyJhbGciOiJSUzI1NiJ9...",
  "userId": 2,
  "email": "cliente@example.com",
  "firstName": "Juan",
  "lastName": "Pérez",
  "role": "CUSTOMER"
}
```

**💾 Guardar el JWT token del cliente**

---

## ⚙️ FASE 6: Configuración del Perfil del Cliente

**⚠️ Todas las peticiones requieren JWT del cliente**

### **6.1. Ver Perfil del Cliente**

```http
GET /v1/users/profile
Authorization: Bearer {jwt-token-cliente}
```

### **6.2. Subir Foto de Perfil (RF-04)**

```http
POST /v1/users/profile/photo
Authorization: Bearer {jwt-token-cliente}
Content-Type: multipart/form-data

file: [archivo de imagen]
```

**Respuesta (200 OK):**
```json
{
  "numOfErrors": 0,
  "message": "Foto de perfil subida exitosamente."
}
```

### **6.3. Agregar Dirección de Entrega (RF-05)**

```http
POST /v1/addresses
Authorization: Bearer {jwt-token-cliente}
Content-Type: application/json

{
  "name": "Casa",
  "street": "Av. Principal 123",
  "city": "Trujillo",
  "state": "La Libertad",
  "zipCode": "13001",
  "reference": "Frente al parque",
  "latitude": -8.1119,
  "longitude": -79.0288,
  "isDefault": true
}
```

**Respuesta (201 Created):**
```json
{
  "id": 1,
  "name": "Casa",
  "street": "Av. Principal 123",
  "city": "Trujillo",
  "state": "La Libertad",
  "zipCode": "13001",
  "latitude": -8.1119,
  "longitude": -79.0288,
  "isDefault": true
}
```

### **6.4. Listar Direcciones del Cliente**

```http
GET /v1/addresses
Authorization: Bearer {jwt-token-cliente}
```

### **6.5. Agregar Método de Pago (RF-06)**

```http
POST /v1/payment-methods
Authorization: Bearer {jwt-token-cliente}
Content-Type: application/json

{
  "type": "CREDIT_CARD",
  "token": "tok_visa_1234567890",
  "lastFourDigits": "4242",
  "cardholderName": "Juan Pérez",
  "isDefault": true
}
```

**Respuesta (201 Created):**
```json
{
  "id": 1,
  "type": "CREDIT_CARD",
  "lastFourDigits": "4242",
  "cardholderName": "Juan Pérez",
  "isDefault": true,
  "isActive": true
}
```

### **6.6. Listar Métodos de Pago**

```http
GET /v1/payment-methods
Authorization: Bearer {jwt-token-cliente}
```

---

## 🔍 FASE 7: Exploración de Restaurantes (Público)

**⚠️ Estos endpoints NO requieren autenticación**

### **7.1. Dashboard Principal (RF-07)**

```http
GET /v1/restaurants/home
```

**Respuesta (200 OK):**
```json
{
  "featuredRestaurants": [...],
  "activeRestaurants": [...]
}
```

### **7.2. Ver Todos los Restaurantes Activos**

```http
GET /v1/restaurants
```

### **7.3. Ver Restaurantes Destacados**

```http
GET /v1/restaurants/featured
```

### **7.4. Buscar Restaurantes (RF-08)**

```http
GET /v1/restaurants/search?q=pizza
```

### **7.5. Restaurantes Cercanos por Geolocalización (RF-10)**

```http
GET /v1/restaurants/nearby?lat=-8.1119&lng=-79.0288&radius=5.0
```

### **7.6. Ver Detalles de un Restaurante**

```http
GET /v1/restaurants/{restaurantId}
```

### **7.7. Ver Menú de un Restaurante (RF-12)**

```http
GET /v1/restaurants/{restaurantId}/menu
```

### **7.8. Buscar Platos en el Menú (RF-08)**

```http
GET /v1/restaurants/{restaurantId}/menu/search?q=pasta
```

---

## 🛒 FASE 8: Carrito de Compras y Pedidos (CUSTOMER)

**⚠️ Todas las peticiones requieren JWT del cliente**

### **8.1. Agregar Restaurante a Favoritos (RF-11)**

```http
POST /v1/favorites/{restaurantId}
Authorization: Bearer {jwt-token-cliente}
```

**Respuesta (201 Created):**
```json
{
  "id": 1,
  "user": {...},
  "restaurant": {...}
}
```

### **8.2. Ver Mis Favoritos**

```http
GET /v1/favorites
Authorization: Bearer {jwt-token-cliente}
```

### **8.3. Carrito de Compras - Agregar Item (RF-13)**

```http
POST /v1/cart/items
Authorization: Bearer {jwt-token-cliente}
Content-Type: application/json

{
  "menuItemId": 1,
  "quantity": 2,
  "customizationNotes": "Sin cebolla, extra queso"
}
```

**Respuesta (200 OK):**
```json
{
  "numOfErrors": 0,
  "message": "Item agregado al carrito exitosamente."
}
```

### **8.4. Ver Carrito**

```http
GET /v1/cart
Authorization: Bearer {jwt-token-cliente}
```

**Respuesta (200 OK):**
```json
{
  "cart": {
    "id": 1,
    "restaurant": {...},
    "subtotal": 45.00,
    "deliveryFee": 5.00,
    "total": 50.00
  },
  "items": [
    {
      "id": 1,
      "menuItemId": 1,
      "menuItemName": "Pizza Margarita",
      "quantity": 2,
      "unitPrice": 20.00,
      "totalPrice": 40.00,
      "customizationNotes": "Sin cebolla"
    }
  ],
  "subtotal": 45.00,
  "deliveryFee": 5.00,
  "total": 50.00
}
```

### **8.5. Actualizar Cantidad de Item en Carrito**

```http
PUT /v1/cart/items/{cartItemId}?quantity=3
Authorization: Bearer {jwt-token-cliente}
```

### **8.6. Eliminar Item del Carrito**

```http
DELETE /v1/cart/items/{cartItemId}
Authorization: Bearer {jwt-token-cliente}
```

### **8.7. Vaciar Carrito**

```http
DELETE /v1/cart
Authorization: Bearer {jwt-token-cliente}
```

### **8.8. Proceso de Checkout (RF-15)**

```http
POST /v1/orders/checkout
Authorization: Bearer {jwt-token-cliente}
Content-Type: application/json

{
  "addressId": 1,
  "paymentMethodId": 1,
  "notes": "Entregar en la puerta principal",
  "scheduledDeliveryTime": "2025-01-20T18:00:00"  // Opcional para pedidos programados (RF-16)
}
```

**Respuesta (201 Created):**
```json
{
  "orderId": 1,
  "status": "PENDING",
  "subtotal": 45.00,
  "deliveryFee": 5.00,
  "tax": 9.00,
  "total": 59.00,
  "orderDate": "2025-01-20T15:30:00",
  "message": "Pedido creado exitosamente"
}
```

**💾 Guardar el orderId para seguimiento**

---

## 📦 FASE 9: Gestión de Pedidos por Restaurante (RESTAURANT)

**⚠️ Login del restaurante primero**

### **9.1. Login del Restaurante**

```http
POST /v1/auth/login
Content-Type: application/json

{
  "email": "pizzahouse@restaurant.com",
  "password": "Restaurant@123"
}
```

**💾 Guardar el JWT token del restaurante**

### **9.2. Confirmar Pedido**

```http
PUT /v1/orders/{orderId}/status?status=CONFIRMED
Authorization: Bearer {jwt-token-restaurante}
```

**Respuesta (200 OK):**
```json
{
  "numOfErrors": 0,
  "message": "Estado del pedido actualizado exitosamente."
}
```

**💡 Nota:** Esto creará automáticamente una notificación para el cliente.

### **9.3. Marcar Pedido en Preparación**

```http
PUT /v1/orders/{orderId}/status?status=PREPARING
Authorization: Bearer {jwt-token-restaurante}
```

### **9.4. Marcar Pedido como Listo**

```http
PUT /v1/orders/{orderId}/status?status=READY
Authorization: Bearer {jwt-token-restaurante}
```

---

## 🚚 FASE 10: Gestión de Pedidos por Repartidor (DELIVERY)

**⚠️ Login del repartidor primero**

### **10.1. Login del Repartidor**

```http
POST /v1/auth/login
Content-Type: application/json

{
  "email": "repartidor1@delivery.com",
  "password": "Delivery@123"
}
```

**💾 Guardar el JWT token del repartidor**

### **10.2. Marcar Pedido en Camino**

```http
PUT /v1/orders/{orderId}/status?status=ON_THE_WAY
Authorization: Bearer {jwt-token-repartidor}
```

### **10.3. Marcar Pedido como Entregado**

```http
PUT /v1/orders/{orderId}/status?status=DELIVERED
Authorization: Bearer {jwt-token-repartidor}
```

**💡 Nota:** Esto creará automáticamente una notificación para el cliente.

---

## 👤 FASE 11: Seguimiento y Notificaciones (CUSTOMER)

**⚠️ Todas las peticiones requieren JWT del cliente**

### **11.1. Ver Estado del Pedido en Tiempo Real (RF-18)**

```http
GET /v1/orders/{orderId}/status
Authorization: Bearer {jwt-token-cliente}
```

**Respuesta (200 OK):**
```json
{
  "orderId": 1,
  "status": "ON_THE_WAY",
  "statusDescription": "En camino",
  "restaurantName": "Pizza House",
  "subtotal": 45.00,
  "deliveryFee": 5.00,
  "tax": 9.00,
  "total": 59.00,
  "orderDate": "2025-01-20T15:30:00",
  "items": [...],
  "deliveryAddress": {...}
}
```

### **11.2. Ver Historial de Pedidos (RF-17)**

```http
GET /v1/orders/history
Authorization: Bearer {jwt-token-cliente}
```

**Respuesta (200 OK):**
```json
[
  {
    "orderId": 1,
    "status": "DELIVERED",
    "statusDescription": "Entregado",
    "restaurantName": "Pizza House",
    "total": 59.00,
    "orderDate": "2025-01-20T15:30:00",
    "items": [...]
  }
]
```

### **11.3. Ver Notificaciones (RF-19)**

```http
GET /v1/notifications
Authorization: Bearer {jwt-token-cliente}
```

**Respuesta (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Actualización de Pedido #1",
    "message": "Tu pedido ha sido confirmado y está siendo preparado.",
    "type": "ORDER_CONFIRMED",
    "isRead": false,
    "createdAt": "2025-01-20T15:35:00",
    "orderId": 1
  }
]
```

### **11.4. Contador de Notificaciones No Leídas**

```http
GET /v1/notifications/unread-count
Authorization: Bearer {jwt-token-cliente}
```

**Respuesta (200 OK):**
```json
{
  "unreadCount": 3
}
```

### **11.5. Marcar Notificación como Leída**

```http
PUT /v1/notifications/{notificationId}/read
Authorization: Bearer {jwt-token-cliente}
```

### **11.6. Marcar Todas las Notificaciones como Leídas**

```http
PUT /v1/notifications/read-all
Authorization: Bearer {jwt-token-cliente}
```

---

## ⭐ FASE 12: Calificaciones y Reseñas (CUSTOMER - Después de Entrega)

**⚠️ Solo se puede calificar pedidos entregados (status: DELIVERED)**

### **12.1. Crear Reseña (RF-20)**

```http
POST /v1/reviews
Authorization: Bearer {jwt-token-cliente}
Content-Type: application/json

{
  "orderId": 1,
  "restaurantRating": 5,
  "deliveryRating": 4,
  "comment": "Excelente servicio, la comida llegó caliente y a tiempo.",
  "photoUrl": "/uploads/reviews/review_1.jpg"  // Opcional
}
```

**Respuesta (201 Created):**
```json
{
  "numOfErrors": 0,
  "message": "Reseña creada exitosamente."
}
```

**💡 Nota:** Esto actualizará automáticamente la calificación promedio del restaurante.

### **12.2. Ver Reseñas de un Restaurante**

```http
GET /v1/reviews/restaurant/{restaurantId}
```

**Respuesta (200 OK):**
```json
[
  {
    "id": 1,
    "restaurantRating": 5,
    "deliveryRating": 4,
    "comment": "Excelente servicio...",
    "userName": "Juan Pérez",
    "createdAt": "2025-01-20T16:00:00"
  }
]
```

### **12.3. Ver Mi Reseña de un Pedido**

```http
GET /v1/reviews/order/{orderId}
Authorization: Bearer {jwt-token-cliente}
```

---

## 💬 FASE 13: Chat de Soporte (CUSTOMER y SUPPORT)

### **13.1. Cliente - Crear Chat de Soporte (RF-21)**

```http
POST /v1/chat
Authorization: Bearer {jwt-token-cliente}
Content-Type: application/json

{
  "orderId": 1,  // Opcional
  "subject": "Problema con mi pedido"
}
```

**Respuesta (201 Created):**
```json
{
  "chatId": 1,
  "status": "OPEN",
  "message": "Chat creado exitosamente"
}
```

### **13.2. Cliente - Ver Mis Chats**

```http
GET /v1/chat
Authorization: Bearer {jwt-token-cliente}
```

### **13.3. Cliente - Ver Chat con Mensajes**

```http
GET /v1/chat/{chatId}
Authorization: Bearer {jwt-token-cliente}
```

**Respuesta (200 OK):**
```json
{
  "id": 1,
  "status": "IN_PROGRESS",
  "subject": "Problema con mi pedido",
  "messages": [
    {
      "id": 1,
      "content": "Mi pedido llegó tarde",
      "isFromSupport": false,
      "senderName": "Juan Pérez",
      "createdAt": "2025-01-20T16:00:00"
    },
    {
      "id": 2,
      "content": "Hola, estamos revisando tu pedido...",
      "isFromSupport": true,
      "senderName": "Ana Soporte",
      "createdAt": "2025-01-20T16:05:00"
    }
  ],
  "supportUser": {
    "id": 3,
    "name": "Ana Soporte"
  }
}
```

### **13.4. Cliente - Enviar Mensaje**

```http
POST /v1/chat/{chatId}/messages
Authorization: Bearer {jwt-token-cliente}
Content-Type: application/json

{
  "content": "Mi pedido llegó tarde y frío",
  "attachmentUrl": "/uploads/attachments/photo.jpg"  // Opcional
}
```

### **13.5. Cliente - Cerrar Chat**

```http
PUT /v1/chat/{chatId}/close
Authorization: Bearer {jwt-token-cliente}
```

---

### **13.6. Soporte - Login del Personal de Soporte**

```http
POST /v1/auth/login
Content-Type: application/json

{
  "email": "soporte@delivery.com",
  "password": "Support@123"
}
```

**💾 Guardar el JWT token del soporte**

### **13.7. Soporte - Asignar Usuario a Chat**

```http
PUT /v1/chat/{chatId}/assign?supportUserId=3
Authorization: Bearer {jwt-token-soporte}
```

**Respuesta (200 OK):**
```json
{
  "numOfErrors": 0,
  "message": "Usuario de soporte asignado exitosamente."
}
```

### **13.8. Soporte - Enviar Mensaje**

```http
POST /v1/chat/{chatId}/messages
Authorization: Bearer {jwt-token-soporte}
Content-Type: application/json

{
  "content": "Hola, estamos revisando tu pedido. Te contactaremos pronto.",
  "attachmentUrl": null
}
```

---

## 🔄 FASE 14: Recuperación de Contraseña (RF-03)

**⚠️ Este endpoint es público (no requiere autenticación)**

### **14.1. Solicitar Recuperación de Contraseña**

```http
POST /v1/auth/forgot-password
Content-Type: application/json

{
  "email": "cliente@example.com"
}
```

**Respuesta (200 OK):**
```json
{
  "numOfErrors": 0,
  "message": "Si el email existe, se enviará un enlace de recuperación."
}
```

**💡 Nota:** Se enviará un email con un token de recuperación (si está configurado el email).

### **14.2. Restablecer Contraseña con Token**

```http
POST /v1/auth/reset-password
Content-Type: application/json

{
  "token": "uuid-token-recibido-por-email",
  "newPassword": "NuevaPassword@123"
}
```

**Respuesta (200 OK):**
```json
{
  "numOfErrors": 0,
  "message": "Contraseña restablecida exitosamente."
}
```

---

## 📊 Resumen de Secuencias por Rol

### **🔴 OWNER (Administrador) - Orden de Ejecución:**

1. `POST /v1/auth/register/owner` - Registrarse como administrador
2. `POST /v1/auth/login` - Login como administrador
3. `GET /v1/owner/dashboard` - Verificar dashboard
4. `POST /v1/auth/register/restaurant` - Registrar restaurantes
5. `POST /v1/auth/register/delivery` - Registrar repartidores
6. `POST /v1/auth/register/support` - Registrar personal de soporte
7. `GET /v1/users/workers` - Verificar trabajadores registrados

### **🟠 RESTAURANT - Orden de Ejecución:**

1. `POST /v1/auth/login` - Login como restaurante
2. `PUT /v1/orders/{orderId}/status?status=CONFIRMED` - Confirmar pedidos
3. `PUT /v1/orders/{orderId}/status?status=PREPARING` - Marcar en preparación
4. `PUT /v1/orders/{orderId}/status?status=READY` - Marcar como listo

### **🟡 DELIVERY (Repartidor) - Orden de Ejecución:**

1. `POST /v1/auth/login` - Login como repartidor
2. `PUT /v1/orders/{orderId}/status?status=ON_THE_WAY` - Marcar en camino
3. `PUT /v1/orders/{orderId}/status?status=DELIVERED` - Marcar como entregado

### **🟢 CUSTOMER (Cliente) - Orden de Ejecución:**

1. `POST /v1/auth/register/customer` - Registrarse
2. `POST /v1/auth/login` - Login
3. `POST /v1/addresses` - Agregar dirección
4. `POST /v1/payment-methods` - Agregar método de pago
5. `GET /v1/restaurants/home` - Explorar restaurantes (público)
6. `GET /v1/restaurants/{id}/menu` - Ver menú (público)
7. `POST /v1/cart/items` - Agregar al carrito
8. `POST /v1/orders/checkout` - Realizar pedido
9. `GET /v1/orders/{id}/status` - Seguimiento en tiempo real
10. `GET /v1/notifications` - Ver notificaciones
11. `POST /v1/reviews` - Calificar después de entrega
12. `POST /v1/chat` - Crear chat si hay problemas

### **🔵 SUPPORT (Soporte) - Orden de Ejecución:**

1. `POST /v1/auth/login` - Login como soporte
2. `PUT /v1/chat/{chatId}/assign` - Asignar chats
3. `POST /v1/chat/{chatId}/messages` - Responder mensajes

---

## 🎯 Variables de Entorno para Postman

Configura estas variables en Postman:

```
base_url = http://localhost:8080
jwt_token_owner = (token del administrador)
jwt_token_restaurant = (token del restaurante)
jwt_token_delivery = (token del repartidor)
jwt_token_support = (token del soporte)
jwt_token_customer = (token del cliente)
```

---

## 📝 Notas Importantes

1. **Orden Crítico:** El administrador (OWNER) debe configurar todo primero antes de que otros usuarios usen el sistema.

2. **Tokens JWT:** Cada usuario debe guardar su token JWT después del login y usarlo en todas las peticiones protegidas.

3. **Estados de Pedido:** Los estados deben seguir esta secuencia:
   - `PENDING` → `CONFIRMED` → `PREPARING` → `READY` → `ON_THE_WAY` → `DELIVERED`

4. **Notificaciones Automáticas:** Se crean automáticamente cuando cambia el estado de un pedido.

5. **Reseñas:** Solo se pueden crear para pedidos con estado `DELIVERED`.

6. **Carrito:** Solo puede contener items de un restaurante a la vez.

7. **Pedidos Programados:** La fecha debe ser en el futuro.

---

**Última actualización:** 2025-01-20  
**Versión:** 1.0  
**Para uso en Postman**

