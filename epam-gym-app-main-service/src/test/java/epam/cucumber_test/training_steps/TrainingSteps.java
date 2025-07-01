package epam.cucumber_test.training_steps;

import epam.client.dto.TrainerWorkloadSummaryInMonthsResponseDTO;
import epam.client.dto.TrainerWorkloadSummaryInYearsResponseDTO;
import epam.client.dto.TrainerWorkloadSummaryResponseDTO;
import epam.client.messaging.consumer.TrainerWorkloadSummaryMessageConsumer;
import epam.client.messaging.consumer.impl.TrainerWorkloadSummaryMessageConsumerImpl;
import epam.dto.request_dto.AuthenticateRequestDTO;
import epam.dto.request_dto.TrainingRequestDTO;
import epam.dto.response_dto.AuthenticationResponseDTO;
import epam.dto.response_dto.TrainingResponseDTO;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.jms.JMSException;
import jakarta.validation.constraints.NotNull;
import org.apache.activemq.Message;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class TrainingSteps {

    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    private final TrainerWorkloadSummaryMessageConsumer trainerWorkloadSummaryMessageConsumer = new TrainerWorkloadSummaryMessageConsumerImpl();

    private String authToken;
    private ResponseEntity<?> latestResponse;
    private static UUID createdTrainingId;

    private @NotNull String url() {
        return "http://localhost:" + port;
    }

    @Given("I am authenticated as a {string} user {string} with password {string}")
    public void iAmAuthenticatedAsAUserWithPassword(String role, String username, String password) {
        AuthenticateRequestDTO authRequest = AuthenticateRequestDTO.builder()
                .username(username)
                .password(password)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AuthenticateRequestDTO> request = new HttpEntity<>(authRequest, headers);

        ResponseEntity<AuthenticationResponseDTO> authResponse = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/auth/authenticate", request, AuthenticationResponseDTO.class);

        assertEquals(HttpStatus.OK, authResponse.getStatusCode(), "Authentication as " + role + " should be successful");
        assertNotNull(authResponse.getBody());
        assertNotNull(authResponse.getBody().getToken(), "Auth token for " + role + " should not be null");
        this.authToken = authResponse.getBody().getToken().getAccessToken();
    }

    @Given("the trainer workload summary for {string} year {int} month {int} is {int} minutes for training management")
    public void theTrainerWorkloadSummaryForYearMonthIsMinutes(String username, int year, int month, int duration) throws JMSException {
        TrainerWorkloadSummaryInMonthsResponseDTO monthSummary = TrainerWorkloadSummaryInMonthsResponseDTO.builder()
                .month(String.valueOf(month))
                .durationInMinutes(String.valueOf(duration))
                .build();

        TrainerWorkloadSummaryInYearsResponseDTO yearSummary = TrainerWorkloadSummaryInYearsResponseDTO.builder()
                .year(String.valueOf(year))
                .workloadSummaryInMonths(Collections.singletonList(monthSummary))
                .build();

        TrainerWorkloadSummaryResponseDTO summaryDTO = TrainerWorkloadSummaryResponseDTO.builder()
                .username(username)
                .firstName("Robert")
                .lastName("Brown")
                .status(true)
                .workloadSummaryInYears(Collections.singletonList(yearSummary))
                .build();

        Message mockMessage = new ActiveMQBytesMessage();
        mockMessage.setIntProperty("year", year);
        mockMessage.setIntProperty("month", month);
        trainerWorkloadSummaryMessageConsumer.consumeOnAction(summaryDTO, mockMessage);
    }

    @When("I send a POST request to {string} to create a training with trainer {string}, trainee {string}, name {string}, type {string}, date {string}, and duration {int}")
    public void iSendAPostRequestToCreateATraining(String endpoint, String trainerUsername, String traineeUsername,
                                                   String trainingName, String trainingType, String trainingDateStr,
                                                   int duration) {
        TrainingRequestDTO requestDTO = TrainingRequestDTO.builder()
                .trainerUsername(trainerUsername)
                .traineeUsername(traineeUsername)
                .trainingName(trainingName)
                .trainingType(trainingType)
                .trainingDate(LocalDate.parse(trainingDateStr))
                .trainingDurationInMinutes(duration)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (authToken != null) {
            headers.setBearerAuth(authToken);
        }
        HttpEntity<TrainingRequestDTO> request = new HttpEntity<>(requestDTO, headers);

        latestResponse = restTemplate.exchange("http://localhost:" + port + endpoint, HttpMethod.POST, request, TrainingResponseDTO.class);
    }

    @Then("the training creation response status should be {int}")
    public void theTrainingCreationResponseStatusShouldBe(int expectedStatus) {
        assertNotNull(latestResponse, "No response received from training creation.");
        assertEquals(HttpStatus.valueOf(expectedStatus), latestResponse.getStatusCode());
    }

    @And("the created training response should contain id, trainer {string}, trainee {string}, name {string}, type {string}, date {string}, and duration {int}")
    public void theCreatedTrainingResponseShouldContainDetails(String trainerUsername, String traineeUsername,
                                                               String trainingName, String trainingType, String trainingDateStr,
                                                               int duration) {
        assertNotNull(latestResponse.getBody(), "Response body should not be null.");
        assertInstanceOf(TrainingResponseDTO.class, latestResponse.getBody(), "Response body is not a TrainingResponseDTO.");
        TrainingResponseDTO responseDTO = (TrainingResponseDTO) latestResponse.getBody();

        assertNotNull(responseDTO.getTrainingId(), "Training ID should be present.");
        assertEquals(trainerUsername, responseDTO.getTrainer().getUser().getUsername());
        assertEquals(traineeUsername, responseDTO.getTrainee().getUser().getUsername());
        assertEquals(trainingName, responseDTO.getTrainingName());
        assertEquals(trainingType, responseDTO.getTrainingType());
        assertEquals(LocalDate.parse(trainingDateStr, DateTimeFormatter.ISO_LOCAL_DATE), responseDTO.getTrainingDate());
        assertEquals(duration, responseDTO.getTrainingDurationInMinutes());

        createdTrainingId = responseDTO.getTrainingId();
    }

    @And("the training creation response should contain null values")
    public void theTrainingCreationResponseShouldContainErrorMessage() {
        assertNotNull(latestResponse.getBody(), "Response body should not be null for error.");
        assertInstanceOf(TrainingResponseDTO.class, latestResponse.getBody(), "Response body is not a String for error.");
        TrainingResponseDTO responseBody = (TrainingResponseDTO) latestResponse.getBody();
        assertNull(responseBody.getTrainingDate());
        assertNull(responseBody.getTrainee());
        assertNull(responseBody.getTrainingName());
        assertNull(responseBody.getTrainer());
    }

    @When("I send a DELETE request to {string} for the created training with ID")
    public void iSendADeleteRequestForTheCreatedTraining(String endpoint) {
        assertNotNull(createdTrainingId, "No training ID was captured from a creation step.");

        HttpHeaders headers = new HttpHeaders();
        if (authToken != null) {
            headers.setBearerAuth(authToken);
        }
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        latestResponse = restTemplate.exchange("http://localhost:" + port + endpoint + "/" + createdTrainingId.toString(), HttpMethod.DELETE, entity, String.class);
    }

    @When("I send a DELETE request to {string} with ID {string}")
    public void iSendADeleteRequestToWithSpecificId(String endpoint, String trainingId) {
        HttpHeaders headers = new HttpHeaders();
        if (authToken != null) {
            headers.setBearerAuth(authToken);
        }
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        latestResponse = restTemplate.exchange(url() + endpoint + "/" + trainingId, HttpMethod.DELETE, entity, String.class);
    }

    @Then("the training deletion response status should be {int}")
    public void theTrainingDeletionResponseStatusShouldBe(int expectedStatus) {
        assertNotNull(latestResponse, "No response received from training deletion.");
        assertEquals(HttpStatus.valueOf(expectedStatus), latestResponse.getStatusCode());
    }

    @And("the training deletion response should contain message {string}")
    public void theTrainingDeletionResponseShouldContainMessage(String expectedMessage) {
        assertNotNull(latestResponse.getBody(), "Response body should not be null.");
        assertInstanceOf(String.class, latestResponse.getBody(), "Response body is not a String.");
        String responseBody = (String) latestResponse.getBody();
        assertTrue(responseBody.contains(expectedMessage));
    }

    @And("the training deletion response should contain an error message {string}")
    public void theTrainingDeletionResponseShouldContainAnErrorMessage(String errorMessage) {
        assertNotNull(latestResponse.getBody(), "Response body should not be null for error.");
        assertInstanceOf(String.class, latestResponse.getBody(), "Response body is not a String for error.");
        String responseBody = (String) latestResponse.getBody();
        assertTrue(responseBody.contains(errorMessage), "Error message expected: '" + errorMessage + "', but found: " + responseBody);
    }

    @And("the response should contain error message {string}")
    public void theResponseShouldContainErrorMessage(String errorMessage) {
        assertNotNull(latestResponse.getBody(), "Response body should not be null for error.");
        assertInstanceOf(String.class, latestResponse.getBody(), "Response body is not a String for error.");
        String responseBody = (String) latestResponse.getBody();
        assertTrue(responseBody.contains(errorMessage), "Error message expected: '" + errorMessage + "', but found: " + responseBody);
    }

}