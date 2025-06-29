Feature: Trainer Workload Summary

  Background: Authenticated Trainer and Workload Data
    Given I am authenticated as a trainer user "david.miller" with password "password6"
    And the trainer workload summary for "david.miller" year 2025 month 10 is 200 minutes
    And the trainer workload summary for "david.miller" year 2025 month 1 is 300 minutes

  Scenario: Get Trainer Workload Summary - Success
    When I send a GET request to "/api/v1/trainer-workload" with year 2025 and month 10 as authenticated trainer
    Then the workload summary response status should be 200
    And the workload summary should contain username "david.miller"
    And the workload summary for year 2025 month 10 should be 200 minutes

  Scenario: Get Trainer Workload Summary - Trainer not found/No workload data for period
    Given I am authenticated as a trainer user "sarah.davis" with password "password5"
    When I send a GET request to "/api/v1/trainer-workload" with year 2025 and month 11 as authenticated trainer without workload summary
    Then the workload summary response status should be 200
    And the workload summary should response "Trainer workload summary not found"

  Scenario: Get Trainer Workload Summary - Unauthorized access
    When I send a GET request to "/api/v1/trainer-workload" with year 2025 and month 10 without authentication
    Then the workload summary response status should be 401
    And the workload summary should be a "Unauthorized" error message