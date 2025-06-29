Feature: Test create trainer

  Scenario: Should return unauthorized without body
    When the trainer calls endpoint "/api/v1/auth/register/trainer"
    Then the response status should be 401
    And the response should not contain a token

  Scenario: Should return unauthorized without specialization
    Given the trainer request body:
    """
    {
     "user":{
        "firstName":"userfr",
        "lastName":"usermr",
        "isActive":"true"
            }
     }
      """
    When the trainer calls endpoint "/api/v1/auth/register/trainer"
    Then the response status should be 400
    And the response should not contain a token

  Scenario: Should return ok for trainer registration
    Given the trainer request body:
        """
  {
    "specialization": "STRENGTH_TRAINING",
      "user":{
      "firstName":"userft",
      "lastName":"usermt",
      "isActive":"true"
       }
  }
  """
    When the trainer calls endpoint "/api/v1/auth/register/trainer"
    Then the response status should be 200
    And the response should not contain a token

  Scenario: Should return incorrect password and/or username
    Given the trainer auth body:
    """
    {
      "username":"robert.brown1",
      "password":"incorrectp"
    }
    """
    When the trainer authenticates with endpoint "/api/v1/auth/authenticate"
    Then the trainer authentication response status should be 404
    And the response should not contain a token

  Scenario: Should return ok for trainer authentication
    Given the trainer auth body:
    """
    {
      "username":"sarah.davis",
      "password":"password5"
    }
    """
    When the trainer authenticates with endpoint "/api/v1/auth/authenticate"
    Then the trainer authentication response status should be 200
    And the trainer response should contain a token
