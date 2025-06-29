package epam.cucumber_test;

import epam.dto.request_dto.AuthenticateRequestDTO;
import epam.dto.response_dto.AuthenticationResponseDTO;
import epam.dto.response_dto.TrainerResponseDTO;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Transactional(propagation = Propagation.REQUIRES_NEW)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TraineeTrainerSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<List<TrainerResponseDTO>> trainerListResponse;
    private ResponseEntity<String> updateResponse;

    private String authToken;


    @Given("I am authenticated as user {string} with password {string} to get trainee trainer")
    public void iAmAuthenticatedAsUserWithPassword(String username, String password) {
        AuthenticateRequestDTO authenticateRequestDTO = AuthenticateRequestDTO.builder().username(username).password(password).build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AuthenticateRequestDTO> request = new HttpEntity<>(authenticateRequestDTO, headers);

        ResponseEntity<AuthenticationResponseDTO> authResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/v1/auth/authenticate",
                request,
                AuthenticationResponseDTO.class
        );

        assertEquals(HttpStatus.OK, authResponse.getStatusCode(), "Authentication should be successful");
        assertNotNull(authResponse.getBody());
        assertNotNull(authResponse.getBody().getToken(), "Auth token should not be null");
        this.authToken = authResponse.getBody().getToken().getAccessToken();
    }


    @When("I send a GET request to {string} as authenticated user")
    public void iSendAGetRequestToAsAuthenticatedUser(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        trainerListResponse = restTemplate.exchange("http://localhost:" + port + endpoint, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
                }
        );
    }

    @When("I send a GET request to {string} without authentication")
    public void iSendAGetRequestToWithoutAuthentication(String endpoint) {
        trainerListResponse = restTemplate.getForEntity("http://localhost:" + port + endpoint, null, TrainerResponseDTO.class);
    }

    @Then("the trainee trainer response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatusCode) {
        assertEquals(HttpStatus.valueOf(expectedStatusCode), trainerListResponse.getStatusCode());
    }

    @And("the list of trainers should be empty")
    public void theListOfTrainersShouldBeEmpty() {
        assertNull(trainerListResponse.getBody(), "Response body should not be null");
    }

    @And("the list of trainers should contain {int} trainers")
    public void theListOfTrainersShouldContainTrainers(int expectedCount) {
        assertNotNull(trainerListResponse.getBody(), "Response body should not be null");
        assertEquals(expectedCount, trainerListResponse.getBody().size(), "The list should contain " + expectedCount + " trainers");
    }

    @And("the list of trainers should contain trainer {string}")
    public void theListOfTrainersShouldContainTrainer(String expectedTrainerUsername) {
        assertNotNull(trainerListResponse.getBody());
        boolean found = trainerListResponse.getBody().stream()
                .anyMatch(trainer -> trainer.getUser() != null && expectedTrainerUsername.equals(trainer.getUser().getUsername()));
        assertTrue(found, "Trainer " + expectedTrainerUsername + " should be in the list");
    }

    @And("the list of trainers should not contain trainer {string}")
    public void theListOfTrainersShouldNotContainTrainer(String unexpectedTrainerUsername) {
        assertNotNull(trainerListResponse.getBody());
        boolean found = trainerListResponse.getBody().stream()
                .anyMatch(trainer -> trainer.getUser() != null && unexpectedTrainerUsername.equals(trainer.getUser().getUsername()));
        assertFalse(found, "Trainer " + unexpectedTrainerUsername + " should NOT be in the list");
    }


    @When("I send a PUT request to {string} with trainers {string} as authenticated user")
    public void iSendAPutRequestToWithTrainersAsAuthenticatedUser(String endpoint, String trainerUsernames) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        URI uri = new URI("http://localhost:" + port + endpoint + "?trainers=" +
                trainerUsernames.replace(",", "&trainers="));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        updateResponse = restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);
    }

    @When("I send a PUT request to {string} with trainers {string} without authentication")
    public void iSendAPutRequestToWithTrainersWithoutAuthentication(String endpoint, String trainerUsernames) throws Exception {
        URI uri = new URI("http://localhost:" + port + endpoint + "?trainers=" +
                trainerUsernames.replace(",", "&trainers="));

        updateResponse = restTemplate.exchange(uri, HttpMethod.PUT, null, String.class);
    }

    @Then("the update response status should be {int}")
    public void theUpdateResponseStatusShouldBe(int expectedStatusCode) {
        assertEquals(HttpStatus.valueOf(expectedStatusCode), updateResponse.getStatusCode());
    }

    @And("I should get a successful update response")
    public void iShouldGetASuccessfulUpdateResponse() {
        assertNotNull(updateResponse);
        assertTrue(updateResponse.getStatusCode().is2xxSuccessful(), "Update response should be successful (2xx)");
    }

    @And("I should get a failed update response")
    public void iShouldGetAFailedUpdateResponse() {
        assertNotNull(updateResponse);
        assertFalse(updateResponse.getStatusCode().is2xxSuccessful(), "Update response should indicate failure");
    }
}