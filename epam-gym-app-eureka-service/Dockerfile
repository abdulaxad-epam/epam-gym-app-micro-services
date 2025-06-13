FROM gradle:jdk17 AS build

LABEL authors="abdulaxad"

WORKDIR /app

COPY gradlew .
COPY gradle/wrapper/gradle-wrapper.jar gradle/wrapper/

COPY build.gradle .
COPY settings.gradle .

COPY src ./src

RUN gradle bootJar

# Stage 2:
FROM openjdk:17-jdk-slim-bullseye

LABEL authors="abdulaxad"

WORKDIR /app

COPY --from=build /app/build/libs/epam-gym-app-eureka-server-1.0-0.jar epam-gym-app-eureka-server-1.0-0.jar

EXPOSE 8761

CMD ["java", "-jar", "epam-gym-app-eureka-server-1.0-0.jar"]
