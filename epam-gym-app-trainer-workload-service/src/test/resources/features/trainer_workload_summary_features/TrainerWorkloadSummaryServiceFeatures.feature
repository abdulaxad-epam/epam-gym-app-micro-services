Feature: Trainer Workload Summary Service Operations

  Scenario: Produce message for active trainer workload
    Given a TrainerWorkloadResponseDTO for trainer "john.doe" with first name "John", last name "Doe", training date "2025-01-15", and duration 60
    And the active status is "true"
    When the produce method is called
    Then the properties builder should be called with year 2025 and month 1
    And the trainer action producer should produce a message
    And the produced message should contain username "john.doe", first name "John", last name "Doe", status "true", year 2025, month 1, and duration 60

  Scenario: Produce message for inactive trainer workload
    Given a TrainerWorkloadResponseDTO for trainer "jane.smith" with first name "Jane", last name "Smith", training date "2024-11-20", and duration 45
    And the active status is "false"
    When the produce method is called
    Then the properties builder should be called with year 2024 and month 11
    And the trainer action producer should produce a message
    And the produced message should contain username "jane.smith", first name "Jane", last name "Smith", status "false", year 2024, month 11, and duration 45

  Scenario: Produce message with different date
    Given a TrainerWorkloadResponseDTO for trainer "test.user" with first name "Test", last name "User", training date "2023-03-05", and duration 120
    And the active status is "true"
    When the produce method is called
    Then the properties builder should be called with year 2023 and month 3
    And the trainer action producer should produce a message
    And the produced message should contain username "test.user", first name "Test", last name "User", status "true", year 2023, month 3, and duration 120