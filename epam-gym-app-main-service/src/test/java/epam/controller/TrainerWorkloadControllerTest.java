package epam.controller;

import epam.client.dto.TrainerWorkloadSummaryResponseDTO;
import epam.client.service.TrainerWorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class TrainerWorkloadControllerTest {

    private TrainerWorkloadController trainerWorkloadController;

    @Mock
    private TrainerWorkloadService trainerWorkloadService;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        trainerWorkloadController = new TrainerWorkloadController(trainerWorkloadService);
    }

    @Test
    void testGetTrainerWorkloadSummary_ReturnsData() {
        // Given
        int year = 2024;
        int month = 5;
        TrainerWorkloadSummaryResponseDTO expectedDto = new TrainerWorkloadSummaryResponseDTO();
        when(trainerWorkloadService.getTrainerWorkloadSummary(year, month, authentication))
                .thenReturn(expectedDto);

        // When
        ResponseEntity<?> response = trainerWorkloadController.getTrainerWorkloadSummary(year, month, authentication);

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedDto, response.getBody());
    }

    @Test
    void testGetTrainerWorkloadSummary_ReturnsMessageWhenNull() {
        // Given
        int year = 2024;
        int month = 5;
        when(trainerWorkloadService.getTrainerWorkloadSummary(year, month, authentication))
                .thenReturn(null);

        // When
        ResponseEntity<?> response = trainerWorkloadController.getTrainerWorkloadSummary(year, month, authentication);

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Trainer workload summary not found", response.getBody());
    }
}
