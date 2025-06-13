package epam.client.service;

import epam.client.dto.TrainerWorkloadSummaryResponseDTO;
import epam.client.messaging.consumer.TrainerWorkloadSummaryMessageConsumer;
import epam.client.messaging.producer.TrainerMessageProducer;
import epam.client.service.impl.TrainerWorkloadMessagePropertiesBuilder;
import epam.client.service.impl.TrainerWorkloadServiceImpl;
import epam.entity.Trainer;
import epam.entity.Training;
import epam.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainerWorkloadServiceTest {

    @Mock
    private TrainerMessageProducer trainerMessageProducer;
    @Mock
    private TrainerWorkloadSummaryMessageConsumer trainerWorkloadSummaryMessageConsumer;
    @Mock
    private TrainerWorkloadMessagePropertiesBuilder trainerWorkloadUrlBuilder;
    @InjectMocks
    private TrainerWorkloadServiceImpl trainerWorkloadService;

    private Training training;
    private Authentication connectedUser;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .username("john.doe")
                .firstname("John")
                .lastname("Doe")
                .isActive(true)
                .build();

        Trainer trainer = Trainer.builder()
                .user(user)
                .build();

        training = Training.builder()
                .trainingId(UUID.randomUUID())
                .trainer(trainer)
                .trainingName("Test Training")
                .trainingDate(LocalDateTime.now().plusDays(1))
                .trainingDuration(60)
                .build();

        userDetails = mock(UserDetails.class);
        lenient().when(userDetails.getUsername()).thenReturn("testuser");

        connectedUser = mock(Authentication.class);
        lenient().when(connectedUser.getPrincipal()).thenReturn(userDetails);
    }

    @Test
    void shouldReturnTrainerWorkloadSummarySuccessfully() {
        // Given
        Integer year = 2023;
        Integer month = 10;
        TrainerWorkloadSummaryResponseDTO expectedSummary = TrainerWorkloadSummaryResponseDTO.builder()
                .username("testuser")
                .workloadSummaryInYears(null)
                .build();

        when(trainerWorkloadSummaryMessageConsumer.getTrainerWorkloadSummary(eq("testuser"), eq(year), eq(month)))
                .thenReturn(expectedSummary);

        // When
        TrainerWorkloadSummaryResponseDTO actualSummary = trainerWorkloadService.getTrainerWorkloadSummary(year, month, connectedUser);

        // Then
        assertNotNull(actualSummary);
        assertEquals(expectedSummary.getUsername(), actualSummary.getUsername());
        verify(trainerWorkloadSummaryMessageConsumer).getTrainerWorkloadSummary("testuser", year, month);
    }

    @Test
    void shouldReturnNullWhenSummaryUsernameIsNull() {
        // Given
        Integer year = 2023;
        Integer month = 10;
        TrainerWorkloadSummaryResponseDTO summaryWithNullUsername = TrainerWorkloadSummaryResponseDTO.builder()
                .username(null)
                .workloadSummaryInYears(null)
                .build();

        when(trainerWorkloadSummaryMessageConsumer.getTrainerWorkloadSummary(eq("testuser"), eq(year), eq(month)))
                .thenReturn(summaryWithNullUsername);

        // When
        TrainerWorkloadSummaryResponseDTO actualSummary = trainerWorkloadService.getTrainerWorkloadSummary(year, month, connectedUser);

        // Then
        assertNull(actualSummary);
        verify(trainerWorkloadSummaryMessageConsumer).getTrainerWorkloadSummary("testuser", year, month);
    }

    @Test
    void shouldReturnNullWhenSummaryIsNull() {
        // Given
        Integer year = 2023;
        Integer month = 10;

        when(trainerWorkloadSummaryMessageConsumer.getTrainerWorkloadSummary(eq("testuser"), eq(year), eq(month)))
                .thenReturn(null);

        // When
        TrainerWorkloadSummaryResponseDTO actualSummary = trainerWorkloadService.getTrainerWorkloadSummary(year, month, connectedUser);

        // Then
        assertNull(actualSummary);
        verify(trainerWorkloadSummaryMessageConsumer).getTrainerWorkloadSummary("testuser", year, month);
    }

    @Test
    void shouldThrowExceptionWhenConsumerFailsOnGetSummary() {
        // Given
        Integer year = 2023;
        Integer month = 10;

        when(trainerWorkloadSummaryMessageConsumer.getTrainerWorkloadSummary(eq("testuser"), eq(year), eq(month)))
                .thenThrow(new RuntimeException("Consumer error"));

        // When / Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> trainerWorkloadService.getTrainerWorkloadSummary(year, month, connectedUser));
        assertEquals("Consumer error", exception.getMessage());
    }
}