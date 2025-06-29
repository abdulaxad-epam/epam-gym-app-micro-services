package epam.cucumber_test;

import epam.client.dto.TrainerWorkloadSummaryInMonthsResponseDTO;
import epam.client.dto.TrainerWorkloadSummaryInYearsResponseDTO;
import epam.client.dto.TrainerWorkloadSummaryResponseDTO;
import epam.client.messaging.consumer.TrainerWorkloadSummaryMessageConsumer;
import epam.dto.request_dto.AuthenticateRequestDTO;
import epam.dto.response_dto.AuthenticationResponseDTO;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.apache.activemq.command.ActiveMQBytesMessage;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TrainerWorkloadSteps {

    @LocalServerPort
    private int port;

    private static final TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private TrainerWorkloadSummaryMessageConsumer trainerWorkloadSummaryMessageConsumer;

    private String authToken;

    private ResponseEntity<TrainerWorkloadSummaryResponseDTO> workloadSummaryResponse;
    private ResponseEntity<String> workloadSummaryTextResponse;


    @Given("I am authenticated as a trainer user {string} with password {string}")
    public void iAmAuthenticatedAsATrainerUserWithPassword(String username, String password) {
        AuthenticateRequestDTO authRequest = AuthenticateRequestDTO.builder()
                .username(username)
                .password(password)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AuthenticateRequestDTO> request = new HttpEntity<>(authRequest, headers);

        ResponseEntity<AuthenticationResponseDTO> authResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/v1/auth/authenticate",
                request,
                AuthenticationResponseDTO.class
        );

        assertEquals(HttpStatus.OK, authResponse.getStatusCode(), "Authentication as trainer should be successful");
        assertNotNull(authResponse.getBody());
        assertNotNull(authResponse.getBody().getToken(), "Auth token for trainer should not be null");
        this.authToken = authResponse.getBody().getToken().getAccessToken();
    }

    @Given("the trainer workload summary for {string} year {int} month {int} is {int} minutes")
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


    @When("I send a GET request to {string} with year {int} and month {int} as authenticated trainer")
    public void iSendAGetRequestToWithYearAndMonthAsAuthenticatedTrainer(String endpoint, int year, int month) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = String.format("http://localhost:%d%s?year=%d&month=%d", port, endpoint, year, month);

        workloadSummaryResponse = restTemplate.exchange(url, HttpMethod.GET, entity, TrainerWorkloadSummaryResponseDTO.class);
    }

    @When("I send a GET request to {string} with year {int} and month {int} as authenticated trainer without workload summary")
    public void iSendAGetRequestToWithYearAndMonthAsAuthenticatedTrainerWithoutWorkload(String endpoint, int year, int month) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = String.format("http://localhost:%d%s?year=%d&month=%d", port, endpoint, year, month);

        workloadSummaryTextResponse = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }


    @When("I send a GET request to {string} with year {int} and month {int} without authentication")
    public void iSendAGetRequestToWithYearAndMonthWithoutAuthentication(String endpoint, int year, int month) {
        String url = String.format("http://localhost:%d%s?year=%d&month=%d", port, endpoint, year, month);
        workloadSummaryTextResponse = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
    }


    @Then("the workload summary response status should be {int}")
    public void theWorkloadSummaryResponseStatusShouldBe(int expectedStatusCode) {
        if (workloadSummaryResponse != null) {
            assertEquals(HttpStatus.valueOf(expectedStatusCode), workloadSummaryResponse.getStatusCode());
        } else if (workloadSummaryTextResponse != null) {
            assertEquals(HttpStatus.valueOf(expectedStatusCode), workloadSummaryTextResponse.getStatusCode());
        } else {
            throw new AssertionError("No response received for workload summary request.");
        }
    }

    @And("the workload summary should contain username {string}")
    public void theWorkloadSummaryShouldContainUsername(String expectedUsername) {
        assertNotNull(workloadSummaryResponse.getBody(), "Workload summary response body should not be null");
        assertEquals(expectedUsername, workloadSummaryResponse.getBody().getUsername());
    }

    @And("the workload summary for year {int} month {int} should be {int} minutes")
    public void theWorkloadSummaryForYearMonthShouldBeMinutes(int year, int month, int expectedDuration) {
        assertNotNull(workloadSummaryResponse.getBody(), "Workload summary response body should not be null");

        TrainerWorkloadSummaryInYearsResponseDTO yearSummary = workloadSummaryResponse.getBody().getWorkloadSummaryInYears().stream()
                .filter(ys -> String.valueOf(year).equals(ys.getYear())).findFirst().orElse(null);

        assertNotNull(yearSummary, "Year " + year + " summary not found");

        TrainerWorkloadSummaryInMonthsResponseDTO monthSummary = yearSummary.getWorkloadSummaryInMonths().stream()
                .filter(ms -> String.valueOf(month).equals(ms.getMonth())).findFirst().orElse(null);

        assertNotNull(monthSummary, "Month " + month + " summary not found for year " + year);
        assertEquals(String.valueOf(expectedDuration), monthSummary.getDurationInMinutes(), "Duration mismatch");
    }

    @And("the workload summary should response {string}")
    public void theWorkloadSummaryShouldBeNullOrNotFoundMessage(String arg0) {
        if (workloadSummaryResponse != null) {
            assertNull(workloadSummaryResponse.getBody(), "Workload summary response body should be null");
        } else if (workloadSummaryTextResponse != null) {
            assert workloadSummaryTextResponse.getBody() != null;
            assertTrue(workloadSummaryTextResponse.getBody().contains(arg0),
                    "Workload summary response body should be null or contain 'not found' message");
        } else {
            throw new AssertionError("No response captured.");
        }
    }

    @And("the workload summary should be a {string} error message")
    public void theWorkloadSummaryShouldBeAnErrorMessage(String errorMessage) {
        assertNotNull(workloadSummaryTextResponse);
        assertNotNull(workloadSummaryTextResponse.getBody());
        assertTrue(workloadSummaryTextResponse.getBody().contains(errorMessage));
    }
}
