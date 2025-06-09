# Epam Gym CRM
## Trainer Workload Service
A comprehensive Spring Boot application for managing trainers, trainees, and training sessions.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Setup Instructions](#setup-instructions)
- [Usage Examples](#usage-examples)
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

## Setup Instructions

### Installation

1. Clone the repository:
```bash
git clone --branch epam-gym-app-trainer-workload-service https://github.com/abdulaxad-epam/epam-gym-app.git 
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
### Trainer Workload API Documentation

### 1. Perform an Action on Trainer Workload

### Endpoint
```http
POST /api/v1/trainer-workload/action
```

### Description
Triggers a workload-related action for a specific trainer based on the provided request data.

### Request Headers
```http
Content-Type: application/json
```

### Request Body
```json
{
  "traineeUsername": "jane.smith",
  "trainingName": "Flexibility",
  "trainingDate": "2025-06-10",
  "trainingType": "CARDIOVASCULAR_TRAINING",
  "trainingDuration": 60
}
```

*Note: Adjust `actionType` and `hours` according to business rules.*

### Example cURL
```bash
curl -X POST http://localhost:8081/api/v1/trainer-workload/action \
  -H "Content-Type: application/json" \
  -d '{
        "traineeUsername": "jane.smith",
        "trainingName": "Flexibility",
        "trainingDate": "2025-06-10",
        "trainingType": "CARDIOVASCULAR_TRAINING",
        "trainingDuration": 60
        "actionType": ADD/DELETE
  }'
```

---

### 2. Retrieve Trainer Workload Summary

#### Endpoint
```http
GET /api/v1/trainer-workload-summary
```

#### Description
Returns a summary of a trainer’s workload for a specific month and year.

#### Query Parameters
- `trainerUsername` (String) — required
- `year` (Integer) — required
- `month` (Integer) — required

### Example cURL
```bash
curl -X GET "http://localhost:8081/api/v1/trainer-workload-summary?trainerUsername=john.smith&year=2025&month=6"
```

The `TrainerWorkloadService` provides methods to interact with the external trainer workload system.

* `actionOnADD(Training training)`: Sends a request to add a new training workload for a trainer.
* `actionOnDELETE(Training training)`: Sends a request to delete an existing training workload for a trainer.

These methods internally handle:
* Mapping `Training` entities to `TrainerWorkloadRequestDTO`.
* Generating JWT tokens.
* Making HTTP POST requests to the configured workload service endpoint.
* Applying Circuit Breaker logic for resilience.


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