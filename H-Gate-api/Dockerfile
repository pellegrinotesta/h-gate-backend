# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Run (solo JRE, utente non root)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S hgate && adduser -S hgate -G hgate \
    && mkdir -p /app/uploads/allegati /app/logs \
    && chown -R hgate:hgate /app

COPY --from=build --chown=hgate:hgate /app/target/H-Gate-0.0.1-SNAPSHOT.jar app.jar

USER hgate
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
