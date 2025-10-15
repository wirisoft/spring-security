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

# Definimos el nombre del archivo JAR, basado en su directorio (app-trujillo-services)
ARG JAR_FILE=app-trujillo-services-*.jar

# Establece el directorio de trabajo
WORKDIR /app

# Copia el JAR generado desde la etapa de construcción (builder)
COPY --from=builder /app/build/libs/$JAR_FILE app.jar

# Expone el puerto por defecto de Spring Boot
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
