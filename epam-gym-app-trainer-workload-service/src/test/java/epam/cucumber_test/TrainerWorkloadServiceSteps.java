package epam.cucumber_test;

import epam.dto.TrainerWorkloadRequestDTO;
import epam.dto.TrainerWorkloadResponseDTO;
import epam.entity.DailyWorkload;
import epam.entity.MonthlyWorkload;
import epam.entity.TrainerWorkload;
import epam.entity.YearlyWorkload;
import epam.exception.DailyTrainingDurationExceededException;
import epam.exception.TrainerWorkloadNotFoundException;
import epam.repostiory.TrainerWorkloadRepository;
import epam.service.TrainerWorkloadSummaryService;
import epam.service.impl.TrainerWorkloadServiceImpl;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TrainerWorkloadServiceSteps {

    private static final Logger log = LoggerFactory.getLogger(TrainerWorkloadServiceSteps.class);

    @Mock
    private TrainerWorkloadRepository trainerWorkloadRepository;

    @Mock
    private TrainerWorkloadSummaryService trainerWorkloadSummaryService;

    private TrainerWorkloadServiceImpl trainerWorkloadService;

    private TrainerWorkloadRequestDTO currentRequestDTO;
    private TrainerWorkloadResponseDTO serviceResponseDTO;
    private Exception caughtException;

    private static final Integer TEST_MAX_DAILY_WORKING_HOURS = 480;

    private static final UUID predefinedId = UUID.randomUUID();

    @Before
    public void setupScenario() {
        MockitoAnnotations.openMocks(this);

        trainerWorkloadService = new TrainerWorkloadServiceImpl(trainerWorkloadRepository, trainerWorkloadSummaryService);

        ReflectionTestUtils.setField(trainerWorkloadService, "maxDailyWorkingHours", TEST_MAX_DAILY_WORKING_HOURS);

        currentRequestDTO = null;
        serviceResponseDTO = null;
        caughtException = null;

        Mockito.reset(trainerWorkloadRepository, trainerWorkloadSummaryService);
    }


    @Given("a TrainerWorkloadRequestDTO for trainer {string} with first name {string}, last name {string}, active status {string}, date {string}, duration {int}, and action type {string}")
    public void aTrainerWorkloadRequestDTOForTrainer(String username, String firstName, String lastName, String isActive, String dateStr, int duration, String actionType) {
        currentRequestDTO = TrainerWorkloadRequestDTO.builder()
                .trainerUsername(username)
                .trainerFirstName(firstName)
                .trainerLastName(lastName)
                .isActive(Boolean.valueOf(isActive))
                .trainingDate(LocalDate.parse(dateStr))
                .trainingDurationInMinutes(duration)
                .actionType(actionType)
                .build();


        when(trainerWorkloadRepository.save(any(TrainerWorkload.class)))
                .thenAnswer(invocation -> {
                    TrainerWorkload savedWorkload = invocation.getArgument(0);
                    if (savedWorkload.getId() == null) {
                        savedWorkload.setId(String.valueOf(predefinedId));
                    }
                    return savedWorkload;
                });

    }


    @Given("an existing TrainerWorkload for trainer {string} with initial daily duration {int} for date {string}")
    public void anExistingTrainerWorkloadForTrainerWithInitialDailyDurationForDate(String username, int initialDailyDuration, String dateStr) {
        LocalDate trainingDate = LocalDate.parse(dateStr);

        DailyWorkload initialDailyWorkload = DailyWorkload.builder()
                .day(trainingDate.getDayOfMonth())
                .dailyTrainingDuration(initialDailyDuration)
                .build();

        MonthlyWorkload initialMonthlyWorkload = MonthlyWorkload.builder()
                .month(trainingDate.getMonthValue())
                .monthlyTrainingDuration(initialDailyDuration)
                .days(new ArrayList<>(Collections.singletonList(initialDailyWorkload)))
                .build();

        YearlyWorkload initialYearlyWorkload = YearlyWorkload.builder()
                .year(trainingDate.getYear())
                .months(new ArrayList<>(Collections.singletonList(initialMonthlyWorkload)))
                .build();

        TrainerWorkload existingWorkload = TrainerWorkload.builder()
                .id(String.valueOf(predefinedId))
                .trainerUsername(username)
                .trainerFirstName("Robert")
                .trainerLastName("Brown")
                .isActive(true)
                .years(new ArrayList<>(Collections.singletonList(initialYearlyWorkload)))
                .build();

        when(trainerWorkloadRepository.findTrainerWorkloadByTrainerUsername(username))
                .thenReturn(existingWorkload);

        when(trainerWorkloadRepository.save(any(TrainerWorkload.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }


    @When("the actionOn method is called with the request DTO")
    public void theActionOnMethodIsCalledWithTheRequestDTO() {
        try {
            serviceResponseDTO = trainerWorkloadService.actionOn(currentRequestDTO);
        } catch (Exception e) {
            caughtException = e;
        }
    }


    @Then("a TrainerWorkloadResponseDTO should be returned")
    public void aTrainerWorkloadResponseDTOShouldBeReturned() {
        assertNotNull(serviceResponseDTO, "Service should return a response DTO");
        assertNull(caughtException, "No exception should have been caught");
    }

    @Then("a TrainerWorkloadResponseDTO should be null")
    public void aTrainerWorkloadResponseDTOShouldBeNull() {
        assertNull(serviceResponseDTO, "Service should return null");
    }

    @And("the response DTO's username should be {string}")
    public void theResponseDTOSUsernameShouldBe(String expectedUsername) {
        assertEquals(expectedUsername, serviceResponseDTO.getTrainerUsername());
    }

    @And("the response DTO's training date should be {string}")
    public void theResponseDTOSTrainingDateShouldBe(String expectedDateStr) {
        assertEquals(LocalDate.parse(expectedDateStr), serviceResponseDTO.getTrainingDate());
    }

    @And("the response DTO's training duration should be {int} minutes")
    public void theResponseDTOSDurationShouldBe(int expectedDuration) {
        assertEquals(expectedDuration, serviceResponseDTO.getTrainingDurationInMinutes());
    }

    @And("the trainer workload repository save method should be called")
    public void theTrainerWorkloadRepositorySaveMethodShouldBeCalled() {
        verify(trainerWorkloadRepository, times(1)).save(any(TrainerWorkload.class));
    }

    @And("the trainer workload repository save method should not be called")
    public void theTrainerWorkloadRepositorySaveMethodShouldNotBeCalled() {
        verify(trainerWorkloadRepository, never()).save(any(TrainerWorkload.class));
    }

    @And("a new TrainerWorkload entity should have been saved with username {string} and daily duration {int} for date {string}")
    public void aNewTrainerWorkloadEntityShouldHaveBeenSaved(String username, int expectedDailyDuration, String dateStr) {
        ArgumentCaptor<TrainerWorkload> captor = ArgumentCaptor.forClass(TrainerWorkload.class);
        verify(trainerWorkloadRepository).save(captor.capture());
        TrainerWorkload savedWorkload = captor.getValue();

        assertNotNull(savedWorkload.getId(), "Saved workload should have an ID");
        assertEquals(username, savedWorkload.getTrainerUsername());
        assertEquals(savedWorkload.getIsActive(), currentRequestDTO.getIsActive(), "isActive status mismatch");

        LocalDate trainingDate = LocalDate.parse(dateStr);
        assertEquals(expectedDailyDuration, savedWorkload.getYears().get(0).getMonths().get(0).getDays().get(0).getDailyTrainingDuration());
        assertEquals(trainingDate.getYear(), savedWorkload.getYears().get(0).getYear());
        assertEquals(trainingDate.getMonthValue(), savedWorkload.getYears().get(0).getMonths().get(0).getMonth());
        assertEquals(trainingDate.getDayOfMonth(), savedWorkload.getYears().get(0).getMonths().get(0).getDays().get(0).getDay());
    }

    @And("the existing TrainerWorkload entity for username {string} should be updated with daily duration {int} for date {string}")
    public void theExistingTrainerWorkloadEntityShouldBeUpdated(String username, int expectedDailyDuration, String dateStr) {
        ArgumentCaptor<TrainerWorkload> captor = ArgumentCaptor.forClass(TrainerWorkload.class);
        verify(trainerWorkloadRepository).save(captor.capture());
        TrainerWorkload updatedWorkload = captor.getValue();

        assertEquals(username, updatedWorkload.getTrainerUsername());

        assertEquals(expectedDailyDuration, updatedWorkload.getYears().get(0).getMonths().get(0).getDays().get(0).getDailyTrainingDuration());
    }


    @And("the trainer workload summary service produce method should be called with active status {string}")
    public void theTrainerWorkloadSummaryServiceProduceMethodShouldBeCalled(String expectedIsActive) {
        ArgumentCaptor<TrainerWorkloadResponseDTO> responseCaptor = ArgumentCaptor.forClass(TrainerWorkloadResponseDTO.class);
        ArgumentCaptor<Boolean> isActiveCaptor = ArgumentCaptor.forClass(Boolean.class);
        verify(trainerWorkloadSummaryService, times(1)).produce(responseCaptor.capture(), isActiveCaptor.capture());

        assertNotNull(responseCaptor.getValue());
        assertEquals(expectedIsActive, isActiveCaptor.getValue().toString());
    }

    @And("the trainer workload summary service produce method should not be called")
    public void theTrainerWorkloadSummaryServiceProduceMethodShouldNotBeCalled() {
        verify(trainerWorkloadSummaryService, never()).produce(any(TrainerWorkloadResponseDTO.class), anyBoolean());
    }

    @Then("a DailyTrainingDurationExceededException should be thrown")
    public void aDailyTrainingDurationExceededExceptionShouldBeThrown() {
        log.info(currentRequestDTO.toString());
        assertNotNull(caughtException, "An exception should have been thrown");
        assertInstanceOf(DailyTrainingDurationExceededException.class, caughtException, "Expected DailyTrainingDurationExceededException");
        assertEquals("Training duration cannot be more than 8 hours (480 minutes) for a day", caughtException.getMessage());
    }

    @Then("a TrainerWorkloadNotFoundException should be thrown")
    public void aTrainerWorkloadNotFoundExceptionShouldBeThrown() {
        assertNotNull(caughtException, "An exception should have been thrown");
        assertInstanceOf(TrainerWorkloadNotFoundException.class, caughtException, "Expected TrainerWorkloadNotFoundException");
        assertTrue(caughtException.getMessage().contains("Trainer workload associated to username " + currentRequestDTO.getTrainerUsername() + " does not exist."));
    }


    @Then("on response should throw exception DailyTrainingDurationExceededException")
    public void onResponseShouldThrowExceptionDailyTrainingDurationExceededException() {
        assertNotNull(caughtException, "An exception should have been thrown");
        assertInstanceOf(DailyTrainingDurationExceededException.class, caughtException, "Expected DailyTrainingDurationExceededException");

    }

    @And("on message should include {string}")
    public void onMessageShouldInclude(String arg0) {
        assertTrue(caughtException.getMessage().contains(arg0));

    }
}