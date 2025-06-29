package epam.cucumber_test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import epam.dto.request_dto.AuthenticateRequestDTO;
import epam.dto.response_dto.AuthenticationResponseDTO;
import epam.dto.response_dto.TrainerResponseDTO;
import epam.dto.response_dto.TrainingResponseDTO;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TrainerSteps {

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    private ResponseEntity<String> latestResponse;
    private HttpHeaders headers = new HttpHeaders();
    private String trainerUpdateRequestJson;
    private URI trainerEndpointUri;

    @Given("I am authenticated as user {string} with password {string}")
    public void iAmAuthenticatedAsUserWithPassword(String username, String password) throws JsonProcessingException {
        String authBody = String.format("""
                {
                  "username": "%s",
                  "password": "%s"
                }
                """, username, password);

        AuthenticateRequestDTO authRequestDTO = objectMapper.readValue(authBody, AuthenticateRequestDTO.class);

        ResponseEntity<AuthenticationResponseDTO> authResponse = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/auth/authenticate",
                authRequestDTO, AuthenticationResponseDTO.class);

        assertNotNull(authResponse.getBody());
        assertNull(authResponse.getBody().getUser());
        assertNotNull(authResponse.getBody().getToken());
        assertNotNull(authResponse.getBody().getToken().getAccessToken());
        assertNotNull(authResponse.getBody().getToken().getRefreshToken());
        assertEquals(HttpStatus.OK, authResponse.getStatusCode());

        headers.setBearerAuth(authResponse.getBody().getToken().getAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Then("the trainer response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatusCode) {
        assertNotNull(latestResponse, "Response entity should not be null");
        assertEquals(HttpStatus.valueOf(expectedStatusCode), latestResponse.getStatusCode(),
                "Expected status code " + expectedStatusCode + " but got " + latestResponse.getStatusCode());
    }

    @Then("the trainer response body should contain the error message {string}")
    public void theResponseBodyShouldContainTheErrorMessage(String expectedMessage) {
        assertNotNull(latestResponse.getBody(), "Response body should not be null for error message assertion");
        assertTrue(latestResponse.getBody().contains(expectedMessage),
                "Response body '" + latestResponse.getBody() + "' did not contain expected message: " + expectedMessage);
    }

    @When("I get trainer profile using endpoint {string}")
    public void iGetTrainerProfileUsingEndpoint(String endpoint) {
        latestResponse = restTemplate.exchange("http://localhost:" + port + endpoint, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    }

    @Then("the trainer profile should be returned")
    public void theTrainerProfileShouldBeReturned() throws JsonProcessingException {
        assertNotNull(latestResponse, "Response entity should not be null");
        assertEquals(HttpStatus.OK, latestResponse.getStatusCode(), "Expected 200 OK status");
        assertNotNull(latestResponse.getBody(), "Trainer response body should not be null");

        TrainerResponseDTO body = objectMapper.readValue(latestResponse.getBody(), TrainerResponseDTO.class);
        assertNotNull(body, "Trainer response DTO should not be null after deserialization");
        assertNotNull(body.getUser(), "User details in trainer profile should not be null");
        assertNotNull(body.getUser().getFirstName(), "User first name should not be null");
        assertNotNull(body.getTrainerSpecialization(), "Trainer specialization should not be null");
    }

    @Then("the trainer response should be unauthorized or not found")
    public void theTrainerResponseShouldBeUnauthorizedOrNotFound() {
        assertNotNull(latestResponse, "Response entity should not be null");
        boolean isExpectedErrorStatus = latestResponse.getStatusCode().equals(HttpStatus.UNAUTHORIZED) ||
                latestResponse.getStatusCode().equals(HttpStatus.NOT_FOUND);
        assertTrue(isExpectedErrorStatus, "Expected 401 or 404 status, but got " + latestResponse.getStatusCode());
    }

    @Given("I prepare a trainer update request with:")
    public void iPrepareATrainerUpdateRequestWith(String docString) {
        this.trainerUpdateRequestJson = docString;
    }

    @When("I send a PUT request to endpoint {string} as trainer")
    public void iSendAPutRequestToEndpoint(String endpoint) {
        HttpEntity<String> requestEntity = new HttpEntity<>(trainerUpdateRequestJson, headers);
        latestResponse = restTemplate.exchange("http://localhost:" + port + endpoint, HttpMethod.PUT, requestEntity, String.class);
    }

    @Then("the updated trainer profile should contain:")
    public void theUpdatedTrainerProfileShouldContain(DataTable dataTable) throws JsonProcessingException {
        assertEquals(HttpStatus.OK, latestResponse.getStatusCode(), "Expected 200 OK for update success");
        TrainerResponseDTO responseDTO = objectMapper.readValue(latestResponse.getBody(), TrainerResponseDTO.class);
        assertNotNull(responseDTO, "Updated TrainerResponseDTO should not be null");

        Map<String, String> expectedData = dataTable.asMap(String.class, String.class);

        if (expectedData.containsKey("firstName")) {
            assertEquals(expectedData.get("firstName"), responseDTO.getUser().getFirstName());
        }
        if (expectedData.containsKey("lastName")) {
            assertEquals(expectedData.get("lastName"), responseDTO.getUser().getLastName());
        }
        if (expectedData.containsKey("specialization")) {
            assertEquals(expectedData.get("specialization"), responseDTO.getTrainerSpecialization());
        }
        if (expectedData.containsKey("isActive")) {
            Boolean expectedIsActive = Boolean.parseBoolean(expectedData.get("isActive"));
            assertEquals(expectedIsActive, responseDTO.getUser().getIsActive());
        }
    }

    @When("I retrieve trainings for trainer with no filters using endpoint {string}")
    public void iRetrieveTrainingsForTrainerWithNoFiltersUsingEndpoint(String endpoint) {
        this.trainerEndpointUri = UriComponentsBuilder.fromUriString("http://localhost:" + port + endpoint).build().toUri();
        latestResponse = restTemplate.exchange(this.trainerEndpointUri, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    }

    @When("I retrieve trainings for trainer with filters:")
    public void iRetrieveTrainingsForTrainerWithFilters(DataTable dataTable) {
        Map<String, String> filters = dataTable.asMap(String.class, String.class);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost:" + port + "/api/v1/trainers/trainings");

        filters.forEach((key, value) -> {
            uriBuilder.queryParam(key, value.trim());
        });
        this.trainerEndpointUri = uriBuilder.build().toUri();
    }

    @When("I use trainer endpoint {string}")
    public void iUseTrainerEndpoint(String endpoint) {
        if (this.trainerEndpointUri == null) {
            this.trainerEndpointUri = UriComponentsBuilder.fromUriString("http://localhost:" + port + endpoint).build().toUri();
        }
        latestResponse = restTemplate.exchange(this.trainerEndpointUri, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        this.trainerEndpointUri = null;
    }

    @Then("the list of trainer trainings should not be empty")
    public void theListOfTrainerTrainingsShouldNotBeEmpty() throws JsonProcessingException {
        assertNotNull(latestResponse.getBody(), "Response body should not be null");
        List<TrainingResponseDTO> trainings = objectMapper.readValue(latestResponse.getBody(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, TrainingResponseDTO.class));
        assertNotNull(trainings, "Trainings list should not be null");
        assertFalse(trainings.isEmpty(), "Trainings list should not be empty");
    }

    @When("I send a PATCH request to trainer endpoint {string} with status {string}")
    public void iSendAPatchRequestToTrainerEndpointWithStatus(String endpoint, String isActive) {
        URI uri = UriComponentsBuilder.fromUriString("http://localhost:" + port + endpoint).queryParam("isActive", isActive).build().toUri();

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        latestResponse = restTemplate.exchange(uri, HttpMethod.PATCH, requestEntity, String.class);
    }

    @When("I send a PATCH request to trainer endpoint {string} with missing status")
    public void iSendAPatchRequestToTrainerEndpointWithMissingStatus(String endpoint) {
        URI uri = UriComponentsBuilder.fromUriString("http://localhost:" + port + endpoint).build().toUri();

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        latestResponse = restTemplate.exchange(uri, HttpMethod.PATCH, requestEntity, String.class);
    }

    @When("I send a DELETE request to trainer endpoint {string}")
    public void iSendADeleteRequestToTrainerEndpoint(String endpoint) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        latestResponse = restTemplate.exchange("http://localhost:" + port + endpoint, HttpMethod.DELETE, requestEntity, String.class);
    }
}