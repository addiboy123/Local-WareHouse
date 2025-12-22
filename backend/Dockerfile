# -------- BUILD STAGE --------
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom first (for dependency caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests


# -------- RUNTIME STAGE --------
FROM eclipse-temurin:21-jre

WORKDIR /app

# Create storage directory
RUN mkdir -p /data/storage

# Copy built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose Spring Boot port
EXPOSE 8080

# Optional but recommended
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]
