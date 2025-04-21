FROM eclipse-temurin:21-jdk-alpine as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Garantir que o script mvnw tenha permissão de execução
RUN chmod +x ./mvnw

# Compilar e empacotar a aplicação
RUN ./mvnw install -DskipTests

# Estágio de execução
FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp

# Instalar curl para healthcheck
RUN apk add --no-cache curl

# Variáveis de ambiente que podem ser sobrescritas no docker-compose
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/marketplacedb
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=postgres
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update
ENV SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect
ENV SPRING_PROFILES_ACTIVE=prod

# Copiar o JAR do estágio de build
COPY --from=build /workspace/app/target/*.jar app.jar

# Expor a porta da aplicação
EXPOSE 8080

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "/app.jar"]