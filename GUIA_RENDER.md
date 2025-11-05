# 🚀 Guía de Despliegue en Render.com (Capa Gratuita)

## 📋 Requisitos Previos

1. **Cuenta en Render.com** - Crear cuenta gratuita en [render.com](https://render.com)
2. **Repositorio Git** - Proyecto en GitHub, GitLab o Bitbucket
3. **Claves JWT** - Generadas y listas para subir

---

## 🎯 Paso 1: Preparar el Repositorio

### 1.1 Subir el Código a Git

```bash
# Si aún no tienes un repositorio Git
git init
git add .
git commit -m "Initial commit - Ready for Render deployment"
git branch -M main

# Conectar con tu repositorio remoto (GitHub/GitLab/Bitbucket)
git remote add origin https://github.com/tu-usuario/app-trujillo-services.git
git push -u origin main
```

### 1.2 Verificar Archivos Necesarios

Asegúrate de tener estos archivos en tu repositorio:
- ✅ `Dockerfile`
- ✅ `render.yaml` (opcional, pero recomendado)
- ✅ `build.gradle`
- ✅ `src/` (código fuente)
- ✅ `src/main/resources/jwtKeys/` (claves JWT - **NO subir a Git por seguridad**)

---

## 🗄️ Paso 2: Crear Base de Datos PostgreSQL

### Opción A: Desde el Dashboard (Recomendado)

1. **Inicia sesión en Render.com**
   - Ve a [dashboard.render.com](https://dashboard.render.com)

2. **Crear Nueva Base de Datos**
   - Click en **"New +"**
   - Selecciona **"PostgreSQL"**

3. **Configurar Base de Datos**
   - **Name:** `delivery-trujillo-db`
   - **Database:** `deliveryTrujillo`
   - **User:** `postgres` (o el que prefieras)
   - **Region:** `Oregon` (o la más cercana)
   - **Plan:** `Free`
   - **PostgreSQL Version:** `14`

4. **Crear Base de Datos**
   - Click en **"Create Database"**
   - ⚠️ **IMPORTANTE:** Guarda las credenciales de conexión que te muestra

### Opción B: Usando render.yaml (Automático)

Si usas `render.yaml`, la base de datos se creará automáticamente cuando despliegues el servicio.

---

## 🌐 Paso 3: Crear Web Service (API)

### Opción A: Desde el Dashboard (Recomendado para primera vez)

1. **Crear Nuevo Web Service**
   - Click en **"New +"**
   - Selecciona **"Web Service"**

2. **Conectar Repositorio**
   - Conecta tu cuenta de GitHub/GitLab/Bitbucket
   - Selecciona el repositorio `app-trujillo-services`
   - Click en **"Connect"**

3. **Configurar el Servicio**
   - **Name:** `delivery-trujillo-api`
   - **Region:** `Oregon` (o la misma que la BD)
   - **Branch:** `main`
   - **Root Directory:** `./` (raíz del proyecto)
   - **Runtime:** `Docker`
   - **Dockerfile Path:** `./Dockerfile`
   - **Docker Context:** `.`
   - **Plan:** `Free`

4. **Configurar Variables de Entorno**

   Click en **"Environment"** y agrega estas variables:

   ```bash
   # Database (usar las credenciales de tu base de datos)
   SPRING_DATASOURCE_URL=jdbc:postgresql://dpg-xxxxx-a.oregon-postgres.render.com/deliveryTrujillo
   SPRING_DATASOURCE_USERNAME=postgres
   SPRING_DATASOURCE_PASSWORD=tu_password_aqui
   SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver
   
   # JPA Configuration
   SPRING_JPA_HIBERNATE_DDL_AUTO=update
   SPRING_JPA_SHOW_SQL=false
   SPRING_JPA_PROPERTIES_HIBERNATE_FORMAT_SQL=true
   
   # JWT Keys (usar las rutas dentro del contenedor)
   JWT_KEYS_PRIVATE_KEY_PATH=/app/jwtKeys/private_key.pem
   JWT_KEYS_PUBLIC_KEY_PATH=/app/jwtKeys/public_key.pem
   
   # Server
   SERVER_PORT=8080
   SPRING_PROFILES_ACTIVE=production
   ```

5. **Configurar Health Check**
   - **Health Check Path:** `/v1/restaurants/home`
   - **Health Check Interval:** `300` (5 minutos)

6. **Crear Servicio**
   - Click en **"Create Web Service"**

### Opción B: Usando render.yaml (Automático)

1. **Subir render.yaml al repositorio**
   ```bash
   git add render.yaml
   git commit -m "Add Render configuration"
   git push
   ```

2. **En Render Dashboard**
   - Click en **"New +"** → **"Blueprint"**
   - Conecta tu repositorio
   - Render detectará automáticamente `render.yaml`
   - Click en **"Apply"**

---

## 🔐 Paso 4: Configurar Claves JWT

### Opción 1: Usar Variables de Entorno (Recomendado)

1. **Generar Claves JWT** (si no las tienes)
   ```bash
   mkdir -p src/main/resources/jwtKeys
   openssl genpkey -algorithm RSA -out src/main/resources/jwtKeys/private_key.pem -pkeyopt rsa_keygen_bits:2048
   openssl rsa -pubout -in src/main/resources/jwtKeys/private_key.pem -out src/main/resources/jwtKeys/public_key.pem
   ```

2. **Convertir Claves a Base64**
   ```bash
   # En Windows PowerShell
   $privateKey = Get-Content src/main/resources/jwtKeys/private_key.pem -Raw | ConvertTo-Base64
   $publicKey = Get-Content src/main/resources/jwtKeys/public_key.pem -Raw | ConvertTo-Base64
   
   # En Linux/Mac
   base64 -i src/main/resources/jwtKeys/private_key.pem
   base64 -i src/main/resources/jwtKeys/public_key.pem
   ```

3. **Agregar Variables de Entorno en Render**
   - En el dashboard de tu Web Service
   - Ve a **"Environment"**
   - Agrega:
     ```bash
     JWT_PRIVATE_KEY_B64=<tu_clave_privada_en_base64>
     JWT_PUBLIC_KEY_B64=<tu_clave_publica_en_base64>
     ```

4. **Modificar Dockerfile para Usar Variables de Entorno**
   
   Agrega esto al Dockerfile antes del ENTRYPOINT:
   ```dockerfile
   # Crear claves JWT desde variables de entorno si existen
   RUN if [ -n "$JWT_PRIVATE_KEY_B64" ] && [ -n "$JWT_PUBLIC_KEY_B64" ]; then \
       echo "$JWT_PRIVATE_KEY_B64" | base64 -d > /app/jwtKeys/private_key.pem && \
       echo "$JWT_PUBLIC_KEY_B64" | base64 -d > /app/jwtKeys/public_key.pem; \
   fi
   ```

### Opción 2: Subir Claves en el Build (No Recomendado)

⚠️ **NO RECOMENDADO:** No subas las claves JWT directamente al repositorio Git.

### Opción 3: Usar Render Secrets (Recomendado para Producción)

1. En Render Dashboard → **"Secrets"**
2. Crear secretos para las claves JWT
3. Referenciarlos en las variables de entorno

---

## 🔗 Paso 5: Conectar Base de Datos con el API

### Si creaste la BD manualmente:

1. **En el Dashboard de tu Web Service**
   - Ve a **"Environment"**
   - Busca la sección **"Add from Database"**
   - Selecciona tu base de datos `delivery-trujillo-db`
   - Render agregará automáticamente las variables:
     - `DATABASE_URL`
     - `DATABASE_HOST`
     - `DATABASE_PORT`
     - `DATABASE_NAME`
     - `DATABASE_USER`
     - `DATABASE_PASSWORD`

2. **Configurar SPRING_DATASOURCE_URL**
   
   Si Render no crea automáticamente `SPRING_DATASOURCE_URL`, configúrala manualmente:
   ```
   SPRING_DATASOURCE_URL=jdbc:postgresql://[HOST]:[PORT]/[DATABASE_NAME]
   ```
   
   Ejemplo:
   ```
   SPRING_DATASOURCE_URL=jdbc:postgresql://dpg-xxxxx-a.oregon-postgres.render.com:5432/deliveryTrujillo
   ```

### Si usas render.yaml:

La conexión se configura automáticamente usando `fromDatabase`.

---

## 🚀 Paso 6: Desplegar

### Despliegue Automático

1. **Push a la rama principal**
   ```bash
   git push origin main
   ```

2. **Render detectará el push automáticamente**
   - Irá a **"Events"** en el dashboard
   - Verás el build en progreso

3. **Esperar el Build**
   - El build puede tardar 5-15 minutos
   - Puedes ver los logs en tiempo real

### Despliegue Manual

1. En el dashboard de tu Web Service
2. Click en **"Manual Deploy"**
3. Selecciona la rama y commit
4. Click en **"Deploy"**

---

## ✅ Paso 7: Verificar el Despliegue

### 1. Verificar que el Servicio Esté Corriendo

- En el dashboard, verifica que el estado sea **"Live"**
- El health check debería estar **"Healthy"**

### 2. Probar el API

```bash
# Obtener la URL de tu servicio (ejemplo: https://delivery-trujillo-api.onrender.com)
curl https://tu-servicio.onrender.com/v1/restaurants/home

# Deberías recibir un 401 (No autorizado) que significa que el API está funcionando
```

### 3. Ver Logs

- En el dashboard → **"Logs"**
- Verifica que no haya errores
- Busca: `Started AppTrujilloServicesApplication`

---

## 📊 Monitoreo y Logs

### Ver Logs en Tiempo Real

1. En el dashboard de tu Web Service
2. Click en **"Logs"**
3. Verás los logs en tiempo real

### Logs Importantes a Buscar

```
✅ Started AppTrujilloServicesApplication
✅ Tomcat started on port(s): 8080
✅ Hikari Connection Pool: HikariPool-1 - Start completed
✅ Database connection established
❌ Error: Connection refused (BD no conectada)
❌ JPA Error: ... (problemas con la BD)
```

---

## 🔧 Configuración Avanzada

### Cambiar Plan (de Free a Paid)

1. En el dashboard → **"Settings"**
2. Click en **"Change Plan"**
3. Selecciona el plan deseado

### Configurar Dominio Personalizado

1. En el dashboard → **"Settings"**
2. Click en **"Custom Domains"**
3. Agrega tu dominio
4. Configura DNS según las instrucciones

### Configurar Auto-Deploy

1. En el dashboard → **"Settings"**
2. **"Auto-Deploy"** → Activar
3. Seleccionar rama (ej: `main`)

---

## 🐛 Solución de Problemas

### Problema: Build Falla

**Solución:**
```bash
# Ver logs del build
# En Render Dashboard → "Logs"

# Verificar que el Dockerfile esté correcto
# Verificar que todas las dependencias estén en build.gradle
```

### Problema: No se Conecta a la Base de Datos

**Solución:**
1. Verificar variables de entorno:
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`

2. Verificar que la BD esté corriendo:
   - En el dashboard de la BD, verificar estado "Available"

3. Verificar que la BD y el API estén en la misma región

### Problema: Error de Claves JWT

**Solución:**
1. Verificar que las claves JWT estén configuradas
2. Verificar que las rutas sean correctas
3. Verificar permisos de archivos

### Problema: El Servicio se Cae Después de Inactividad

**Solución:**
- Esto es normal en el plan gratuito
- Render "duerme" los servicios después de 15 minutos de inactividad
- El primer request puede tardar 30-60 segundos (cold start)
- Para evitar esto, usa un plan de pago o configura un cron job que haga ping periódico

---

## 💰 Límites del Plan Gratuito

### Web Service (API)
- **RAM:** 512 MB
- **CPU:** 0.1 CPU compartido
- **Sleep:** Después de 15 min de inactividad
- **Build Time:** Ilimitado (pero lento)
- **Bandwidth:** 100 GB/mes

### PostgreSQL Database
- **RAM:** 256 MB
- **Storage:** 1 GB
- **CPU:** Compartido
- **Backups:** Automáticos (7 días)

---

## 📝 Checklist de Despliegue

- [ ] Repositorio Git creado y código subido
- [ ] Base de datos PostgreSQL creada en Render
- [ ] Web Service creado en Render
- [ ] Variables de entorno configuradas
- [ ] Claves JWT configuradas
- [ ] Base de datos conectada al API
- [ ] Build completado exitosamente
- [ ] Servicio en estado "Live"
- [ ] Health check pasando
- [ ] API respondiendo correctamente

---

## 🎯 Siguientes Pasos

1. **Configurar Dominio Personalizado** (opcional)
2. **Configurar Auto-Deploy** (si no lo has hecho)
3. **Configurar Monitoreo** (Render tiene monitoreo básico)
4. **Configurar Alertas** (para errores críticos)
5. **Hacer Backup de la Base de Datos** (configurar backups automáticos)

---

## 📚 Recursos Adicionales

- [Documentación de Render](https://render.com/docs)
- [Render Community](https://community.render.com)
- [Render Status](https://status.render.com)

---

**Última actualización:** 2025-01-20  
**Versión:** 1.0

