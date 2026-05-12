# Usamos una imagen ligera de Java 17 como base
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiamos el JAR generado por Maven al contenedor
COPY target/auth-service.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]