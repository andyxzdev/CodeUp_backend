# Etapa 1 — Build
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copia tudo
COPY . .

# Dá permissão ao mvnw (necessário no Linux)
RUN chmod +x mvnw

# Build do projeto
RUN ./mvnw -q -e -DskipTests package

# Etapa 2 — Runtime
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copia o JAR criado
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
