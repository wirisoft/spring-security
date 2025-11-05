# 🚀 Despliegue Rápido en Render.com

## ⚡ Inicio Rápido (5 minutos)

### 1. **Preparar Repositorio Git**
```bash
git add .
git commit -m "Ready for Render deployment"
git push origin main
```

### 2. **Crear Cuenta en Render**
- Ve a [render.com](https://render.com)
- Crea una cuenta gratuita
- Conecta tu cuenta de GitHub/GitLab/Bitbucket

### 3. **Crear Base de Datos PostgreSQL**
1. En Render Dashboard → **"New +"** → **"PostgreSQL"**
2. Configurar:
   - **Name:** `delivery-trujillo-db`
   - **Database:** `deliveryTrujillo`
   - **Plan:** `Free`
3. Click **"Create Database"**
4. ⚠️ **Guarda las credenciales** que te muestra

### 4. **Crear Web Service (API)**
1. En Render Dashboard → **"New +"** → **"Web Service"**
2. Conectar tu repositorio
3. Configurar:
   - **Name:** `delivery-trujillo-api`
   - **Runtime:** `Docker`
   - **Dockerfile Path:** `./Dockerfile`
   - **Plan:** `Free`
4. **Agregar Variables de Entorno:**
   ```bash
   SPRING_DATASOURCE_URL=jdbc:postgresql://[TU_HOST]:5432/deliveryTrujillo
   SPRING_DATASOURCE_USERNAME=postgres
   SPRING_DATASOURCE_PASSWORD=[TU_PASSWORD]
   SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver
   SPRING_JPA_HIBERNATE_DDL_AUTO=update
   SPRING_JPA_SHOW_SQL=false
   SERVER_PORT=8080
   ```
5. Click **"Create Web Service"**

### 5. **Configurar Claves JWT**

**Generar Claves:**
```bash
mkdir -p src/main/resources/jwtKeys
openssl genpkey -algorithm RSA -out src/main/resources/jwtKeys/private_key.pem -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in src/main/resources/jwtKeys/private_key.pem -out src/main/resources/jwtKeys/public_key.pem
```

**Convertir a Base64:**
```bash
# Windows PowerShell
$privateKey = [Convert]::ToBase64String([System.IO.File]::ReadAllBytes("src/main/resources/jwtKeys/private_key.pem"))
$publicKey = [Convert]::ToBase64String([System.IO.File]::ReadAllBytes("src/main/resources/jwtKeys/public_key.pem"))
Write-Host "Private Key: $privateKey"
Write-Host "Public Key: $publicKey"

# Linux/Mac
base64 -i src/main/resources/jwtKeys/private_key.pem
base64 -i src/main/resources/jwtKeys/public_key.pem
```

**Agregar Variables en Render:**
1. En el dashboard de tu Web Service → **"Environment"**
2. Agregar:
   ```bash
   JWT_PRIVATE_KEY_B64=[tu_clave_privada_en_base64]
   JWT_PUBLIC_KEY_B64=[tu_clave_publica_en_base64]
   ```

### 6. **Desplegar**
- Render desplegará automáticamente al hacer push a `main`
- O manualmente: Dashboard → **"Manual Deploy"**

---

## ✅ Verificar Despliegue

```bash
# Tu URL será algo como: https://delivery-trujillo-api.onrender.com
curl https://tu-servicio.onrender.com/v1/restaurants/home

# Deberías recibir un 401 (No autorizado) = API funcionando ✅
```

---

## 📚 Documentación Completa

Para más detalles, consulta:
- **`GUIA_RENDER.md`** - Guía completa paso a paso
- **`render.yaml`** - Configuración como código (opcional)

---

## ⚠️ Notas Importantes

1. **Plan Gratuito:** 
   - El servicio "duerme" después de 15 min de inactividad
   - El primer request puede tardar 30-60 segundos (cold start)

2. **Base de Datos:**
   - 1 GB de almacenamiento en plan gratuito
   - Backups automáticos (7 días)

3. **Claves JWT:**
   - ⚠️ **NO subir las claves JWT a Git**
   - Usar variables de entorno en Render

4. **Logs:**
   - Ver logs en tiempo real en Render Dashboard → **"Logs"**

---

**Última actualización:** 2025-01-20

