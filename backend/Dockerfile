# Stage 1: Build the JAR
FROM --platform=$BUILDPLATFORM maven:3.9.12-eclipse-temurin-25-alpine AS build
WORKDIR /build
COPY pom.xml ./
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -Pprod -DskipTests -B

# Stage 2: Run the application
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
