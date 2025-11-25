# ============================
# 🌟 STAGE 1 — BUILD DO JAR
# ============================
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copia arquivos do projeto
COPY pom.xml .
COPY src ./src

# Baixa dependências e compila
RUN mvn -U -e -B -DskipTests clean package

# ============================
# 🌟 STAGE 2 — RUNTIME
# ============================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copia o jar compilado do stage anterior
COPY --from=build /app/target/*.jar app.jar

# Render precisa expor 8080
EXPOSE 8080

# Comando final
ENTRYPOINT ["java", "-jar", "app.jar"]
