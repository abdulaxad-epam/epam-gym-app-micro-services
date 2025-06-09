FROM openjdk:17-jdk-slim-bullseye
LABEL authors="abdulaxad"
WORKDIR /app
COPY build/libs/epam-gym-app-trainer-workload-service-0.0.1.jar epam-gym-app-trainer-workload-service-0.0.1.jar
RUN apt-get update && apt-get install -y wget curl && rm -rf /var/lib/apt/lists/*
COPY src/main/resources/database_initializer/data.sql data.sql
EXPOSE 8081
CMD ["java", "-jar", "epam-gym-app-trainer-workload-service-0.0.1.jar"]
