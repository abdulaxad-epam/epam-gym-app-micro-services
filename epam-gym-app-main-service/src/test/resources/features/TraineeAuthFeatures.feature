Feature: Test create trainee

  Scenario: Should return unauthorized without body
    When the trainee calls endpoint "/api/v1/auth/register/trainee"
    Then the trainee response status should be 401
    And the trainee response should not contain a token

  Scenario: Should return unauthorized without specialization
    Given the trainee request body:
    """
    {
    "user":{
        "firstName":"firstName",
        "lastName":"lastName",
        "isActive":"true"
     }
    }
      """
    When the trainee calls endpoint "/api/v1/auth/register/trainee"
    Then the trainee response status should be 400
    And the trainee response should not contain a token

  Scenario: Should return ok for trainee registration
    Given the trainee request body:
        """
    {
    "dateOfBirth": "1999-05-01",
    "address": "USA",
    "user":{
        "firstName":"rolls",
        "lastName":"royce",
        "isActive":"true"
      }
    }
  """
    When the trainee calls endpoint "/api/v1/auth/register/trainee"
    Then the trainee response status should be 200
    And the trainee response should not contain a token

  Scenario: Should return incorrect password and/or username
    Given the trainee auth body:
    """
    {
      "username":"jane.smith",
      "password":"incorrectp"
    }
    """
    When the trainee authenticates with endpoint "/api/v1/auth/authenticate"
    Then the trainee authentication response status should be 404
    And the trainee response should not contain a token

  Scenario: Should return ok for trainee authentication
    Given the trainee auth body:
    """
    {
      "username":"jane.smith",
      "password":"password1"
    }
    """
    When the trainee authenticates with endpoint "/api/v1/auth/authenticate"
    Then the trainee authentication response status should be 200
    And the trainee response should contain a token
