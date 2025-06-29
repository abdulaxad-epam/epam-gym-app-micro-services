Feature: Training Type Management

  Scenario: Get all training types - Success (Authenticated)
    Given I am authenticated as a "trainer" user "michael.johnson" with password "password2" for training types
    When I send a GET request to "/api/v1/training-types" to get training types
    Then the training types response status should be 200
    And the training types response should contain a list of 7 training types

  Scenario: Get all training types - Unauthorized
    When I send a GET request to "/api/v1/training-types" to get training types
    Then the training types response status should be 401
    And the training types response should be an error message "Unauthorized"