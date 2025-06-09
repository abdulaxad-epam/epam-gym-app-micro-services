FROM openjdk:17-jdk-slim-bullseye
LABEL authors="abdulaxad"
WORKDIR /app
RUN apt-get update && apt-get install -y wget curl && rm -rf /var/lib/apt/lists/*
COPY build/libs/epam-gym-app-eureka-server-1.0-0.jar epam-gym-app-eureka-server-1.0-0.jar
EXPOSE 8761
CMD ["java", "-jar", "epam-gym-app-eureka-server-1.0-0.jar"]
