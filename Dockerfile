# Usa una imagen base de Java con JDK 17 (ajusta si usas otra versión)
FROM eclipse-temurin:17-jdk-alpine

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo JAR al contenedor
COPY target/*.jar app.jar

# Expone el puerto (Render detecta automáticamente)
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
