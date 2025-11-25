# Etapa de build
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN ./mvnw -q -e -DskipTests package

# Etapa de execução
FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
