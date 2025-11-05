# 📋 Configuración en Render - Guía Paso a Paso

## ⚙️ Configuración de Campos en Render

### **Campos Básicos (ya configurados correctamente):**

1. **Source Code:**
   - ✅ `wirisoft/spring-security` (ya conectado)

2. **Select a service type:**
   - ✅ `Web Service` (ya seleccionado)

3. **Name:**
   - ✅ `delivery-trujillo-api` (correcto)

4. **Language:**
   - ✅ `Docker` (ya seleccionado)

5. **Branch:**
   - ✅ `main` (correcto)

6. **Region:**
   - ✅ `Oregon (US West)` (correcto)

7. **Root Directory:**
   - ✅ **Dejar vacío** (o `./`)

8. **Instance Type:**
   - ✅ `Free` (correcto para fines académicos)

---

### **Sección "Advanced" - Configuración Crítica:**

#### **1. Health Check Path:**
```
/v1/restaurants/home
```
**⚠️ IMPORTANTE:** Cambia el valor actual `/healthz` por `/v1/restaurants/home`

#### **2. Docker Build Context Directory:**
```
.
```
**✅ Correcto:** Ya está en `.`

#### **3. Dockerfile Path:**
```
./Dockerfile
```
**⚠️ IMPORTANTE:** Cambia el valor actual `.` por `./Dockerfile`

#### **4. Docker Command:**
**✅ Dejar vacío** (usa el ENTRYPOINT del Dockerfile)

#### **5. Pre-Deploy Command:**
**✅ Dejar vacío**

#### **6. Auto-Deploy:**
**✅ On Commit** (activado - correcto)

---

### **Sección "Environment Variables":**

**⚠️ IMPORTANTE:** Como es para fines académicos y NO usarás variables de entorno, puedes dejar esta sección vacía. La configuración vendrá del `application.properties`.

**Nota:** Si Render requiere al menos una variable, puedes agregar solo:
```
SPRING_PROFILES_ACTIVE=production
```

---

### **Sección "Secret Files":**

**⚠️ IMPORTANTE:** Si las claves JWT están en el repositorio, NO necesitas configurar nada aquí. El Dockerfile ya las copia desde el repositorio.

---

## 📝 Pasos a Seguir ANTES de Desplegar:

### **Paso 1: Crear Base de Datos PostgreSQL**

1. **En Render Dashboard:**
   - Click en **"New +"** → **"PostgreSQL"**

2. **Configurar:**
   - **Name:** `delivery-trujillo-db`
   - **Database:** `deliverytrujillo`
   - **User:** `postgres`
   - **Plan:** `Free`
   - **Region:** `Oregon (US West)` (misma que el API)

3. **Crear y guardar credenciales:**
   - Render te mostrará algo como:
     ```
     Host: dpg-xxxxx-a.oregon-postgres.render.com
     Port: 5432
     Database: deliveryTrujillo
     User: postgres
     Password: [tu_password]
     ```

4. **⚠️ IMPORTANTE:** Guarda estas credenciales

---

### **Paso 2: Actualizar application.properties**

**Antes de hacer commit y push**, actualiza el `application.properties` con las credenciales de Render:

```properties
# ----------------------------------------------------
# 2. Configuracion de la Fuente de Datos (PostgreSQL)
# ----------------------------------------------------
spring.datasource.url=jdbc:postgresql://[TU_HOST_DE_RENDER]:5432/deliverytrujillo
spring.datasource.username=postgres
spring.datasource.password=[TU_PASSWORD_DE_RENDER]
spring.datasource.driver-class-name=org.postgresql.Driver
```

**Ejemplo:**
```properties
spring.datasource.url=jdbc:postgresql://dpg-xxxxx-a.oregon-postgres.render.com:5432/deliverytrujillo
spring.datasource.username=postgres
spring.datasource.password=tu_password_aqui
```

---

### **Paso 3: Verificar que las Claves JWT Estén en el Repositorio**

**Asegúrate de que las claves JWT estén en:**
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

**⚠️ NOTA ACADÉMICA:** Para fines académicos, puedes subir las claves JWT al repositorio. En producción, NO se debe hacer esto.

---

### **Paso 4: Hacer Commit y Push**

```bash
git add .
git commit -m "Configuración para Render - Credenciales BD"
git push origin main
```

---

### **Paso 5: Desplegar en Render**

1. **Verifica que todos los campos estén configurados:**
   - ✅ Health Check Path: `/v1/restaurants/home`
   - ✅ Dockerfile Path: `./Dockerfile`
   - ✅ Docker Build Context: `.`

2. **Click en "Deploy web service"**

3. **Espera el build** (5-15 minutos)

---

## ✅ Verificación Post-Despliegue

### **1. Ver Logs:**
- En Render Dashboard → Tu servicio → **"Logs"**
- Busca: `✅ Started AppTrujilloServicesApplication`

### **2. Probar el API:**
```bash
curl https://delivery-trujillo-api.onrender.com/v1/restaurants/home
```

**Deberías recibir:** `401 Unauthorized` = ✅ API funcionando

---

## 📋 Resumen de Configuración

| Campo | Valor |
|-------|-------|
| **Source Code** | `wirisoft/spring-security` |
| **Service Type** | `Web Service` |
| **Name** | `delivery-trujillo-api` |
| **Language** | `Docker` |
| **Branch** | `main` |
| **Region** | `Oregon (US West)` |
| **Root Directory** | (vacío) |
| **Instance Type** | `Free` |
| **Health Check Path** | `/v1/restaurants/home` |
| **Docker Build Context** | `.` |
| **Dockerfile Path** | `./Dockerfile` |
| **Auto-Deploy** | `On Commit` |

---

## 🚨 Notas Importantes

1. **Credenciales en application.properties:** Para fines académicos, las credenciales están en `application.properties`. En producción, usa variables de entorno.

2. **Claves JWT en el repositorio:** Para fines académicos, las claves JWT están en el repositorio. En producción, usa variables de entorno o secretos.

3. **Plan Gratuito:** El servicio "duerme" después de 15 minutos de inactividad. El primer request puede tardar 30-60 segundos.

4. **Base de Datos:** Asegúrate de crear la BD ANTES de desplegar el API.

---

**Última actualización:** 2025-01-20

