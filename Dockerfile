# Etapa 1: Construcción
FROM maven:3.9.3-eclipse-temurin-17 AS builder

WORKDIR /app

# Copiar todo el proyecto
COPY . .

# Compilar el proyecto y generar el jar (salida en target/)
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copiar el JAR generado en la etapa anterior
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

# Ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]
