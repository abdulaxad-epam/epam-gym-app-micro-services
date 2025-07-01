Feature: Trainee-Trainer Management

  Background: Authentication and Data Setup
    Given I am authenticated as user "jane.smith" with password "password1" to get trainee trainer

  Scenario: Get trainers not assigned to trainee - Unauthorized access
    When I send a GET request to "/api/v1/trainee-trainer/not-assigned-trainers" without authentication
    Then the trainee trainer response status should be 401
    And the list of trainers should be empty

  Scenario: Get trainers not assigned to trainee - Happy Path (Trainee with no assigned trainers initially)
    When I send a GET request to "/api/v1/trainee-trainer/not-assigned-trainers" as authenticated user
    Then the trainee trainer response status should be 200
    And the list of trainers should contain trainer "robert.brown"
    And the list of trainers should contain trainer "lisa.wilson"
    And the list of trainers should not contain trainer "jane.smith"

  Scenario: Update trainee's trainer list - Add trainers (Happy Path)
    When I send a PUT request to "/api/v1/trainee-trainer/update" with trainers "robert.brown,sarah.davis" as authenticated user
    Then the update response status should be 200
    And I should get a successful update response
    When I send a GET request to "/api/v1/trainee-trainer/not-assigned-trainers" as authenticated user
    Then the trainee trainer response status should be 200
    And the list of trainers should not contain trainer "robert.brown"
    And the list of trainers should not contain trainer "sarah.davis"

  Scenario: Update trainee's trainer list - Remove trainers (Happy Path - assuming update replaces or adds)
    Given I send a PUT request to "/api/v1/trainee-trainer/update" with trainers "robert.brown,sarah.davis" as authenticated user
    And I should get a successful update response
    When I send a PUT request to "/api/v1/trainee-trainer/update" with trainers "david.miller" as authenticated user
    Then the update response status should be 200
    And I should get a successful update response
    When I send a GET request to "/api/v1/trainee-trainer/not-assigned-trainers" as authenticated user
    Then the trainee trainer response status should be 200
    And the list of trainers should contain trainer "robert.brown"
    And the list of trainers should contain trainer "sarah.davis"
    And the list of trainers should not contain trainer "david.miller"


  Scenario: Update trainee's trainer list - Invalid trainer (trainer username does not exist)
    When I send a PUT request to "/api/v1/trainee-trainer/update" with trainers "nonexistent.trainer" as authenticated user
    Then the update response status should be 404
    And I should get a failed update response

  Scenario: Update trainee's trainer list - Unauthorized access
    When I send a PUT request to "/api/v1/trainee-trainer/update" with trainers "robert.brown" without authentication
    Then the update response status should be 401
    And I should get a failed update response