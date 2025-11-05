# --------------------------------------------------------------------
# STAGE 1: BUILD (Compilación de la aplicación Spring Boot con Java 21)
# --------------------------------------------------------------------
# Usamos la imagen JDK de Eclipse Temurin basada en Jammy (Ubuntu 22.04) para la construcción (Java 21)
FROM eclipse-temurin:21-jdk-jammy AS builder

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia los archivos de configuración de Gradle para aprovechar el caché de capas
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle .

# Copia el código fuente
COPY src src

# Concede permisos de ejecución al wrapper de Gradle
RUN chmod +x gradlew

# Construye el JAR ejecutable. Usamos '-x test' para omitir las pruebas durante la construcción.
RUN ./gradlew bootJar -x test

# --------------------------------------------------------------------
# STAGE 2: RUN (Ejecución de la aplicación en un entorno JRE ligero)
# --------------------------------------------------------------------
# Usamos una imagen JRE Alpine (la más ligera) para el entorno de ejecución (Java 21)
FROM eclipse-temurin:21-jre-alpine

# Instalar dependencias necesarias para PostgreSQL
RUN apk add --no-cache curl

# Definimos el nombre del archivo JAR, basado en su directorio (app-trujillo-services)
ARG JAR_FILE=app-trujillo-services-*.jar

# Establece el directorio de trabajo
WORKDIR /app

# Copia el JAR generado desde la etapa de construcción (builder)
COPY --from=builder /app/build/libs/$JAR_FILE app.jar

# Crear directorio para claves JWT
RUN mkdir -p /app/jwtKeys

# Instalar base64 para decodificar claves JWT desde variables de entorno (para Render)
RUN apk add --no-cache base64

# Copia las claves JWT desde el builder stage si existen (para desarrollo local)
# Si no existen, se crearán desde variables de entorno en producción (Render)
RUN --mount=from=builder,source=/app/src/main/resources/jwtKeys,target=/tmp/jwtKeys,rw \
    if [ -d /tmp/jwtKeys ] && [ "$(ls -A /tmp/jwtKeys 2>/dev/null)" ]; then \
        cp -r /tmp/jwtKeys/* /app/jwtKeys/ 2>/dev/null || true; \
    fi

# Script para crear claves JWT desde variables de entorno si existen (para Render)
RUN echo '#!/bin/sh' > /app/create-jwt-keys.sh && \
    echo 'if [ -n "$JWT_PRIVATE_KEY_B64" ] && [ -n "$JWT_PUBLIC_KEY_B64" ]; then' >> /app/create-jwt-keys.sh && \
    echo '  echo "$JWT_PRIVATE_KEY_B64" | base64 -d > /app/jwtKeys/private_key.pem' >> /app/create-jwt-keys.sh && \
    echo '  echo "$JWT_PUBLIC_KEY_B64" | base64 -d > /app/jwtKeys/public_key.pem' >> /app/create-jwt-keys.sh && \
    echo '  chmod 600 /app/jwtKeys/private_key.pem' >> /app/create-jwt-keys.sh && \
    echo '  chmod 644 /app/jwtKeys/public_key.pem' >> /app/create-jwt-keys.sh && \
    echo 'fi' >> /app/create-jwt-keys.sh && \
    chmod +x /app/create-jwt-keys.sh

# Expone el puerto por defecto de Spring Boot
EXPOSE 8080

# Health check para verificar que la aplicación esté funcionando
# Usa curl para verificar que el puerto responde (no requiere actuator)
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health 2>/dev/null || curl -f http://localhost:8080/v1/restaurants/home || exit 1

# Comando para ejecutar la aplicación
# Primero crea las claves JWT desde variables de entorno si existen (Render)
# Luego ejecuta la aplicación con las rutas de las claves JWT
# Optimizaciones para plan gratuito: memoria limitada (512MB)
ENTRYPOINT ["/bin/sh", "-c", "/app/create-jwt-keys.sh && java -Xmx384m -Xms128m -DjwtKeys.privateKeyPath=/app/jwtKeys/private_key.pem -DjwtKeys.publicKeyPath=/app/jwtKeys/public_key.pem -jar app.jar"]
