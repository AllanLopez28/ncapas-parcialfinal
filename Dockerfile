# Etapa 1: Build con Maven + JDK 21
FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /app

# 1. Copia el pom y las fuentes
COPY pom.xml .
COPY src ./src

# 2. Compila el jar (sin tests para acelerar)
RUN mvn clean package -DskipTests

# Etapa 2: Imagen de runtime solo con JRE
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copia el jar generado en el build
COPY --from=builder /app/target/parcial-final-n-capas-0.0.1-SNAPSHOT.jar app.jar

# Exponer puerto
EXPOSE 8080

# Comando de arranque
ENTRYPOINT ["java","-jar","/app/app.jar"]
