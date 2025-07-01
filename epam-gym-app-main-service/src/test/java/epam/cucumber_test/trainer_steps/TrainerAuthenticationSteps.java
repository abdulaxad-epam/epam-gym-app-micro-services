package epam.cucumber_test.trainer_steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import epam.dto.request_dto.AuthenticateRequestDTO;
import epam.dto.request_dto.RegisterTrainerRequestDTO;
import epam.dto.response_dto.AuthenticationResponseDTO;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


public class TrainerAuthenticationSteps {


    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterTrainerRequestDTO trainerRequestBody;

    private AuthenticateRequestDTO authenticateRequestDTO;

    @LocalServerPort
    private String port;

    private ResponseEntity<AuthenticationResponseDTO> response;

    private @NotNull String url() {
        return "http://localhost:" + port;
    }

    @Given("the trainer request body:")
    public void theTrainerRequestBody(String requestBodyJson) throws JsonProcessingException {
        this.trainerRequestBody = objectMapper.readValue(requestBodyJson, RegisterTrainerRequestDTO.class);
    }

    @When("the trainer calls endpoint {string}")
    public void whenTrainerRegistration(String endpoint) {
        response = restTemplate.postForEntity("http://localhost:" + port + endpoint, trainerRequestBody, AuthenticationResponseDTO.class);
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatusCode) {
        assertEquals(HttpStatus.valueOf(expectedStatusCode), response.getStatusCode());
    }

    @Then("the response should not contain a token")
    public void theResponseShouldNotContainAToken() {
        assertNotNull(response.getBody());
        assertNull(response.getBody().getToken());
    }

    @Given("the trainer auth body:")
    public void theTrainerAuthBody(String requestBodyJson) throws JsonProcessingException {
        this.authenticateRequestDTO = objectMapper.readValue(requestBodyJson, AuthenticateRequestDTO.class);

    }

    @When("the trainer authenticates with endpoint {string}")
    public void theTrainerAuthenticatesWithEndpoint(String arg0) {
        response = restTemplate.postForEntity(url() + arg0, authenticateRequestDTO, AuthenticationResponseDTO.class);
    }

    @Then("the trainer authentication response status should be {int}")
    public void theTrainerAuthenticationResponseStatusShouldBe(int arg0) {
        assertEquals(HttpStatus.valueOf(arg0), response.getStatusCode());
    }

    @And("the trainer response should contain a token")
    public void theTrainerResponseShouldContainAToken() {
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getToken());
    }
}