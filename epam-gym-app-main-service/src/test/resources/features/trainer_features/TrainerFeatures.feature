Feature: Trainer Management Operations

  Scenario: Get trainer profile - Success
    Given I am authenticated as user "david.miller" with password "password6"
    When I get trainer profile using endpoint "/api/v1/trainers/profile"
    Then the trainer response status should be 200
    And the trainer profile should be returned

  Scenario: Get trainer profile - Unauthorized
    When I get trainer profile using endpoint "/api/v1/trainers/profile"
    Then the trainer response status should be 401
    And the trainer response should be unauthorized or not found

  Scenario: Update trainer profile - Success
    Given I am authenticated as user "lisa.wilson" with password "password7"
    And I prepare a trainer update request with:
    """
    {
      "specialization": "FLEXIBILITY",
        "user":{
        "firstName":"userfe",
        "lastName":"userle",
        "isActive":"true"
       }
    }
    """
    When I send a PUT request to endpoint "/api/v1/trainers/update" as trainer
    Then the trainer response status should be 200
    And the updated trainer profile should contain:
      | firstName      | userfe      |
      | lastName       | userle      |
      | specialization | FLEXIBILITY |
      | isActive       | true        |

  Scenario: Update trainer profile - Invalid Request (Missing Specialization)
    Given I am authenticated as user "james.taylor" with password "password8"
    And I prepare a trainer update request with:
    """
    {
      "specialization": "",
      "user": {
        "firstName": "John",
        "lastName": "Doe",
        "isActive": true
      }
    }
    """
    When I send a PUT request to endpoint "/api/v1/trainers/update" as trainer
    Then the trainer response status should be 400
    And the trainer response body should contain the error message "Specialization is required"

  Scenario: Update trainer profile - Unauthorized
    And I prepare a trainer update request with:
    """
    {
      "specialization": "Weightlifting",
      "user": {
        "firstName": "John",
        "lastName": "Doe"
      }
    }
    """
    When I send a PUT request to endpoint "/api/v1/trainers/update" as trainer
    Then the trainer response status should be 401
    And the trainer response should be unauthorized or not found

  Scenario: Get trainer trainings - Success with no filters
    Given I am authenticated as user "david.miller" with password "password6"
    When I retrieve trainings for trainer with no filters using endpoint "/api/v1/trainers/trainings"
    Then the trainer response status should be 200
    And the list of trainer trainings should not be empty

  Scenario: Get trainer trainings - Success with filters
    Given I am authenticated as user "sarah.davis" with password "password5"
    When I retrieve trainings for trainer with filters:
      | periodFrom | 2024-01-01 |
      | periodTo   | 2026-12-31 |
    And I use trainer endpoint "/api/v1/trainers/trainings"
    Then the trainer response status should be 200
    And the list of trainer trainings should not be empty

  Scenario: Get trainer trainings - Unauthorized
    When I retrieve trainings for trainer with no filters using endpoint "/api/v1/trainers/trainings"
    Then the trainer response status should be 401
    And the trainer response should be unauthorized or not found

  Scenario: Update trainer status - Activate Success
    Given I am authenticated as user "david.miller" with password "password6"
    When I send a PATCH request to trainer endpoint "/api/v1/trainers/status" with status "true"
    Then the trainer response status should be 200

  Scenario: Update trainer status - Deactivate Success
    Given I am authenticated as user "lisa.wilson" with password "password7"
    When I send a PATCH request to trainer endpoint "/api/v1/trainers/status" with status "false"
    Then the trainer response status should be 200

  Scenario: Update trainer status - Missing status
    Given I am authenticated as user "robert.brown" with password "password4"
    When I send a PATCH request to trainer endpoint "/api/v1/trainers/status" with missing status
    Then the trainer response status should be 401
    And the trainer response body should contain the error message "Unauthorized access"

  Scenario: Delete trainer - Success
    Given I am authenticated as user "robert.brown" with password "password4"
    When I send a DELETE request to trainer endpoint "/api/v1/trainers/delete"
    Then the trainer response status should be 204
