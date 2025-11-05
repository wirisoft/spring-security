# 🐳 Guía de Docker - Delivery Trujillo Services

## 📋 Requisitos Previos

- Docker Desktop instalado y funcionando
- Docker Compose v2.0 o superior
- Mínimo 4GB de RAM disponibles
- Puertos 8080 y 5432 disponibles

---

## 🚀 Inicio Rápido

### **Opción 1: Build y Levantar Todo (Recomendado)**

```bash
# Construir y levantar todos los servicios (API + Base de datos)
docker-compose up --build
```

Este comando:
1. Construye la imagen de la API
2. Descarga la imagen de PostgreSQL
3. Levanta la base de datos
4. Espera a que la base de datos esté lista (health check)
5. Levanta la API

### **Opción 2: Build en Background**

```bash
# Construir y levantar en segundo plano
docker-compose up --build -d

# Ver logs
docker-compose logs -f api-service

# Ver logs de la base de datos
docker-compose logs -f db
```

### **Opción 3: Solo Base de Datos (Para Desarrollo)**

```bash
# Solo levantar la base de datos para desarrollo local
docker-compose -f docker-compose.dev.yml up -d
```

---

## 📝 Comandos Útiles

### **Gestión de Contenedores**

```bash
# Ver contenedores en ejecución
docker-compose ps

# Detener todos los servicios
docker-compose down

# Detener y eliminar volúmenes (⚠️ BORRA LOS DATOS)
docker-compose down -v

# Reiniciar un servicio específico
docker-compose restart api-service

# Ver logs en tiempo real
docker-compose logs -f api-service

# Ver logs de todos los servicios
docker-compose logs -f
```

### **Base de Datos**

```bash
# Conectar a la base de datos PostgreSQL
docker-compose exec db psql -U postgres -d deliveryTrujillo

# Ejecutar comandos SQL
docker-compose exec db psql -U postgres -d deliveryTrujillo -c "SELECT * FROM users;"

# Hacer backup de la base de datos
docker-compose exec db pg_dump -U postgres deliveryTrujillo > backup.sql

# Restaurar backup
docker-compose exec -T db psql -U postgres deliveryTrujillo < backup.sql
```

### **API Service**

```bash
# Ver logs de la API
docker-compose logs -f api-service

# Reiniciar solo la API
docker-compose restart api-service

# Ejecutar comandos dentro del contenedor
docker-compose exec api-service sh

# Ver variables de entorno
docker-compose exec api-service env
```

---

## 🔧 Configuración

### **Variables de Entorno**

Las variables de entorno se configuran en `docker-compose.yml`:

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/deliveryTrujillo
  SPRING_DATASOURCE_USERNAME: postgres
  SPRING_DATASOURCE_PASSWORD: qwerty
  SPRING_JPA_HIBERNATE_DDL_AUTO: update
```

### **Puertos**

- **API:** `http://localhost:8080`
- **PostgreSQL:** `localhost:5432`

### **Volúmenes**

- **Base de datos:** `postgres_data` - Persiste los datos de PostgreSQL
- **Claves JWT:** Se montan desde `./src/main/resources/jwtKeys` al contenedor

---

## 🔐 Configuración de Claves JWT

### **Generar Claves JWT**

**En Windows (PowerShell):**

```powershell
# Crear directorio si no existe
New-Item -ItemType Directory -Force -Path src/main/resources/jwtKeys

# Generar clave privada
openssl genpkey -algorithm RSA -out src/main/resources/jwtKeys/private_key.pem -pkeyopt rsa_keygen_bits:2048

# Generar clave pública
openssl rsa -pubout -in src/main/resources/jwtKeys/private_key.pem -out src/main/resources/jwtKeys/public_key.pem
```

**En Linux/Mac:**

```bash
# Crear directorio
mkdir -p src/main/resources/jwtKeys

# Generar clave privada
openssl genpkey -algorithm RSA -out src/main/resources/jwtKeys/private_key.pem -pkeyopt rsa_keygen_bits:2048

# Generar clave pública
openssl rsa -pubout -in src/main/resources/jwtKeys/private_key.pem -out src/main/resources/jwtKeys/public_key.pem
```

---

## 🏗️ Proceso de Build

### **1. Build de la Imagen**

El Dockerfile usa multi-stage build:

```dockerfile
# Stage 1: BUILD
- Compila la aplicación con Gradle
- Genera el JAR ejecutable

# Stage 2: RUN
- Copia el JAR a una imagen JRE ligera
- Configura las claves JWT
- Expone el puerto 8080
```

### **2. Health Checks**

- **Base de datos:** Verifica que PostgreSQL esté listo con `pg_isready`
- **API:** Verifica que la aplicación responda (si hay actuator)

---

## 🐛 Solución de Problemas

### **Problema: La API no se conecta a la base de datos**

**Solución:**
```bash
# Verificar que la base de datos esté corriendo
docker-compose ps

# Ver logs de la base de datos
docker-compose logs db

# Verificar health check
docker-compose exec db pg_isready -U postgres
```

### **Problema: Error al iniciar (puerto en uso)**

**Solución:**
```bash
# Ver qué está usando el puerto 8080
netstat -ano | findstr :8080  # Windows
lsof -i :8080                  # Linux/Mac

# Cambiar el puerto en docker-compose.yml
ports:
  - "8081:8080"  # Cambiar 8080 por 8081
```

### **Problema: Error de permisos con claves JWT**

**Solución:**
```bash
# Verificar que las claves existan
ls -la src/main/resources/jwtKeys/

# Asegurar permisos correctos
chmod 644 src/main/resources/jwtKeys/*.pem
```

### **Problema: La base de datos se reinicia constantemente**

**Solución:**
```bash
# Ver logs de la base de datos
docker-compose logs db

# Verificar espacio en disco
docker system df

# Limpiar volúmenes no usados
docker volume prune
```

---

## 📊 Verificación del Sistema

### **1. Verificar que Todo Está Funcionando**

```bash
# Verificar contenedores
docker-compose ps

# Verificar que la API responda
curl http://localhost:8080/v1/restaurants/home

# Verificar que la base de datos responda
docker-compose exec db pg_isready -U postgres
```

### **2. Verificar Logs**

```bash
# Logs de todos los servicios
docker-compose logs

# Logs solo de la API
docker-compose logs api-service

# Logs en tiempo real
docker-compose logs -f
```

---

## 🔄 Flujo de Trabajo Recomendado

### **Primera Vez (Setup Inicial)**

```bash
# 1. Generar claves JWT
openssl genpkey -algorithm RSA -out src/main/resources/jwtKeys/private_key.pem -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in src/main/resources/jwtKeys/private_key.pem -out src/main/resources/jwtKeys/public_key.pem

# 2. Construir y levantar todo
docker-compose up --build

# 3. Verificar que todo esté funcionando
curl http://localhost:8080/v1/restaurants/home
```

### **Desarrollo Diario**

```bash
# Levantar servicios
docker-compose up -d

# Ver logs
docker-compose logs -f api-service

# Detener servicios
docker-compose down
```

### **Actualizar Código**

```bash
# Reconstruir solo la API
docker-compose up --build api-service

# O reconstruir todo
docker-compose up --build
```

---

## 📦 Estructura de Archivos Docker

```
.
├── Dockerfile                    # Build de la aplicación
├── docker-compose.yml           # Producción - API + BD
├── docker-compose.dev.yml       # Desarrollo - Solo BD
├── .dockerignore               # Archivos a ignorar en build
└── src/main/resources/jwtKeys/ # Claves JWT (no versionar)
    ├── private_key.pem
    └── public_key.pem
```

---

## ⚙️ Configuración Avanzada

### **Cambiar Contraseña de Base de Datos**

1. Editar `docker-compose.yml`:
```yaml
POSTGRES_PASSWORD: nueva_password
SPRING_DATASOURCE_PASSWORD: nueva_password
```

2. Reconstruir:
```bash
docker-compose down -v  # ⚠️ Borra datos
docker-compose up --build
```

### **Agregar Variables de Entorno**

Editar `docker-compose.yml` en la sección `environment`:

```yaml
environment:
  # Nueva variable
  MI_VARIABLE: mi_valor
```

---

## 🚨 Notas Importantes

1. **Primera Ejecución:** La base de datos puede tardar 10-30 segundos en estar lista. El API espera automáticamente.

2. **Persistencia de Datos:** Los datos de PostgreSQL se guardan en el volumen `postgres_data`. Si ejecutas `docker-compose down -v`, se perderán.

3. **Claves JWT:** Si las claves no existen, la aplicación puede fallar. Asegúrate de generarlas antes de ejecutar.

4. **Puertos:** Si tienes PostgreSQL o otra aplicación corriendo en los puertos 5432 o 8080, cambia los puertos en `docker-compose.yml`.

5. **Logs:** Los logs se mantienen mientras el contenedor esté corriendo. Para ver logs históricos, usa `docker-compose logs`.

---

## 📚 Comandos de Referencia Rápida

```bash
# Iniciar todo
docker-compose up --build

# Iniciar en background
docker-compose up -d --build

# Detener todo
docker-compose down

# Ver logs
docker-compose logs -f

# Reiniciar API
docker-compose restart api-service

# Conectar a BD
docker-compose exec db psql -U postgres -d deliveryTrujillo

# Ver estado
docker-compose ps

# Limpiar todo (⚠️ BORRA DATOS)
docker-compose down -v
```

---

**Última actualización:** 2025-01-20  
**Versión:** 1.0

