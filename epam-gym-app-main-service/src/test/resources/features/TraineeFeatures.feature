Feature: Trainee Management Operations

  Scenario: Retrieve trainee - Unauthorized (no token/invalid token)
    When I get trainee details using endpoint "/api/v1/trainees/profile"
    Then the profile response status should be 401
    And the trainee response should be unauthorized or not found

  Scenario: Retrieve trainee - Success
    Given I am authenticated as trainee user "jane.smith" with password "password1"
    When I get trainee details using endpoint "/api/v1/trainees/profile"
    Then the trainee profile should be returned


  Scenario: Get trainee trainings - Success with no filters
    Given I am authenticated as trainee user "jane.smith" with password "password1"
    When I retrieve trainings for trainee with no filters using endpoint "/api/v1/trainees/trainings"
    Then the profile response status should be 200
    And the list of trainings should not be empty

  Scenario: Get trainee trainings - Success with filters
    Given I am authenticated as trainee user "jane.smith" with password "password1"
    When I retrieve trainings for trainee with filters:
      | periodFrom | 2024-01-01 |
      | periodTo   | 2026-12-31 |
    And I use endpoint "/api/v1/trainees/trainings"
    Then the profile response status should be 200
    And the list of trainings should not be empty

  Scenario: Get trainee trainings - Unauthorized
    When I retrieve trainings for trainee with no filters using endpoint "/api/v1/trainees/trainings"
    Then the profile response status should be 401
    And the trainee response should be unauthorized or not found

  Scenario: Update trainee - Success
    Given I am authenticated as trainee user "jane.smith" with password "password1"
    And I prepare a trainee update request with:
    """
    {
      "firstname": "Jane",
      "lastname": "Smith",
      "dateOfBirth": "1990-01-01",
      "address": "123 New Address, City"
    }
    """
    When I send a PUT request to endpoint "/api/v1/trainees/update"
    Then the profile response status should be 200
    And the updated trainee profile should contain:
      | firstName | Jane                  |
      | lastName  | Smith                 |
      | address   | 123 New Address, City |

  Scenario: Update trainee - Invalid Request (Missing Firstname)
    Given I am authenticated as trainee user "jane.smith" with password "password1"
    And I prepare a trainee update request with:
    """
    {
      "firstname": "",
      "lastname": "Smith Updated"
    }
    """
    When I send a PUT request to endpoint "/api/v1/trainees/update"
    Then the profile response status should be 400
    And the response body should contain the error message "Firstname is required"

  Scenario: Update trainee - Unauthorized
    And I prepare a trainee update request with:
    """
    {
      "firstname": "Jane Updated",
      "lastname": "Smith Updated"
    }
    """
    When I send a PUT request to endpoint "/api/v1/trainees/update"
    Then the profile response status should be 401
    And the trainee response should be unauthorized or not found

  Scenario: Update trainee status - Activate Success
    Given I am authenticated as trainee user "jane.smith" with password "password1"
    When I send a PATCH request to endpoint "/api/v1/trainees/status" with status "true"
    Then the profile response status should be 200

  Scenario: Update trainee status - Deactivate Success
    Given I am authenticated as trainee user "jane.smith" with password "password1"
    When I send a PATCH request to endpoint "/api/v1/trainees/status" with status "false"
    Then the profile response status should be 200
