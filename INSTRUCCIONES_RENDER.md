# 🚀 Instrucciones para Subir el API a Render

## ✅ Paso 1: Esperar a que la Base de Datos Termine de Crearse

Tu base de datos está creándose:
- **Hostname:** `dpg-d45tvcm3jp1c73dp9im0-a.oregon-postgres.render.com`
- **Port:** `5432`
- **Database:** `deliverytrujillo`
- **Username:** `deliverytrujillo_user`
- **Password:** ⏳ Esperando...

**Acción:** Espera a que el estado cambie de "creating" a "Available" (puede tardar 2-5 minutos).

---

## ✅ Paso 2: Obtener el Password de la Base de Datos

Una vez que la BD esté lista:

1. **En el dashboard de tu BD en Render:**
   - Ve a la sección **"Connections"**
   - Verás el **"Password"** disponible
   - **⚠️ IMPORTANTE:** Copia el password, solo se muestra una vez

2. **También verás:**
   - **Internal Database URL:** Para usar dentro de Render
   - **External Database URL:** Para usar desde fuera de Render
   - **PSQL Command:** Para conectar desde terminal

---

## ✅ Paso 3: Actualizar application.properties

Actualiza el archivo `src/main/resources/application.properties` con el password real:

```properties
spring.datasource.url=jdbc:postgresql://dpg-d45tvcm3jp1c73dp9im0-a.oregon-postgres.render.com:5432/deliverytrujillo
spring.datasource.username=deliverytrujillo_user
spring.datasource.password=[TU_PASSWORD_REAL_AQUI]
```

**Ejemplo:**
```properties
spring.datasource.password=abc123xyz456
```

---

## ✅ Paso 4: Verificar que las Claves JWT Estén en el Repositorio

Asegúrate de que las claves JWT existan en:
```
src/main/resources/jwtKeys/private_key.pem
src/main/resources/jwtKeys/public_key.pem
```

**Si no existen, genéralas:**

```powershell
# Crear directorio
mkdir -p src/main/resources/jwtKeys

# Generar claves (requiere OpenSSL)
openssl genpkey -algorithm RSA -out src/main/resources/jwtKeys/private_key.pem -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in src/main/resources/jwtKeys/private_key.pem -out src/main/resources/jwtKeys/public_key.pem
```

---

## ✅ Paso 5: Hacer Commit y Push

```bash
# Agregar cambios
git add .

# Commit con el password actualizado
git commit -m "Configuración para Render: Credenciales BD actualizadas"

# Push a GitHub
git push origin main
```

---

## ✅ Paso 6: Crear Web Service en Render

### 6.1. Ir a Crear Web Service

1. En Render Dashboard → **"New +"** → **"Web Service"**

2. **Conectar Repositorio:**
   - Selecciona **"Git Provider"** → **"GitHub"**
   - Busca y selecciona: `wirisoft/spring-security`
   - Click en **"Connect"**

### 6.2. Configurar el Servicio

**Campos Básicos:**
- **Name:** `delivery-trujillo-api`
- **Language:** `Docker` (ya seleccionado)
- **Branch:** `main`
- **Region:** `Oregon (US West)`
- **Root Directory:** (vacío)
- **Instance Type:** `Free`

**Sección "Advanced" (MUY IMPORTANTE):**
- **Health Check Path:** `/v1/restaurants/home`
- **Docker Build Context Directory:** `.`
- **Dockerfile Path:** `./Dockerfile`
- **Docker Command:** (vacío)
- **Pre-Deploy Command:** (vacío)
- **Auto-Deploy:** `On Commit` (activado)

**Sección "Environment Variables":**
- Como es académico, puedes dejar vacío o agregar solo:
  ```
  SPRING_PROFILES_ACTIVE=production
  ```

**Sección "Secret Files":**
- Dejar vacío (las claves JWT están en el repositorio)

### 6.3. Desplegar

1. **Click en "Deploy web service"**
2. **Render comenzará el build** (puede tardar 5-15 minutos)
3. **Verás los logs en tiempo real**

---

## ✅ Paso 7: Verificar el Despliegue

### 7.1. Ver Logs

En Render Dashboard → Tu Web Service → **"Logs"**

**Busca estos mensajes:**
```
✅ Started AppTrujilloServicesApplication
✅ Tomcat started on port(s): 8080
✅ Hikari Connection Pool: HikariPool-1 - Start completed
```

### 7.2. Probar el API

Tu URL será algo como: `https://delivery-trujillo-api.onrender.com`

**Probar:**
```bash
curl https://delivery-trujillo-api.onrender.com/v1/restaurants/home
```

**Deberías recibir:** `401 Unauthorized` = ✅ API funcionando correctamente

---

## 🚨 Solución de Problemas

### Problema: Build Falla

**Solución:**
- Ver logs en Render Dashboard
- Verificar que el Dockerfile esté correcto
- Verificar que todas las dependencias estén en `build.gradle`

### Problema: No se Conecta a la Base de Datos

**Solución:**
1. Verificar que la BD esté en estado "Available"
2. Verificar que el `application.properties` tenga las credenciales correctas
3. Verificar que el hostname sea correcto: `dpg-d45tvcm3jp1c73dp9im0-a.oregon-postgres.render.com`

### Problema: Error de Claves JWT

**Solución:**
1. Verificar que las claves JWT existan en el repositorio
2. Verificar que las rutas en `application.properties` sean correctas: `/app/jwtKeys/...`

---

## 📋 Checklist Final

- [ ] Base de datos creada y en estado "Available"
- [ ] Password de la BD copiado
- [ ] `application.properties` actualizado con credenciales reales
- [ ] Claves JWT generadas y en el repositorio
- [ ] Cambios commiteados y pusheados a GitHub
- [ ] Web Service creado en Render
- [ ] Build completado exitosamente
- [ ] API respondiendo correctamente

---

## 🎯 Resumen de Credenciales

**Base de Datos:**
- **Hostname:** `dpg-d45tvcm3jp1c73dp9im0-a.oregon-postgres.render.com`
- **Port:** `5432`
- **Database:** `deliverytrujillo`
- **Username:** `deliverytrujillo_user`
- **Password:** ⏳ Obtener cuando la BD esté lista

**URL del API:**
- `https://delivery-trujillo-api.onrender.com` (después del deploy)

---

**Última actualización:** 2025-01-20

