package epam.cucumber_test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import epam.dto.request_dto.AuthenticateRequestDTO;
import epam.dto.response_dto.AuthenticationResponseDTO;
import epam.dto.response_dto.TrainingTypeResponseDTO;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Transactional(propagation = Propagation.REQUIRES_NEW)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TrainingTypeSteps {

    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    private String authToken;
    private ResponseEntity<?> latestResponse;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Given("I am authenticated as a {string} user {string} with password {string} for training types")
    public void iAmAuthenticatedAsAUserWithPassword(String role, String username, String password) {
        AuthenticateRequestDTO authRequest = AuthenticateRequestDTO.builder()
                .username(username)
                .password(password)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AuthenticateRequestDTO> request = new HttpEntity<>(authRequest, headers);

        ResponseEntity<AuthenticationResponseDTO> authResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/v1/auth/authenticate", request, AuthenticationResponseDTO.class);

        assertEquals(HttpStatus.OK, authResponse.getStatusCode(), "Authentication as " + role + " should be successful");
        assertNotNull(authResponse.getBody());
        assertNotNull(authResponse.getBody().getToken(), "Auth token for " + role + " should not be null");
        this.authToken = authResponse.getBody().getToken().getAccessToken();
    }


    @When("I send a GET request to {string} to get training types")
    public void iSendAGetRequestToGetTrainingTypes(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        if (authToken != null) {
            headers.setBearerAuth(authToken);
        }
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        latestResponse = restTemplate.exchange("http://localhost:" + port + endpoint, HttpMethod.GET, entity, String.class);
    }

    @Then("the training types response status should be {int}")
    public void theTrainingTypesResponseStatusShouldBe(int expectedStatus) {
        assertNotNull(latestResponse, "No response received for training types.");
        assertEquals(HttpStatus.valueOf(expectedStatus), latestResponse.getStatusCode());
    }

    @And("the training types response should contain a list of {int} training types")
    public void theTrainingTypesResponseShouldContainListOfTrainingTypes(int expectedCount) throws JsonProcessingException {
        assertNotNull(latestResponse.getBody(), "Response body should not be null.");
        assertInstanceOf(String.class, latestResponse.getBody(), "Response body is not a String.");

        List<TrainingTypeResponseDTO> trainingTypes = objectMapper.readValue((String) latestResponse.getBody(), new TypeReference<>() {
        });
        assertNotNull(trainingTypes, "Parsed training types list should not be null.");
        assertEquals(expectedCount, trainingTypes.size(), "Number of training types mismatch.");

    }

    @And("the training types response should be an error message {string}")
    public void theTrainingTypesResponseShouldBeAnErrorMessage(String errorMessage) {
        assertNotNull(latestResponse.getBody(), "Response body should not be null for error.");
        assertInstanceOf(String.class, latestResponse.getBody(), "Response body is not a String.");
        String responseBody = (String) latestResponse.getBody();
        assertTrue(responseBody.contains(errorMessage), "Error message expected: '" + errorMessage + "', but found: " + responseBody);
    }
}