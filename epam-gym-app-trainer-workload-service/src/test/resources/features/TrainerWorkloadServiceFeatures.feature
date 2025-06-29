Feature: Trainer Workload Service Layer Operations

  Background:


  Scenario: Add new training - Creates a new trainer workload
    Given a TrainerWorkloadRequestDTO for trainer "robert.brown" with first name "New", last name "Trainer", active status "true", date "2025-07-01", duration 60, and action type "ADD"
    When the actionOn method is called with the request DTO
    Then a TrainerWorkloadResponseDTO should be returned
    And the response DTO's username should be "robert.brown"
    And the response DTO's training date should be "2025-07-01"
    And the response DTO's training duration should be 60 minutes
    And the trainer workload repository save method should be called
    And a new TrainerWorkload entity should have been saved with username "robert.brown" and daily duration 60 for date "2025-07-01"
    And the trainer workload summary service produce method should be called with active status "true"

  Scenario: Add training - Updates an existing trainer workload
    Given an existing TrainerWorkload for trainer "robert.brown" with initial daily duration 120 for date "2025-07-02"
    And a TrainerWorkloadRequestDTO for trainer "robert.brown" with first name "Existing", last name "Trainer", active status "true", date "2025-07-02", duration 90, and action type "ADD"
    When the actionOn method is called with the request DTO
    Then a TrainerWorkloadResponseDTO should be returned
    And the response DTO's username should be "robert.brown"
    And the response DTO's training date should be "2025-07-02"
    And the trainer workload repository save method should be called
    And the trainer workload summary service produce method should be called with active status "true"

  Scenario: Delete training - Updates an existing trainer workload
    Given an existing TrainerWorkload for trainer "robert.brown" with initial daily duration 100 for date "2025-07-03"
    And a TrainerWorkloadRequestDTO for trainer "robert.brown" with first name "Existing", last name "Del", active status "true", date "2025-07-03", duration 40, and action type "DELETE"
    When the actionOn method is called with the request DTO
    Then a TrainerWorkloadResponseDTO should be null


  Scenario: Delete training - Trainer workload not found
    Given a TrainerWorkloadRequestDTO for trainer "nonexistent.trainer" with first name "Non", last name "Existent", active status "true", date "2025-07-06", duration 60, and action type "DELETE"
    When the actionOn method is called with the request DTO
    Then a TrainerWorkloadNotFoundException should be thrown
    And the trainer workload repository save method should not be called
    And the trainer workload summary service produce method should not be called