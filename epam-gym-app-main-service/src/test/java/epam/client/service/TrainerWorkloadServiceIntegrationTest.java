package epam.client.service;

import epam.client.dto.TrainerWorkloadRequestDTO;
import epam.client.dto.TrainerWorkloadResponseDTO;
import epam.client.security.InternalJwtService;
import epam.client.service.impl.TrainerWorkloadUrlBuilder;
import epam.entity.Trainer;
import epam.entity.Training;
import epam.entity.User;
import epam.enums.ActionType;
import epam.exception.exception.TrainerWorkloadIsUnavailableException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("dev")
public class TrainerWorkloadServiceIntegrationTest {

    @Autowired
    private TrainerWorkloadService trainerWorkloadService;

    @MockBean
    private TrainerRestClientService trainerRestClientService;

    @MockBean
    private InternalJwtService internalJwtService;

    @MockBean
    private TrainerWorkloadUrlBuilder trainerWorkloadUrlBuilder;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    private static final String TRAINER_WORKLOAD_SERVICE_CB_NAME = "trainerWorkloadService";

    @BeforeEach
    void setUp() {
        reset(trainerRestClientService, internalJwtService, trainerWorkloadUrlBuilder);

        circuitBreakerRegistry.circuitBreaker(TRAINER_WORKLOAD_SERVICE_CB_NAME);
        circuitBreakerRegistry.circuitBreaker(TRAINER_WORKLOAD_SERVICE_CB_NAME).reset();

        when(internalJwtService.generateServiceToken(anyString(), anyString())).thenReturn("mock-jwt-token");
        when(trainerWorkloadUrlBuilder.buildEndpointUrl(anyString())).thenReturn("http://mock-workload-api/action");
    }


    private Training createMockTraining(String username, String firstName, String lastName, boolean isActive, LocalDateTime trainingDate, int trainingDuration) {
        User user = mock(User.class);
        when(user.getUsername()).thenReturn(username);
        when(user.getFirstname()).thenReturn(firstName);
        when(user.getLastname()).thenReturn(lastName);
        when(user.getIsActive()).thenReturn(isActive);

        Trainer trainer = mock(Trainer.class);
        when(trainer.getUser()).thenReturn(user);

        Training training = mock(Training.class);
        when(training.getTrainer()).thenReturn(trainer);
        when(training.getTrainingDate()).thenReturn(trainingDate);
        when(training.getTrainingDuration()).thenReturn(trainingDuration);
        when(training.getTrainingId()).thenReturn(UUID.randomUUID());

        return training;
    }

    @Test
    void testActionOnADD_Success() {
        Training mockTraining = createMockTraining(
                "test.trainer", "Test", "Trainer", true,
                LocalDateTime.of(2023, 1, 15, 10, 0), 90
        );

        TrainerWorkloadResponseDTO expectedResponse = new TrainerWorkloadResponseDTO("test.trainer", "Trainer workload added successfully", "brown", LocalDate.now(), 60);

        when(trainerRestClientService.postTrainingAction(
                anyString(), any(TrainerWorkloadRequestDTO.class), anyString()))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        TrainerWorkloadResponseDTO actualResponse = trainerWorkloadService.actionOnADD(mockTraining);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);

        verify(internalJwtService, times(1)).generateServiceToken(anyString(), eq("trainer_workload:write"));
        verify(trainerWorkloadUrlBuilder, times(1)).buildEndpointUrl(eq("/action"));

        verify(trainerRestClientService, times(1)).postTrainingAction(
                eq("http://mock-workload-api/action"),
                argThat(dto ->
                        dto.getTrainerUsername().equals("test.trainer") &&
                                dto.getTrainerFirstName().equals("Test") &&
                                dto.getTrainerLastName().equals("Trainer") &&
                                dto.getTrainingDate().equals(LocalDate.of(2023, 1, 15)) &&
                                dto.getTrainingDuration().equals(90) &&
                                dto.getIsActive().equals(true) &&
                                dto.getActionType().equals(ActionType.ADD.name())
                ),
                eq("mock-jwt-token")
        );
    }

    @Test
    void testActionOnDELETE_Success() {
        Training mockTraining = createMockTraining(
                "del.trainer", "Delete", "User", true,
                LocalDateTime.of(2023, 2, 10, 14, 30), 60
        );

        TrainerWorkloadResponseDTO expectedResponse = new TrainerWorkloadResponseDTO("del.trainer", "Trainer workload deleted successfully", "brown", LocalDate.now(), 60);
        when(trainerRestClientService.postTrainingAction(
                anyString(), any(TrainerWorkloadRequestDTO.class), anyString()))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        TrainerWorkloadResponseDTO actualResponse = trainerWorkloadService.actionOnDELETE(mockTraining);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);

        verify(internalJwtService, times(1)).generateServiceToken(anyString(), eq("trainer_workload:write"));
        verify(trainerWorkloadUrlBuilder, times(1)).buildEndpointUrl(eq("/action"));

        verify(trainerRestClientService, times(1)).postTrainingAction(
                eq("http://mock-workload-api/action"),
                argThat(dto ->
                        dto.getTrainerUsername().equals("del.trainer") &&
                                dto.getTrainerFirstName().equals("Delete") &&
                                dto.getTrainerLastName().equals("User") &&
                                dto.getTrainingDate().equals(LocalDate.of(2023, 2, 10)) &&
                                dto.getTrainingDuration().equals(60) &&
                                dto.getIsActive().equals(true) &&
                                dto.getActionType().equals(ActionType.DELETE.name())
                ),
                eq("mock-jwt-token")
        );
    }

    @Test
    void testActionOnADD_FallbackTriggered() {
        Training mockTraining = createMockTraining(
                "fallback.trainer", "Fallback", "Test", true,
                LocalDateTime.of(2023, 3, 20, 9, 0), 45
        );

        when(trainerRestClientService.postTrainingAction(
                anyString(), any(TrainerWorkloadRequestDTO.class), anyString()))
                .thenThrow(new RuntimeException("Simulated network error"));

        TrainerWorkloadIsUnavailableException thrown = assertThrows(
                TrainerWorkloadIsUnavailableException.class,
                () -> trainerWorkloadService.actionOnADD(mockTraining)
        );

        assertEquals("Trainer workload action services currently unavailable", thrown.getMessage());

        verify(trainerRestClientService, times(1)).postTrainingAction(
                anyString(), any(TrainerWorkloadRequestDTO.class), anyString());
    }

    @Test
    void testActionOnDELETE_FallbackTriggered() {
        Training mockTraining = createMockTraining(
                "fallback.del", "Fallback", "Delete", true,
                LocalDateTime.of(2023, 4, 5, 11, 0), 75
        );

        when(trainerRestClientService.postTrainingAction(
                anyString(), any(TrainerWorkloadRequestDTO.class), anyString()))
                .thenThrow(new RuntimeException("Simulated service outage"));

        TrainerWorkloadIsUnavailableException thrown = assertThrows(
                TrainerWorkloadIsUnavailableException.class,
                () -> trainerWorkloadService.actionOnDELETE(mockTraining)
        );

        assertEquals("Trainer workload action services currently unavailable", thrown.getMessage());

        verify(trainerRestClientService, times(1)).postTrainingAction(
                anyString(), any(TrainerWorkloadRequestDTO.class), anyString());
    }

    @Test
    void testActionOnADD_ReturnsNullBody() {
        Training mockTraining = createMockTraining(
                "null.body", "Null", "Body", true,
                LocalDateTime.of(2023, 5, 1, 10, 0), 30
        );

        when(trainerRestClientService.postTrainingAction(
                anyString(), any(TrainerWorkloadRequestDTO.class), anyString()))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        TrainerWorkloadResponseDTO actualResponse = trainerWorkloadService.actionOnADD(mockTraining);

        assertNull(actualResponse);
    }
}