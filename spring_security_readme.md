# 🔐 Spring Security con JWT - Delivery Trujillo API

## 📋 Tabla de Contenidos
- [¿Qué es Spring Security?](#qué-es-spring-security)
- [¿Qué es JWT?](#qué-es-jwt)
- [¿Qué es OpenSSL?](#qué-es-openssl)
- [Arquitectura de Seguridad](#arquitectura-de-seguridad)
- [Configuración del Proyecto](#configuración-del-proyecto)
- [Documentación de Endpoints](#documentación-de-endpoints)
- [Uso con Postman](#uso-con-postman)
- [Uso con PowerShell/Curl](#uso-con-powershellcurl)

---

## 🛡️ ¿Qué es Spring Security?

**Spring Security** es un framework de autenticación y control de acceso altamente personalizable para aplicaciones Java. Proporciona:

### Características principales:
- **Autenticación**: Verifica la identidad de los usuarios
- **Autorización**: Controla qué recursos puede acceder un usuario autenticado
- **Protección contra ataques**: CSRF, Session Fixation, Clickjacking, etc.
- **Integración**: OAuth2, LDAP, JWT, Basic Auth, etc.

### En este proyecto:
```java
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
    // Configuración de seguridad personalizada
}
```

---

## 🎫 ¿Qué es JWT (JSON Web Token)?

**JWT** es un estándar abierto (RFC 7519) para transmitir información de forma segura entre partes como un objeto JSON.

### Estructura de un JWT:
```
eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjM5...
│                      │                                   │
└─ Header             └─ Payload                          └─ Signature
```

#### 1. **Header** (Encabezado)
```json
{
  "alg": "RS256",
  "typ": "JWT"
}
```
Define el algoritmo de firma (RS256 = RSA con SHA-256)

#### 2. **Payload** (Datos)
```json
{
  "sub": "1",           // Subject (User ID)
  "iat": 1639584000,    // Issued At (fecha de emisión)
  "exp": 1639598400     // Expiration (fecha de expiración)
}
```

#### 3. **Signature** (Firma)
```
RSASHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  privateKey
)
```

### Ventajas de JWT:
- ✅ **Stateless**: No requiere almacenar sesiones en el servidor
- ✅ **Escalable**: Ideal para arquitecturas distribuidas
- ✅ **Autocontenido**: Toda la información está en el token
- ✅ **Seguro**: Firmado criptográficamente

### En este proyecto:
- **Algoritmo**: RS256 (RSA + SHA-256)
- **Expiración**: 4 horas
- **Emisor**: `JWTUtilityServiceImpl`

---

## 🔑 ¿Qué es OpenSSL?

**OpenSSL** es una biblioteca de software de código abierto que implementa protocolos criptográficos SSL y TLS.

### Uso en este proyecto:
OpenSSL se utilizó para generar el par de llaves RSA (pública y privada):

```bash
# Generar llave privada RSA de 2048 bits
openssl genrsa -out private_key.pem 2048

# Extraer llave pública de la privada
openssl rsa -in private_key.pem -pubout -out public_key.pem
```

### ¿Por qué RSA?
- **Asimétrico**: Usa dos llaves (pública y privada)
- **Firma**: La llave privada firma el JWT
- **Verificación**: La llave pública verifica la autenticidad
- **Seguridad**: Imposible falsificar sin la llave privada

```
Usuario → Login → [Servidor firma JWT con private_key.pem]
                            ↓
                    JWT firmado enviado al usuario
                            ↓
Usuario → Petición + JWT → [Servidor verifica con public_key.pem]
```

---

## 🏗️ Arquitectura de Seguridad

### Flujo de Autenticación

```
┌─────────────┐         ┌──────────────────┐         ┌──────────────┐
│   Cliente   │         │  AuthController  │         │   Database   │
└──────┬──────┘         └────────┬─────────┘         └──────┬───────┘
       │                         │                           │
       │ POST /v1/auth/register  │                           │
       │─────────────────────────>│                           │
       │                         │  Valida y encripta password│
       │                         │───────────────────────────>│
       │                         │                           │
       │                         │  Usuario guardado         │
       │    201 Created          │<───────────────────────────│
       │<─────────────────────────│                           │
       │                         │                           │
       │ POST /v1/auth/login     │                           │
       │─────────────────────────>│                           │
       │                         │  Busca usuario y verifica │
       │                         │───────────────────────────>│
       │                         │                           │
       │                         │  Usuario encontrado       │
       │                         │<───────────────────────────│
       │                         │                           │
       │                         │  Genera JWT (firma con    │
       │                         │  private_key.pem)         │
       │  200 OK + JWT Token     │                           │
       │<─────────────────────────│                           │
       │                         │                           │
```

### Flujo de Autorización

```
┌─────────────┐         ┌──────────────────────┐      ┌──────────────┐
│   Cliente   │         │ JWTAuthorizationFilter│      │  Controller  │
└──────┬──────┘         └──────────┬───────────┘      └──────┬───────┘
       │                           │                          │
       │ GET /v1/users             │                          │
       │ Authorization: Bearer JWT │                          │
       │───────────────────────────>│                          │
       │                           │                          │
       │                           │ Extrae y verifica JWT    │
       │                           │ (usa public_key.pem)     │
       │                           │                          │
       │                           │ ✓ Token válido           │
       │                           │                          │
       │                           │ Establece autenticación  │
       │                           │ en SecurityContext       │
       │                           │                          │
       │                           │ Permite el request       │
       │                           │──────────────────────────>│
       │                           │                          │
       │                           │        Respuesta         │
       │                           │<──────────────────────────│
       │      200 OK + Data        │                          │
       │<───────────────────────────│                          │
       │                           │                          │
```

---

## ⚙️ Configuración del Proyecto

### 1. SecurityConfiguration.java

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfiguration {

    @Autowired
    private IJWTUtilityService jwtUtilityService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
            // Deshabilita CSRF (no necesario para APIs REST stateless)
            .csrf(csrf -> csrf.disable())
            
            // Configura las reglas de autorización
            .authorizeHttpRequests(authRequest ->
                authRequest
                    // Permite acceso público a endpoints de autenticación
                    .requestMatchers("/v1/auth/**").permitAll()
                    
                    // Todos los demás endpoints requieren autenticación
                    .anyRequest().authenticated()
            )
            
            // Configura la aplicación como STATELESS (sin sesiones)
            .sessionManagement(sessionManager ->
                sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Agrega el filtro JWT antes del filtro de autenticación por defecto
            .addFilterBefore(
                jwtAuthorizationFilter(), 
                UsernamePasswordAuthenticationFilter.class
            )
            
            // Maneja errores de autenticación devolviendo 401
            .exceptionHandling(exceptionHandling ->
                exceptionHandling.authenticationEntryPoint(
                    (request, response, authException) -> {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                    }
                )
            )
            .build();
    }

    @Bean
    public JWTAuthorizationFilter jwtAuthorizationFilter() {
        return new JWTAuthorizationFilter(jwtUtilityService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt es un algoritmo de hash adaptativo para passwords
        return new BCryptPasswordEncoder();
    }
}
```

**Puntos clave:**
- `@EnableWebSecurity`: Habilita Spring Security
- `@EnableMethodSecurity`: Permite usar anotaciones como `@PreAuthorize`
- `STATELESS`: No guarda sesiones en el servidor
- `permitAll()`: Permite acceso sin autenticación
- `authenticated()`: Requiere autenticación JWT

---

### 2. JWTAuthorizationFilter.java

```java
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final IJWTUtilityService jwtUtilityService;

    public JWTAuthorizationFilter(IJWTUtilityService jwtUtilityService) {
        this.jwtUtilityService = jwtUtilityService;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, 
        HttpServletResponse response, 
        FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Extrae el header Authorization
        String header = request.getHeader("Authorization");

        // 2. Verifica que el header exista y tenga el formato "Bearer {token}"
        if (header == null || !header.startsWith("Bearer ")) {
            // Si no hay token, continúa sin autenticación
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extrae el token (elimina el prefijo "Bearer ")
        String token = header.substring(7);

        try {
            // 4. Verifica y parsea el JWT
            JWTClaimsSet claims = jwtUtilityService.parseJWT(token);
            
            // 5. Crea un objeto de autenticación con el subject (userId)
            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                    claims.getSubject(),  // Usuario (ID)
                    null,                 // Credentials (no necesarias)
                    Collections.emptyList() // Authorities (roles/permisos)
                );
            
            // 6. Establece la autenticación en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        } catch (Exception e) {
            // 7. Si el token es inválido o expiró, retorna 401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token");
            return;
        }

        // 8. Continúa con el siguiente filtro en la cadena
        filterChain.doFilter(request, response);
    }
}
```

**Flujo del filtro:**
1. Extrae el header `Authorization`
2. Valida formato `Bearer {token}`
3. Extrae el token JWT
4. Verifica firma y expiración usando `public_key.pem`
5. Crea objeto de autenticación
6. Establece autenticación en `SecurityContext`
7. Maneja errores (token inválido/expirado)
8. Continúa con la petición

**¿Por qué `OncePerRequestFilter`?**
- Garantiza que el filtro se ejecute **una sola vez** por petición
- Evita procesamiento duplicado en casos de forwards/includes

---

### 3. JWTUtilityServiceImpl.java

```java
@Service
public class JWTUtilityServiceImpl implements IJWTUtilityService {

    @Value("classpath:jwtKeys/private_key.pem")
    private Resource privateKeyResource;

    @Value("classpath:jwtKeys/public_key.pem")
    private Resource publicKeyResource;

    @Override
    public String generateJWT(Long userId) {
        // 1. Carga la llave privada desde el archivo PEM
        PrivateKey privateKey = loadPrivateKey(privateKeyResource);
        
        // 2. Crea el firmador con la llave privada
        JWSSigner signer = new RSASSASigner(privateKey);

        Date now = new Date();
        
        // 3. Construye los claims (datos) del JWT
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .subject(userId.toString())                      // Usuario ID
            .issueTime(now)                                  // Fecha de emisión
            .expirationTime(new Date(now.getTime() + 14400000)) // +4 horas
            .build();

        // 4. Crea el JWT firmado
        SignedJWT signedJWT = new SignedJWT(
            new JWSHeader(JWSAlgorithm.RS256),  // Algoritmo RS256
            claimsSet
        );
        
        // 5. Firma el JWT
        signedJWT.sign(signer);

        // 6. Retorna el token serializado
        return signedJWT.serialize();
    }

    @Override
    public JWTClaimsSet parseJWT(String jwt) {
        // 1. Carga la llave pública
        PublicKey publicKey = loadPublicKey(publicKeyResource);

        // 2. Parsea el JWT
        SignedJWT signedJWT = SignedJWT.parse(jwt);

        // 3. Crea el verificador con la llave pública
        JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) publicKey);
        
        // 4. Verifica la firma
        if (!signedJWT.verify(verifier)) {
            throw new JOSEException("Invalid signature");
        }

        // 5. Extrae los claims
        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
        
        // 6. Verifica la expiración
        if (claimsSet.getExpirationTime().before(new Date())) {
            throw new JOSEException("Expired token");
        }

        return claimsSet;
    }
}
```

**Puntos clave:**
- **Generación**: Usa `private_key.pem` para firmar
- **Verificación**: Usa `public_key.pem` para validar
- **Expiración**: 4 horas (14400000 ms)
- **Algoritmo**: RS256 (RSA-SHA256)

---

## 📡 Documentación de Endpoints

### Base URL
```
http://localhost:8080
```

### Autenticación Pública

#### 1. Registrar Usuario
```http
POST /v1/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "Password@123",
  "firstName": "Juan",
  "lastName": "Pérez",
  "phoneNumber": "123456789"
}
```

**Respuesta exitosa (200):**
```json
{
  "numOfErrors": 0,
  "message": "User created successfully!"
}
```

**Validaciones de contraseña:**
- 8-16 caracteres
- Al menos 1 mayúscula
- Al menos 1 minúscula
- Al menos 1 número
- Al menos 1 carácter especial (!@#$%^&*...)

---

#### 2. Login
```http
POST /v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "Password@123"
}
```

**Respuesta exitosa (200):**
```json
{
  "jwt": "eyJhbGciOiJSUzI1NiJ9...",
  "userId": 1,
  "email": "user@example.com",
  "firstName": "Juan",
  "lastName": "Pérez"
}
```

---

### Endpoints Protegidos (Requieren JWT)

> 🔒 Todos estos endpoints requieren el header:
> ```
> Authorization: Bearer {tu-jwt-token}
> ```

#### 3. Obtener Todos los Usuarios
```http
GET /v1/users
Authorization: Bearer {token}
```

**Respuesta (200):**
```json
[
  {
    "id": 1,
    "email": "user@example.com",
    "firstName": "Juan",
    "lastName": "Pérez",
    "phoneNumber": "123456789",
    "createdAt": "2025-10-15T00:06:03.450796",
    "updatedAt": "2025-10-15T00:06:03.450988"
  }
]
```

---

#### 4. Obtener Usuario por ID
```http
GET /v1/users/{id}
Authorization: Bearer {token}
```

**Respuesta (200):**
```json
{
  "id": 1,
  "email": "user@example.com",
  "firstName": "Juan",
  "lastName": "Pérez",
  "phoneNumber": "123456789",
  "createdAt": "2025-10-15T00:06:03.450796",
  "updatedAt": "2025-10-15T00:06:03.450988"
}
```

---

#### 5. Obtener Usuario por Email
```http
GET /v1/users/email/{email}
Authorization: Bearer {token}
```

**Ejemplo:**
```http
GET /v1/users/email/user@example.com
Authorization: Bearer {token}
```

---

#### 6. Actualizar Usuario
```http
PUT /v1/users/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "firstName": "Juan Carlos",
  "lastName": "Pérez García",
  "phoneNumber": "987654321"
}
```

**Respuesta (200):**
```json
{
  "id": 1,
  "email": "user@example.com",
  "firstName": "Juan Carlos",
  "lastName": "Pérez García",
  "phoneNumber": "987654321",
  "updatedAt": "2025-10-15T01:30:00.123456"
}
```

---

#### 7. Eliminar Usuario
```http
DELETE /v1/users/{id}
Authorization: Bearer {token}
```

**Respuesta (200):**
```json
{
  "message": "User deleted successfully"
}
```

---

## 📮 Uso con Postman

### Configuración Inicial

1. **Importar colección** (ver archivo `Delivery-Trujillo-API.postman_collection.json`)

2. **Configurar variable de entorno:**
   - Variable: `jwt_token`
   - Scope: Collection

3. **Flujo de trabajo:**

```
1. POST Register → Crea usuario
2. POST Login → Guarda el JWT automáticamente
3. Usar cualquier endpoint protegido → El JWT se agrega automáticamente
```

### Scripts Automáticos en Postman

**En el endpoint Login, agregar en "Tests":**
```javascript
// Guarda el JWT automáticamente
if (pm.response.code === 200) {
    const response = pm.response.json();
    pm.collectionVariables.set("jwt_token", response.jwt);
    console.log("✅ JWT guardado:", response.jwt);
}
```

**En endpoints protegidos, agregar en "Pre-request Script":**
```javascript
// Verifica que exista el token
const token = pm.collectionVariables.get("jwt_token");
if (!token) {
    console.error("❌ No hay JWT. Ejecuta Login primero.");
}
```

---

## 💻 Uso con PowerShell/Curl

Ver archivo completo: `api-requests.ps1`

### Ejemplos rápidos:

```powershell
# 1. Registro
Invoke-RestMethod -Uri "http://localhost:8080/v1/auth/register" `
  -Method POST -ContentType "application/json" `
  -Body '{"email":"test@test.com","password":"Test@1234","firstName":"Test","lastName":"User","phoneNumber":"123456789"}'

# 2. Login y guardar token
$response = Invoke-RestMethod -Uri "http://localhost:8080/v1/auth/login" `
  -Method POST -ContentType "application/json" `
  -Body '{"email":"test@test.com","password":"Test@1234"}'
$token = $response.jwt

# 3. Usar el token
Invoke-RestMethod -Uri "http://localhost:8080/v1/users" `
  -Method GET -Headers @{ Authorization = "Bearer $token" }
```

---

## 🔒 Seguridad y Mejores Prácticas

### ✅ Implementadas en este proyecto:

1. **Contraseñas hasheadas** con BCrypt
2. **JWT firmado** con RS256
3. **Tokens con expiración** (4 horas)
4. **HTTPS recomendado** en producción
5. **Validación de entrada** en registro
6. **CORS configurado** para frontend específico

### 🚀 Mejoras sugeridas para producción:

1. **Refresh Tokens**: Implementar tokens de actualización
2. **Rate Limiting**: Limitar peticiones por IP
3. **Blacklist de tokens**: Invalidar tokens al logout
4. **Roles y permisos**: Implementar RBAC
5. **Logging**: Auditoría de accesos
6. **HTTPS**: Siempre en producción
7. **Secrets management**: Usar Azure Key Vault o similar

---

## 📚 Referencias

- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/)
- [JWT.io](https://jwt.io/)
- [RFC 7519 - JWT](https://tools.ietf.org/html/rfc7519)
- [OWASP Security Best Practices](https://owasp.org/)
- [Nimbus JOSE+JWT](https://connect2id.com/products/nimbus-jose-jwt)

---

## 📄 Licencia

Este proyecto es parte de Delivery Trujillo Services.

**Desarrollado con ❤️ usando Spring Boot 3.3.5 y Java 21**
