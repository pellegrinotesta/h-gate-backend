# Stage 1: Build
FROM --platform=linux/amd64 eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Run
FROM --platform=linux/amd64 eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY --from=build /app/target/H-Gate-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]