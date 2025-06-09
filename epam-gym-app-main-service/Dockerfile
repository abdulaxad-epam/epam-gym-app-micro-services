FROM openjdk:17-jdk-slim-bullseye
LABEL authors="abdulaxad"
WORKDIR /app
COPY build/libs/epam-gym-application-boot-1.0-0.jar epam-gym-application-boot-1.0-0.jar
RUN apt-get update && apt-get install -y wget curl && rm -rf /var/lib/apt/lists/*
COPY src/main/resources/database_initializer/data.sql data.sql
EXPOSE 8080
CMD ["java", "-jar", "epam-gym-application-boot-1.0-0.jar"]