# ============================
# 1. Imagem builder (Maven + JDK 21)
# ============================
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copia o arquivo pom.xml e baixa dependências primeiro (cache)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o restante do projeto
COPY src ./src

# Build final (gera JAR)
RUN mvn clean package -DskipTests


# ============================
# 2. Imagem final (somente JDK 21)
# ============================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copia o JAR criado
COPY --from=build /app/target/*.jar app.jar

# Porta padrão do Spring Boot
EXPOSE 8080

# Comando para iniciar
ENTRYPOINT ["java", "-jar", "app.jar"]
