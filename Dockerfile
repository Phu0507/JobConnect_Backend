# Image Java 17 nhẹ
FROM eclipse-temurin:17-jdk-alpine

# Thư mục trong container
WORKDIR /app

# Copy file jar đã build sẵn
COPY build/libs/JobConnect_Backend-0.0.1-SNAPSHOT.jar app.jar

# Port app Spring Boot lắng nghe
EXPOSE 8080

# Profile (nếu có application-prod.properties)
ENV SPRING_PROFILES_ACTIVE=prod

# Lệnh chạy app
ENTRYPOINT ["java", "-jar", "app.jar"]
