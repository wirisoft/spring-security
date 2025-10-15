# 🐳 Guía de Docker - Delivery Trujillo API

## 📋 Tabla de Contenidos
- [Requisitos Previos](#requisitos-previos)
- [Arquitectura de Contenedores](#arquitectura-de-contenedores)
- [Comandos Básicos de Docker Compose](#comandos-básicos-de-docker-compose)
- [Gestión de Contenedores](#gestión-de-contenedores)
- [Ver Logs](#ver-logs)
- [Acceder a los Contenedores](#acceder-a-los-contenedores)
- [Consultas a la Base de Datos](#consultas-a-la-base-de-datos)
- [Troubleshooting](#troubleshooting)
- [Scripts de Automatización](#scripts-de-automatización)

---

## 📦 Requisitos Previos

### Software necesario:
- **Docker Desktop** (Windows/Mac) o **Docker Engine** (Linux)
- **Docker Compose** (incluido en Docker Desktop)

### Verificar instalación:
```bash
# Verificar versión de Docker
docker --version

# Verificar versión de Docker Compose
docker-compose --version

# Verificar que Docker está corriendo
docker ps
```

**Versiones recomendadas:**
- Docker: 20.10.0 o superior
- Docker Compose: 2.0.0 o superior

---

## 🏗️ Arquitectura de Contenedores

```
┌─────────────────────────────────────────────────────────────┐
│                    Docker Network                           │
│                 app-trujillo-services_default               │
│                                                             │
│  ┌──────────────────────┐      ┌──────────────────────┐   │
│  │  delivery_api_service │      │  postgres_delivery_db │   │
│  │                      │      │                      │   │
│  │  Spring Boot API     │◄─────┤  PostgreSQL 14       │   │
│  │  Puerto: 8080        │      │  Puerto: 5432        │   │
│  │  Java 21             │      │                      │   │
│  └──────────────────────┘      └──────────────────────┘   │
│           │                              │                 │
└───────────┼──────────────────────────────┼─────────────────┘
            │                              │
            ▼                              ▼
     Host: 8080                     Host: 5432
```

### Servicios:

#### 1. **postgres_delivery_db**
- **Imagen**: `postgres:14-alpine`
- **Puerto**: `5432:5432`
- **Base de datos**: `deliveryTrujillo`
- **Usuario**: `postgres`
- **Volumen**: `postgres_data` (persistencia de datos)

#### 2. **delivery_api_service**
- **Imagen**: Construcción custom (Dockerfile multi-stage)
- **Puerto**: `8080:8080`
- **Depende de**: `postgres_delivery_db`
- **Variables de entorno**: Configuración de conexión a DB

---

## 🚀 Comandos Básicos de Docker Compose

### 1️⃣ Levantar los servicios (primera vez)

```bash
# Construir imágenes y levantar contenedores en segundo plano
docker-compose up -d --build
```

**Salida esperada:**
```
[+] Building 72.8s
[+] Running 4/4
 ✔ Network app-trujillo-services_default  Created
 ✔ Container postgres_delivery_db         Started
 ✔ Container delivery_api_service         Started
```

**Explicación de flags:**
- `-d`: Detached mode (segundo plano)
- `--build`: Forzar reconstrucción de imágenes

---

### 2️⃣ Ver el estado de los contenedores

```bash
# Ver contenedores en ejecución
docker ps

# Ver todos los contenedores (incluyendo detenidos)
docker ps -a
```

**Salida esperada:**
```
CONTAINER ID   IMAGE                               STATUS         PORTS                    NAMES
5428da9f7bde   app-trujillo-services-api-service   Up 2 minutes   0.0.0.0:8080->8080/tcp   delivery_api_service
ff305ea07cbb   postgres:14-alpine                  Up 2 minutes   0.0.0.0:5432->5432/tcp   postgres_delivery_db
```

---

### 3️⃣ Detener los servicios

```bash
# Detener contenedores (mantiene volúmenes y datos)
docker-compose stop

# O detener un servicio específico
docker-compose stop api-service
docker-compose stop db
```

---

### 4️⃣ Iniciar servicios detenidos

```bash
# Iniciar todos los servicios
docker-compose start

# O iniciar un servicio específico
docker-compose start api-service
docker-compose start db
```

---

### 5️⃣ Reiniciar servicios

```bash
# Reiniciar todos los servicios
docker-compose restart

# Reiniciar solo la API
docker-compose restart api-service

# Reiniciar solo la base de datos
docker-compose restart db
```

---

### 6️⃣ Bajar los servicios (eliminar contenedores)

```bash
# Detener y eliminar contenedores (mantiene volúmenes)
docker-compose down

# Detener, eliminar contenedores Y volúmenes (⚠️ BORRA LOS DATOS)
docker-compose down -v

# Detener, eliminar contenedores, volúmenes e imágenes
docker-compose down -v --rmi all
```

**⚠️ ADVERTENCIA:** `docker-compose down -v` eliminará todos los datos de la base de datos.

---

### 7️⃣ Reconstruir y reiniciar todo

```bash
# Proceso completo: bajar, reconstruir y levantar
docker-compose down
docker-compose up -d --build
```

**Usar cuando:**
- Cambias código fuente
- Modificas dependencias (build.gradle)
- Actualizas configuración

---

## 🔍 Gestión de Contenedores

### Ver información detallada

```bash
# Inspeccionar un contenedor
docker inspect delivery_api_service

# Ver uso de recursos (CPU, RAM)
docker stats

# Ver uso de recursos de un contenedor específico
docker stats delivery_api_service
```

### Verificar salud de los servicios

```bash
# Ver logs para verificar que todo está OK
docker-compose logs

# Verificar que la API responde
curl http://localhost:8080/v1/auth/register
```

En PowerShell:
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/v1/auth/register" -Method GET
```

---

## 📊 Ver Logs

### Logs de Spring Boot (API)

```bash
# Ver logs de la API en tiempo real
docker logs -f delivery_api_service

# Ver últimas 100 líneas
docker logs --tail 100 delivery_api_service

# Ver logs desde hace 10 minutos
docker logs --since 10m delivery_api_service

# Ver logs con timestamps
docker logs -t delivery_api_service
```

**Logs importantes a buscar:**
```
✅ Started AppTrujilloServicesApplication in X seconds
✅ Tomcat started on port(s): 8080
✅ Hikari Connection Pool: HikariPool-1 - Start completed
❌ Error: Connection refused
❌ JPA Error: ...
```

---

### Logs de PostgreSQL (Base de Datos)

```bash
# Ver logs de PostgreSQL en tiempo real
docker logs -f postgres_delivery_db

# Ver últimas 50 líneas
docker logs --tail 50 postgres_delivery_db
```

**Logs importantes a buscar:**
```
✅ database system is ready to accept connections
✅ PostgreSQL init process complete
❌ FATAL: database "deliveryTrujillo" does not exist
❌ FATAL: password authentication failed
```

---

### Logs combinados de todos los servicios

```bash
# Ver logs de todos los servicios
docker-compose logs

# Seguir logs de todos los servicios en tiempo real
docker-compose logs -f

# Logs de un servicio específico
docker-compose logs -f api-service
docker-compose logs -f db

# Últimas 50 líneas de todos los servicios
docker-compose logs --tail 50
```

---

## 🔐 Acceder a los Contenedores

### Acceder al contenedor de la API (Spring Boot)

```bash
# Abrir shell en el contenedor de la API
docker exec -it delivery_api_service sh

# Una vez dentro del contenedor:
pwd                    # Ver directorio actual (/app)
ls -la                 # Ver archivos (app.jar)
cat /etc/os-release    # Ver info del sistema (Alpine Linux)
java -version          # Ver versión de Java
exit                   # Salir del contenedor
```

**Comandos útiles dentro del contenedor:**
```bash
# Ver procesos corriendo
ps aux

# Ver uso de memoria
free -h

# Ver variables de entorno
env | grep SPRING

# Ver el contenido del JAR
jar tf app.jar | head -20
```

---

### Acceder al contenedor de PostgreSQL

```bash
# Abrir shell de PostgreSQL (psql)
docker exec -it postgres_delivery_db psql -U postgres -d deliveryTrujillo

# Alternativa: shell bash del contenedor
docker exec -it postgres_delivery_db sh
```

---

## 🗄️ Consultas a la Base de Datos

### Método 1: Desde el host (recomendado)

```bash
# Conectarse a PostgreSQL desde fuera del contenedor
docker exec -it postgres_delivery_db psql -U postgres -d deliveryTrujillo
```

### Método 2: Desde dentro del contenedor

```bash
# Entrar al contenedor
docker exec -it postgres_delivery_db sh

# Conectarse a PostgreSQL
psql -U postgres -d deliveryTrujillo
```

---

### Comandos PostgreSQL (psql)

Una vez conectado con `psql`:

#### Comandos básicos:

```sql
-- Ver todas las bases de datos
\l

-- Conectarse a una base de datos
\c deliveryTrujillo

-- Ver todas las tablas
\dt

-- Describir estructura de una tabla
\d users

-- Ver información detallada de la tabla users
\d+ users

-- Ver índices
\di

-- Ver funciones
\df

-- Salir de psql
\q
```

---

#### Consultas a la tabla users:

```sql
-- Ver todos los usuarios
SELECT * FROM users;

-- Ver usuarios con formato bonito
SELECT id, email, first_name, last_name, phone_number, created_at 
FROM users;

-- Contar usuarios
SELECT COUNT(*) FROM users;

-- Ver usuarios creados hoy
SELECT * FROM users 
WHERE DATE(created_at) = CURRENT_DATE;

-- Ver usuarios ordenados por fecha de creación
SELECT id, email, first_name, last_name, created_at 
FROM users 
ORDER BY created_at DESC;

-- Buscar usuario por email
SELECT * FROM users 
WHERE email = 'docker@example.com';

-- Ver solo IDs y emails
SELECT id, email FROM users;

-- Ver últimos 5 usuarios registrados
SELECT id, email, first_name, last_name, created_at 
FROM users 
ORDER BY created_at DESC 
LIMIT 5;

-- Ver usuarios con nombre que contiene 'Docker'
SELECT * FROM users 
WHERE first_name LIKE '%Docker%';

-- Ver estadísticas por día
SELECT DATE(created_at) as fecha, COUNT(*) as total_usuarios
FROM users
GROUP BY DATE(created_at)
ORDER BY fecha DESC;
```

---

#### Consultas administrativas:

```sql
-- Ver tamaño de la base de datos
SELECT pg_size_pretty(pg_database_size('deliveryTrujillo'));

-- Ver tamaño de la tabla users
SELECT pg_size_pretty(pg_total_relation_size('users'));

-- Ver información de conexiones activas
SELECT * FROM pg_stat_activity 
WHERE datname = 'deliveryTrujillo';

-- Ver versión de PostgreSQL
SELECT version();

-- Ver hora del servidor
SELECT NOW();

-- Ver esquema de la base de datos
\dn

-- Ver todas las columnas de una tabla
SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_name = 'users';
```

---

#### Operaciones de mantenimiento:

```sql
-- Limpiar y optimizar tabla
VACUUM ANALYZE users;

-- Ver estadísticas de la tabla
SELECT * FROM pg_stat_user_tables 
WHERE relname = 'users';

-- Reindexar tabla
REINDEX TABLE users;
```

---

### Script completo de consultas

```bash
# Crear un script SQL
cat > /tmp/queries.sql << 'EOF'
-- Resumen de usuarios
SELECT 
    COUNT(*) as total_usuarios,
    COUNT(DISTINCT email) as emails_unicos,
    MIN(created_at) as primer_registro,
    MAX(created_at) as ultimo_registro
FROM users;

-- Últimos 10 usuarios
SELECT id, email, first_name, last_name, created_at 
FROM users 
ORDER BY created_at DESC 
LIMIT 10;
EOF

# Ejecutar el script
docker exec -i postgres_delivery_db psql -U postgres -d deliveryTrujillo < /tmp/queries.sql
```

---

## 🔧 Troubleshooting

### Problema: El contenedor de la API no inicia

```bash
# Ver logs detallados
docker logs delivery_api_service

# Verificar que la DB está corriendo
docker ps | grep postgres

# Verificar conectividad entre contenedores
docker exec delivery_api_service ping db
```

**Soluciones comunes:**
- Esperar a que PostgreSQL termine de inicializar (15-30 segundos)
- Verificar variables de entorno con `docker inspect delivery_api_service`
- Reconstruir: `docker-compose down && docker-compose up -d --build`

---

### Problema: No puedo conectarme a la base de datos

```bash
# Verificar que el puerto 5432 está expuesto
docker port postgres_delivery_db

# Probar conexión desde el host
docker exec postgres_delivery_db psql -U postgres -d deliveryTrujillo -c "SELECT 1;"

# Verificar credenciales
docker exec postgres_delivery_db env | grep POSTGRES
```

---

### Problema: Los datos se pierden al reiniciar

```bash
# Verificar que el volumen existe
docker volume ls | grep postgres

# Inspeccionar el volumen
docker volume inspect app-trujillo-services_postgres_data

# Si necesitas hacer backup
docker exec postgres_delivery_db pg_dump -U postgres deliveryTrujillo > backup.sql

# Restaurar backup
docker exec -i postgres_delivery_db psql -U postgres -d deliveryTrujillo < backup.sql
```

---

### Problema: Puerto 8080 o 5432 ya en uso

```bash
# Ver qué está usando el puerto 8080
# Windows:
netstat -ano | findstr :8080

# Linux/Mac:
lsof -i :8080

# Detener el proceso o cambiar el puerto en docker-compose.yml
# Ejemplo: cambiar a 8081:8080
```

---

### Problema: Error "No space left on device"

```bash
# Limpiar imágenes no usadas
docker system prune -a

# Limpiar volúmenes no usados
docker volume prune

# Ver uso de espacio
docker system df
```

---

## 🤖 Scripts de Automatización

### Script para Windows PowerShell

```powershell
# Guardar como: docker-manager.ps1

function Start-DeliveryAPI {
    Write-Host "🚀 Iniciando servicios..." -ForegroundColor Cyan
    docker-compose up -d --build
    Start-Sleep -Seconds 5
    docker ps
}

function Stop-DeliveryAPI {
    Write-Host "🛑 Deteniendo servicios..." -ForegroundColor Yellow
    docker-compose down
}

function Restart-DeliveryAPI {
    Write-Host "🔄 Reiniciando servicios..." -ForegroundColor Yellow
    docker-compose restart
}

function Show-Logs {
    param([string]$Service = "")
    
    if ($Service) {
        docker-compose logs -f $Service
    } else {
        docker-compose logs -f
    }
}

function Connect-Database {
    Write-Host "🗄️  Conectando a PostgreSQL..." -ForegroundColor Green
    docker exec -it postgres_delivery_db psql -U postgres -d deliveryTrujillo
}

function Show-Users {
    Write-Host "👥 Usuarios registrados:" -ForegroundColor Green
    docker exec postgres_delivery_db psql -U postgres -d deliveryTrujillo -c "SELECT id, email, first_name, last_name, created_at FROM users ORDER BY created_at DESC;"
}

# Menú interactivo
function Show-Menu {
    Write-Host "`n========================================" -ForegroundColor Cyan
    Write-Host "   DELIVERY TRUJILLO - DOCKER MANAGER" -ForegroundColor Cyan
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "1. Iniciar servicios"
    Write-Host "2. Detener servicios"
    Write-Host "3. Reiniciar servicios"
    Write-Host "4. Ver logs (API)"
    Write-Host "5. Ver logs (Base de datos)"
    Write-Host "6. Conectar a PostgreSQL"
    Write-Host "7. Ver usuarios registrados"
    Write-Host "8. Ver estado de contenedores"
    Write-Host "9. Salir"
    Write-Host "========================================" -ForegroundColor Cyan
}

# Uso:
# . .\docker-manager.ps1
# Show-Menu
```

---

### Script para Linux/Mac (Bash)

```bash
#!/bin/bash
# Guardar como: docker-manager.sh
# Dar permisos: chmod +x docker-manager.sh

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

function start_services() {
    echo -e "${CYAN}🚀 Iniciando servicios...${NC}"
    docker-compose up -d --build
    sleep 5
    docker ps
}

function stop_services() {
    echo -e "${YELLOW}🛑 Deteniendo servicios...${NC}"
    docker-compose down
}

function restart_services() {
    echo -e "${YELLOW}🔄 Reiniciando servicios...${NC}"
    docker-compose restart
}

function show_logs() {
    echo -e "${GREEN}📊 Mostrando logs...${NC}"
    docker-compose logs -f "$1"
}

function connect_db() {
    echo -e "${GREEN}🗄️  Conectando a PostgreSQL...${NC}"
    docker exec -it postgres_delivery_db psql -U postgres -d deliveryTrujillo
}

function show_users() {
    echo -e "${GREEN}👥 Usuarios registrados:${NC}"
    docker exec postgres_delivery_db psql -U postgres -d deliveryTrujillo -c "SELECT id, email, first_name, last_name, created_at FROM users ORDER BY created_at DESC;"
}

function show_menu() {
    echo -e "${CYAN}"
    echo "========================================"
    echo "   DELIVERY TRUJILLO - DOCKER MANAGER"
    echo "========================================"
    echo -e "${NC}"
    echo "1. Iniciar servicios"
    echo "2. Detener servicios"
    echo "3. Reiniciar servicios"
    echo "4. Ver logs (API)"
    echo "5. Ver logs (Base de datos)"
    echo "6. Conectar a PostgreSQL"
    echo "7. Ver usuarios registrados"
    echo "8. Ver estado de contenedores"
    echo "9. Salir"
    echo "========================================"
    read -p "Selecciona una opción: " choice
    
    case $choice in
        1) start_services ;;
        2) stop_services ;;
        3) restart_services ;;
        4) show_logs api-service ;;
        5) show_logs db ;;
        6) connect_db ;;
        7) show_users ;;
        8) docker ps ;;
        9) exit 0 ;;
        *) echo "Opción inválida" ;;
    esac
}

# Loop del menú
while true; do
    show_menu
    echo ""
    read -p "Presiona Enter para continuar..."
done
```

---

## 📚 Comandos de Referencia Rápida

```bash
# INICIAR
docker-compose up -d --build          # Primera vez / con cambios
docker-compose up -d                  # Sin rebuild
docker-compose start                  # Servicios detenidos

# DETENER
docker-compose stop                   # Detener (mantiene contenedores)
docker-compose down                   # Eliminar contenedores
docker-compose down -v                # Eliminar contenedores + datos

# LOGS
docker logs -f delivery_api_service   # Logs de la API
docker logs -f postgres_delivery_db   # Logs de PostgreSQL
docker-compose logs -f                # Todos los logs

# ACCEDER
docker exec -it delivery_api_service sh                              # Shell API
docker exec -it postgres_delivery_db psql -U postgres -d deliveryTrujillo  # PostgreSQL

# ESTADO
docker ps                             # Contenedores corriendo
docker-compose ps                     # Estado de servicios
docker stats                          # Uso de recursos

# LIMPIEZA
docker system prune -a                # Limpiar todo
docker volume prune                   # Limpiar volúmenes
```

---

## 🎯 Flujo de Trabajo Recomendado

### Para desarrollo diario:

```bash
# 1. Iniciar servicios por la mañana
docker-compose up -d

# 2. Trabajar en tu código...

# 3. Si cambias código, reconstruir solo la API
docker-compose up -d --build api-service

# 4. Ver logs si hay problemas
docker logs -f delivery_api_service

# 5. Al final del día, detener (opcional)
docker-compose stop
```

### Para deploy/pruebas completas:

```bash
# 1. Limpiar todo
docker-compose down -v

# 2. Reconstruir desde cero
docker-compose up -d --build

# 3. Verificar que todo está OK
docker ps
docker logs delivery_api_service | grep "Started"

# 4. Probar la API
curl http://localhost:8080/v1/auth/register
```

---

**📝 Nota:** Guarda este archivo como `DOCKER-GUIDE.md` en la raíz de tu proyecto para referencia rápida.

**Desarrollado con ❤️ para Delivery Trujillo Services**