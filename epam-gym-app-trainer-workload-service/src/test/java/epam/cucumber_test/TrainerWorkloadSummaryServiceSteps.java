package epam.cucumber_test;

import epam.dto.TrainerWorkloadResponseDTO;
import epam.dto.TrainerWorkloadSummaryResponseDTO;
import epam.messaging.producer.TrainerActionProducer;
import epam.service.impl.TrainerWorkloadMessagePropertiesBuilder;
import epam.service.impl.TrainerWorkloadSummaryServiceImpl;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.jms.core.MessagePostProcessor;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TrainerWorkloadSummaryServiceSteps {

    @Mock
    private TrainerActionProducer trainerActionProducer;

    @Mock
    private TrainerWorkloadMessagePropertiesBuilder propertiesBuilder;

    @Mock
    private MessagePostProcessor mockMessagePostProcessor;

    private TrainerWorkloadSummaryServiceImpl trainerWorkloadSummaryService;

    private TrainerWorkloadResponseDTO inputResponseDTO;
    private boolean inputStatus;

    @Before
    public void setupScenario() {
        MockitoAnnotations.openMocks(this);

        trainerWorkloadSummaryService = new TrainerWorkloadSummaryServiceImpl(trainerActionProducer, propertiesBuilder);

        inputResponseDTO = null;
        inputStatus = false;

        when(propertiesBuilder.buildMessagePropertyOnProduce(any(Integer.class), any(Integer.class)))
                .thenReturn(mockMessagePostProcessor);

        Mockito.reset(trainerActionProducer);
    }

    @Given("a TrainerWorkloadResponseDTO for trainer {string} with first name {string}, last name {string}, training date {string}, and duration {int}")
    public void aTrainerWorkloadResponseDTO(String username, String firstName, String lastName, String dateStr, int duration) {
        inputResponseDTO = TrainerWorkloadResponseDTO.builder()
                .trainerUsername(username)
                .trainerFirstName(firstName)
                .trainerLastName(lastName)
                .trainingDate(LocalDate.parse(dateStr))
                .trainingDurationInMinutes(duration)
                .build();
    }

    @And("the active status is {string}")
    public void theActiveStatusIs(String status) {
        inputStatus = Boolean.parseBoolean(status);
    }


    @When("the produce method is called")
    public void theProduceMethodIsCalled() {
        assertNotNull(inputResponseDTO, "Input DTO must be set before calling produce method.");
        trainerWorkloadSummaryService.produce(inputResponseDTO, inputStatus);
    }


    @Then("the properties builder should be called with year {int} and month {int}")
    public void thePropertiesBuilderShouldBeCalled(int expectedYear, int expectedMonth) {
        verify(propertiesBuilder, times(1)).buildMessagePropertyOnProduce(expectedYear, expectedMonth);
    }

    @And("the trainer action producer should produce a message")
    public void theTrainerActionProducerShouldProduceAMessage() {
        verify(trainerActionProducer, times(1)).produceOnAction(eq(mockMessagePostProcessor), any(TrainerWorkloadSummaryResponseDTO.class));
    }

    @And("the produced message should contain username {string}, first name {string}, last name {string}, status {string}, year {int}, month {int}, and duration {int}")
    public void theProducedMessageShouldContainCorrectData(String expectedUsername, String expectedFirstName, String expectedLastName,
                                                           String expectedStatus, int expectedYear, int expectedMonth, int expectedDuration) {
        ArgumentCaptor<TrainerWorkloadSummaryResponseDTO> captor = ArgumentCaptor.forClass(TrainerWorkloadSummaryResponseDTO.class);
        verify(trainerActionProducer).produceOnAction(eq(mockMessagePostProcessor), captor.capture());

        TrainerWorkloadSummaryResponseDTO capturedDTO = captor.getValue();
        assertNotNull(capturedDTO, "Captured DTO should not be null");
        assertEquals(expectedUsername, capturedDTO.getUsername());
        assertEquals(expectedFirstName, capturedDTO.getFirstName());
        assertEquals(expectedLastName, capturedDTO.getLastName());
        assertEquals(Boolean.parseBoolean(expectedStatus), capturedDTO.getStatus());

        assertNotNull(capturedDTO.getWorkloadSummaryInYears(), "Workload summary in years should not be null");
        assertEquals(1, capturedDTO.getWorkloadSummaryInYears().size());
        assertEquals(expectedYear, capturedDTO.getWorkloadSummaryInYears().get(0).getYear());

        assertNotNull(capturedDTO.getWorkloadSummaryInYears().get(0).getWorkloadSummaryInMonths(), "Workload summary in months should not be null");
        assertEquals(1, capturedDTO.getWorkloadSummaryInYears().get(0).getWorkloadSummaryInMonths().size());
        assertEquals(expectedMonth, capturedDTO.getWorkloadSummaryInYears().get(0).getWorkloadSummaryInMonths().get(0).getMonth());
        assertEquals(expectedDuration, capturedDTO.getWorkloadSummaryInYears().get(0).getWorkloadSummaryInMonths().get(0).getDurationInMinutes());
    }
}