package epam.cucumber_test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import epam.dto.request_dto.AuthenticateRequestDTO;
import epam.dto.request_dto.RegisterTraineeRequestDTO;
import epam.dto.response_dto.AuthenticationResponseDTO;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


@Transactional(propagation = Propagation.REQUIRES_NEW)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TraineeAuthenticationSteps {

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterTraineeRequestDTO traineeRequestDTO;

    private AuthenticateRequestDTO authenticateRequestDTO;

    @LocalServerPort
    private String port;

    private ResponseEntity<AuthenticationResponseDTO> response;

    @Given("the trainee request body:")
    public void theTraineeRequestBody(String requestBodyJson) throws JsonProcessingException {
        this.traineeRequestDTO = objectMapper.readValue(requestBodyJson, RegisterTraineeRequestDTO.class);

    }

    @When("the trainee calls endpoint {string}")
    public void theTraineeCallsEndpoint(String arg0) {
        response = restTemplate.postForEntity("http://localhost:" + port + arg0, traineeRequestDTO, AuthenticationResponseDTO.class);

    }

    @Then("the trainee response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatusCode) {
        assertEquals(HttpStatus.valueOf(expectedStatusCode), response.getStatusCode());
    }

    @Then("the trainee response should not contain a token")
    public void theResponseShouldNotContainAToken() {
        assertNotNull(response.getBody());
        assertNull(response.getBody().getToken());
    }


    @Given("the trainee auth body:")
    public void theTraineeAuthBody(String requestBodyJson) throws JsonProcessingException {
        this.authenticateRequestDTO = objectMapper.readValue(requestBodyJson, AuthenticateRequestDTO.class);
    }

    @When("the trainee authenticates with endpoint {string}")
    public void theTraineeAuthenticatesWithEndpoint(String arg0) {
        response = restTemplate.postForEntity("http://localhost:" + port + arg0, authenticateRequestDTO, AuthenticationResponseDTO.class);
    }

    @Then("the trainee authentication response status should be {int}")
    public void theTraineeAuthenticationResponseStatusShouldBe(int arg0) {
        assertEquals(HttpStatus.valueOf(arg0), response.getStatusCode());
    }


    @And("the trainee response should contain a token")
    public void theTraineeResponseShouldContainAToken() {
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getToken());
    }
}
