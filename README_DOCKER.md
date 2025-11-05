# 🐳 Docker - Delivery Trujillo Services

## 🚀 Inicio Rápido

### **Build y Levantar Todo (Recomendado)**

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

### **Build en Background**

```bash
# Construir y levantar en segundo plano
docker-compose up --build -d

# Ver logs
docker-compose logs -f api-service
```

---

## 📝 Comandos Útiles

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

# Conectar a la base de datos
docker-compose exec db psql -U postgres -d deliveryTrujillo
```

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

## 🔧 Verificación

### **Verificar que Todo Está Funcionando**

```bash
# Verificar contenedores
docker-compose ps

# Verificar que la API responda
curl http://localhost:8080/v1/restaurants/home

# Verificar que la base de datos responda
docker-compose exec db pg_isready -U postgres
```

---

## 📚 Documentación Completa

Para más detalles, consulta:
- **`GUIA_DOCKER.md`** - Guía completa de Docker
- **`docker_guide.md`** - Guía detallada de comandos

---

## 🚨 Notas Importantes

1. **Primera Ejecución:** La base de datos puede tardar 10-30 segundos en estar lista. El API espera automáticamente.

2. **Persistencia de Datos:** Los datos de PostgreSQL se guardan en el volumen `postgres_data`. Si ejecutas `docker-compose down -v`, se perderán.

3. **Claves JWT:** Si las claves no existen, la aplicación puede fallar. Asegúrate de generarlas antes de ejecutar.

4. **Puertos:** Si tienes PostgreSQL o otra aplicación corriendo en los puertos 5432 o 8080, cambia los puertos en `docker-compose.yml`.

---

**Última actualización:** 2025-01-20

