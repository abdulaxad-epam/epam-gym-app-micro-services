package epam.cucumber_test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import epam.dto.request_dto.AuthenticateRequestDTO;
import epam.dto.response_dto.AuthenticationResponseDTO;
import epam.dto.response_dto.TraineeResponseDTO;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TraineeSteps {

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    private ResponseEntity<String> latestResponse;
    private final HttpHeaders headers = new HttpHeaders();
    private String traineeUpdateRequestJson;

    @Given("I am authenticated as trainee user {string} with password {string}")
    public void iAmAuthenticatedAsUserWithPassword(String username, String password) throws JsonProcessingException {
        String authBody = String.format("""
                {
                  "username": "%s",
                  "password": "%s"
                }
                """, username, password);

        AuthenticateRequestDTO authRequestDTO = objectMapper.readValue(authBody, AuthenticateRequestDTO.class);

        ResponseEntity<AuthenticationResponseDTO> authResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/v1/auth/authenticate",
                authRequestDTO,
                AuthenticationResponseDTO.class
        );

        assertNotNull(authResponse.getBody());
        assertNotNull(authResponse.getBody().getToken());
        assertEquals(HttpStatus.OK, authResponse.getStatusCode());

        headers.setBearerAuth(authResponse.getBody().getToken().getAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @When("I get trainee details using endpoint {string}")
    public void getTraineeDetailsUsingEndpoint(String endpoint) {
        latestResponse = restTemplate.exchange(
                "http://localhost:" + port + endpoint,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
    }

    @Then("the trainee profile should be returned")
    public void theTraineeProfileShouldBeReturned() throws JsonProcessingException {
        assertNotNull(latestResponse, "Response entity should not be null");
        assertEquals(HttpStatus.OK, latestResponse.getStatusCode(), "Expected 200 OK status");
        assertNotNull(latestResponse.getBody(), "Trainee response body should not be null");

        TraineeResponseDTO body = objectMapper.readValue(latestResponse.getBody(), TraineeResponseDTO.class);
        assertNotNull(body, "Trainee response DTO should not be null after deserialization");
        assertNotNull(body.getUser(), "User details in trainee profile should not be null");
        assertNotNull(body.getUser().getFirstName(), "User first name should not be null");
    }

    @Then("the profile response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatusCode) {
        assertNotNull(latestResponse, "Response entity should not be null");
        assertEquals(HttpStatus.valueOf(expectedStatusCode), latestResponse.getStatusCode(),
                "Expected status code " + expectedStatusCode + " but got " + latestResponse.getStatusCode());
    }

    @Then("the trainee response should be unauthorized or not found")
    public void theTraineeResponseShouldBeUnauthorizedOrNotFound() {
        assertNotNull(latestResponse, "Response entity should not be null");
        boolean isExpectedErrorStatus = latestResponse.getStatusCode().equals(HttpStatus.UNAUTHORIZED) ||
                latestResponse.getStatusCode().equals(HttpStatus.NOT_FOUND);
        assertTrue(isExpectedErrorStatus, "Expected 401 or 404 status, but got " + latestResponse.getStatusCode());
    }

    @Then("the response body should contain the error message {string}")
    public void theResponseBodyShouldContainTheErrorMessage(String expectedMessage) {
        assertNotNull(latestResponse.getBody(), "Response body should not be null for error message assertion");
        assertTrue(latestResponse.getBody().contains(expectedMessage),
                "Response body '" + latestResponse.getBody() + "' did not contain expected message: " + expectedMessage);
    }

    private URI trainingEndpointUri;

    @When("I retrieve trainings for trainee with no filters using endpoint {string}")
    public void iRetrieveTrainingsForTraineeWithNoFiltersUsingEndpoint(String endpoint) {
        this.trainingEndpointUri = UriComponentsBuilder.fromUriString("http://localhost:" + port + endpoint).build().toUri();
        latestResponse = restTemplate.exchange(this.trainingEndpointUri, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    }

    @When("I retrieve trainings for trainee with filters:")
    public void iRetrieveTrainingsForTraineeWithFilters(DataTable dataTable) {
        Map<String, String> filters = dataTable.asMap(String.class, String.class);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost:" + port);

        filters.forEach(uriBuilder::queryParam);
        StringBuilder queryParams = new StringBuilder();
        filters.forEach((key, value) -> {
            if (queryParams.isEmpty()) {
                queryParams.append("?");
            } else {
                queryParams.append("&");
            }
            queryParams.append(key).append("=").append(value);
            uriBuilder.queryParam(key, value.trim());
        });
        this.trainingEndpointUri = URI.create("http://localhost:" + port + "/api/v1/trainees/trainings" + queryParams);
    }

    @When("I use endpoint {string}")
    public void iUseEndpoint(String endpoint) {
        if (this.trainingEndpointUri == null) {
            this.trainingEndpointUri = UriComponentsBuilder.fromUriString("http://localhost:" + port + endpoint).build().toUri();
        }
        latestResponse = restTemplate.exchange(this.trainingEndpointUri, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        this.trainingEndpointUri = null;
    }


    @Then("the list of trainings should not be empty")
    public void theListOfTrainingsShouldNotBeEmpty() throws JsonProcessingException {
        assertNotNull(latestResponse.getBody(), "Response body should not be null");
        List<TrainingResponseDTO> trainings = objectMapper.readValue(latestResponse.getBody(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, TrainingResponseDTO.class));
        assertNotNull(trainings, "Trainings list should not be null");
        assertFalse(trainings.isEmpty(), "Trainings list should not be empty");
    }

    @Given("I prepare a trainee update request with:")
    public void iPrepareATraineeUpdateRequestWith(String docString) {
        this.traineeUpdateRequestJson = docString;
    }

    @When("I send a PUT request to endpoint {string}")
    public void iSendAPutRequestToEndpoint(String endpoint) {
        HttpEntity<String> requestEntity = new HttpEntity<>(traineeUpdateRequestJson, headers);
        latestResponse = restTemplate.exchange("http://localhost:" + port + endpoint, HttpMethod.PUT, requestEntity, String.class);
    }

    @Then("the updated trainee profile should contain:")
    public void theUpdatedTraineeProfileShouldContain(DataTable dataTable) throws JsonProcessingException {
        assertEquals(HttpStatus.OK, latestResponse.getStatusCode(), "Expected 200 OK for update success");
        TraineeResponseDTO responseDTO = objectMapper.readValue(latestResponse.getBody(), TraineeResponseDTO.class);
        assertNotNull(responseDTO, "Updated TraineeResponseDTO should not be null");

        Map<String, String> expectedData = dataTable.asMap(String.class, String.class);

        if (expectedData.containsKey("firstName")) {
            assertEquals(expectedData.get("firstName"), responseDTO.getUser().getFirstName());
        }
        if (expectedData.containsKey("lastName")) {
            assertEquals(expectedData.get("lastName"), responseDTO.getUser().getLastName());
        }
        if (expectedData.containsKey("address")) {
            assertEquals(expectedData.get("address"), responseDTO.getAddress());
        }
    }

    @When("I send a DELETE request to endpoint {string}")
    public void iSendADeleteRequestToEndpoint(String endpoint) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        latestResponse = restTemplate.exchange("http://localhost:" + port + endpoint, HttpMethod.DELETE, requestEntity, String.class);
    }

    @When("I send a PATCH request to endpoint {string} with status {string}")
    public void iSendAPatchRequestToEndpointWithStatus(String endpoint, String isActive) {
        URI uri = UriComponentsBuilder.fromUriString("http://localhost:" + port + endpoint)
                .queryParam("isActive", isActive)
                .build().toUri();

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        latestResponse = restTemplate.exchange(uri, HttpMethod.PATCH, requestEntity, String.class);
    }

    @When("I send a PATCH request to endpoint {string} with missing status")
    public void iSendAPatchRequestToEndpointWithMissingStatus(String endpoint) {
        URI uri = UriComponentsBuilder.fromUriString("http://localhost:" + port + endpoint)
                .build().toUri();

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        latestResponse = restTemplate.exchange(uri, HttpMethod.PATCH, requestEntity, String.class);
    }
}