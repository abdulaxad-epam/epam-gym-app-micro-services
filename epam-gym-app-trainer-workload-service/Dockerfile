FROM gradle:jdk17 AS build

LABEL authors="abdulaxad"

WORKDIR /app

COPY gradlew .
COPY gradle/wrapper/gradle-wrapper.jar gradle/wrapper/

COPY build.gradle .
COPY settings.gradle .

COPY src ./src

RUN gradle bootJar -x test

# Stage 2:
FROM openjdk:17-jdk-slim-bullseye AS run

LABEL authors="abdulaxad"

WORKDIR /app

COPY --from=build /app/build/libs/epam-gym-app-trainer-workload-service-0.0.1.jar epam-gym-app-trainer-workload-service-0.0.1.jar

COPY src/main/resources/database_initializer/data.sql data.sql

EXPOSE 8081

CMD ["java", "-jar", "epam-gym-app-trainer-workload-service-0.0.1.jar"]
