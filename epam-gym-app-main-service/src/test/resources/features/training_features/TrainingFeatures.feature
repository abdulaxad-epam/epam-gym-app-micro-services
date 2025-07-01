Feature: Training Management

  Background: Authentication Setup
    Given the trainer workload summary for "lisa.wilson" year 2025 month 10 is 200 minutes for training management
    And the trainer workload summary for "lisa.wilson" year 2025 month 1 is 300 minutes for training management

  Scenario: Create Training - Success by Trainer
    Given I am authenticated as a "trainer" user "lisa.wilson" with password "password7"
    When I send a POST request to "/api/v1/trainings" to create a training with trainer "lisa.wilson", trainee "jane.smith", name "YogaSession", type "YOGA", date "2025-07-15", and duration 60
    Then the training creation response status should be 200
    And the created training response should contain id, trainer "lisa.wilson", trainee "jane.smith", name "YogaSession", type "YOGA", date "2025-07-15", and duration 60

  Scenario: Create Training - Success by Trainee
    Given I am authenticated as a "trainee" user "jane.smith" with password "password1"
    When I send a POST request to "/api/v1/trainings" to create a training with trainer "james.taylor", trainee "jane.smith", name "CardioWorkout", type "HIIT", date "2025-08-01", and duration 45
    Then the training creation response status should be 200
    And the created training response should contain id, trainer "james.taylor", trainee "jane.smith", name "CardioWorkout", type "HIIT", date "2025-08-01", and duration 45

  Scenario: Create Training - Unauthorized Access (No Token)
    When I send a POST request to "/api/v1/trainings" to create a training with trainer "lisa.wilson", trainee "jane.smith", name "PilatesSession", type "FLEXIBILITY", date "2025-07-20", and duration 75
    Then the training creation response status should be 401
    And the training creation response should contain null values

  Scenario: Create Training - Unauthorized Access (User trying to create for someone else)
    Given I am authenticated as a "trainee" user "lisa.wilson" with password "password7"
    When I send a POST request to "/api/v1/trainings" to create a training with trainer "lisa.wilson", trainee "other.user", name "Weightlifting", type "STRENGTH_TRAINING", date "2025-09-01", and duration 90
    Then the training creation response status should be 404
    And the training creation response should contain null values


  Scenario: Delete Training - Success by Authenticated User
    Given I am authenticated as a "trainer" user "james.taylor" with password "password8"
    When I send a DELETE request to "/api/v1/trainings" for the created training with ID
    Then the training deletion response status should be 200
    And the training deletion response should contain message "deleted successfully"

  Scenario: Delete Training - Unauthorized access
    Given I am authenticated as a "trainer" user "emily.anderson" with password "password9"
    When I send a DELETE request to "/api/v1/trainings" with ID "00000000-0000-0000-0000-000000000000"
    Then the training deletion response status should be 404
    And the training deletion response should contain an error message "Training not found"
