# Epam Gym CRM

A comprehensive Spring Boot application for managing trainers, trainees, and training sessions.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [API Documentation](#api-documentation)
- [Setup Instructions](#setup-instructions)
- [Usage Examples](#usage-examples)
- [Authentication](#authentication)
- [Controllers](#controllers)

## Overview

This system provides a platform for managing the relationships between trainers and trainees, scheduling training sessions, and tracking training progress. It includes user registration, authentication, profile management, and training session coordination.

## Features

- User registration (trainers and trainees)
- Authentication and password management
- Profile management for trainers and trainees
- Training session scheduling and tracking
- Trainer-trainee relationship management
- Training type categorization
- Comprehensive API for all operations

## Technology Stack

- **Java Spring Boot**: Backend framework
- **Spring Data JPA**: Database operations
- **Swagger/OpenAPI**: API documentation
- **Lombok**: Reducing boilerplate code
- **Jakarta Validation**: Input validation

## API Documentation

API documentation is available via Swagger UI. After starting the application, access the documentation at:
```
http://localhost:8080/swagger-ui/index.html
```

## Setup Instructions

### Installation

1. Clone the repository:
```bash
git clone --branch epam-gym-app-main-service https://github.com/abdulaxad-epam/epam-gym-app.git 
cd epam-gym-app
```
2. Build the application:

```bash
./gradlew clean build
```

3. Run the application:
```bash
java -jar build/libs/epam-gym-application-boot-1.0-0.jar
```

## Usage Examples

### Register a Trainer
```bash
curl -X POST http://localhost:8080/api/v1/auth/register/trainer \
  -H "Content-Type: application/json" \
  -d '{
    "firstname": "John",
    "lastname": "Smith",
    "specialization": "Strength Training"
  }'
```

### Register a Trainee
```bash
curl -X POST http://localhost:8080/api/v1/auth/register/trainee \
  -H "Content-Type: application/json" \
  -d '{
    "firstname": "Jane",
    "lastname": "Doe",
    "dateOfBirth": "1990-01-01",
    "address": "123 Fitness Street"
  }'
```

### Create a Training Session
```bash
curl -X POST http://localhost:8080/api/v1/trainings \
  -H "Content-Type: application/json" \
  -H "username: trainer_username" \
  -H "password: password" \
  -d '{
    "traineeUsername": "trainee_username",
    "trainerUsername": "trainer_username",
    "trainingName": "Morning Workout",
    "trainingDate": "2025-07-15",
    "trainingDuration": 60,
    "trainingTypeId": 1
  }'
```


The `TrainerWorkloadService` provides methods to interact with the external trainer workload system.

* `actionOnADD(Training training)`: Sends a request to add a new training workload for a trainer.
* `actionOnDELETE(Training training)`: Sends a request to delete an existing training workload for a trainer.

These methods internally handle:
* Mapping `Training` entities to `TrainerWorkloadRequestDTO`.
* Generating JWT tokens.
* Making HTTP POST requests to the configured workload service endpoint.
* Applying Circuit Breaker logic for resilience.


## Authentication

Authentication is required for most endpoints. Pass credentials via headers:
```bash
curl -X GET http://localhost:8080/api/v1/trainees/username \
  -H "password: user_password"
```

Password changes can be performed using the `/api/v1/auth/changePassword` endpoint.

## Controllers

### Authentication Controller
Handles user registration, authentication, and password management.
- Register trainer: `POST /api/v1/auth/register/trainer`
- Register trainee: `POST /api/v1/auth/register/trainee`
- Authenticate: `POST /api/v1/auth/authenticate`
- Change password: `PUT /api/v1/auth/changePassword`

### Trainee Controller
Manages trainee profiles and related operations.
- Get trainee details: `GET /api/v1/trainees/{username}`
- Get trainee's trainings: `GET /api/v1/trainees/{username}/trainings`
- Update trainee profile: `PUT /api/v1/trainees/update/{username}`
- Delete trainee: `DELETE /api/v1/trainees/delete/{username}`
- Update trainee status: `PATCH /api/v1/trainees/status/{username}`

### Trainer Controller
Manages trainer profiles and related operations.
- Get trainer details: `GET /api/v1/trainers/{username}`
- Update trainer profile: `PUT /api/v1/trainers/update/{username}`
- Get trainer's trainings: `GET /api/v1/trainers/{username}/trainings`
- Update trainer status: `PATCH /api/v1/trainers/status/{username}`

### Trainee-Trainer Controller
Manages relationships between trainees and trainers.
- Get unassigned trainers: `GET /api/v1/trainee-trainer/{username}/not-assigned-trainers`
- Update trainee's trainer list: `PUT /api/v1/trainee-trainer/update/{username}`

### Training Controller
Handles training session creation and management.
- Create training: `POST /api/v1/trainings`

### Training Type Controller
Provides access to available training types.
- Get all training types: `GET /api/v1/training-types`

### Trainer-Workload Controller (`/api/v1/trainer-workload`)

| Method | Path | Description                                     | Request Body DTO               | Response Body DTO                 |
| :----- | :--- | :---------------------------------------------- | :----------------------------- | :-------------------------------- |
| `GET`  | `/`  | Get trainer workload summary for a given year and month | - (`year`, `month` as params)  | `TrainerWorkloadSummaryResponseDTO` |



## Testing

To run the tests:
```bash
./gradlew build test