package epam.controller;


import epam.dto.TrainerWorkloadSummaryInMonthsResponseDTO;
import epam.dto.TrainerWorkloadSummaryInYearsResponseDTO;
import epam.dto.TrainerWorkloadSummaryResponseDTO;
import epam.service.TrainerWorkloadSummaryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainerWorkloadSummaryControllerTest {

    @Mock
    private TrainerWorkloadSummaryService trainerWorkloadSummaryService;

    @InjectMocks
    private TrainerWorkloadSummaryController trainerWorkloadSummaryController;

    @Test
    void getTrainerWorkloadSummary_Success() {
        String trainerUsername = "john.doe";
        Integer year = 2023;
        Integer month = 10;

        TrainerWorkloadSummaryInMonthsResponseDTO monthSummary = TrainerWorkloadSummaryInMonthsResponseDTO.builder()
                .month("October")
                .durationInMinutes("120")
                .build();
        TrainerWorkloadSummaryInYearsResponseDTO yearSummary = TrainerWorkloadSummaryInYearsResponseDTO.builder()
                .year("2023")
                .workloadSummaryInMonths(List.of(monthSummary))
                .build();
        TrainerWorkloadSummaryResponseDTO expectedResponseDTO = TrainerWorkloadSummaryResponseDTO.builder()
                .username(trainerUsername)
                .firstName("John")
                .lastName("Doe")
                .status(true)
                .workloadSummaryInYears(List.of(yearSummary))
                .build();

        when(trainerWorkloadSummaryService.getTrainerWorkloadSummary(trainerUsername, year, month))
                .thenReturn(expectedResponseDTO);

        ResponseEntity<TrainerWorkloadSummaryResponseDTO> responseEntity =
                trainerWorkloadSummaryController.getTrainerWorkloadSummary(trainerUsername, year, month);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals(expectedResponseDTO, responseEntity.getBody());

        verify(trainerWorkloadSummaryService, times(1))
                .getTrainerWorkloadSummary(trainerUsername, year, month);
    }

    @Test
    void getTrainerWorkloadSummary_ServiceReturnsNull() {
        String trainerUsername = "nonexistent.trainer";
        Integer year = 2023;
        Integer month = 10;

        when(trainerWorkloadSummaryService.getTrainerWorkloadSummary(trainerUsername, year, month))
                .thenReturn(null);

        ResponseEntity<TrainerWorkloadSummaryResponseDTO> responseEntity =
                trainerWorkloadSummaryController.getTrainerWorkloadSummary(trainerUsername, year, month);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCode().value());
        assertNull(responseEntity.getBody());

        verify(trainerWorkloadSummaryService, times(1))
                .getTrainerWorkloadSummary(trainerUsername, year, month);
    }

    @Test
    void getTrainerWorkloadSummary_EmptyWorkloadData() {
        String trainerUsername = "empty.workload";
        Integer year = 2024;
        Integer month = 1;

        TrainerWorkloadSummaryResponseDTO emptyWorkloadDTO = TrainerWorkloadSummaryResponseDTO.builder()
                .username(trainerUsername)
                .firstName("Empty")
                .lastName("User")
                .status(true)
                .workloadSummaryInYears(Collections.emptyList())
                .build();

        when(trainerWorkloadSummaryService.getTrainerWorkloadSummary(trainerUsername, year, month))
                .thenReturn(emptyWorkloadDTO);

        ResponseEntity<TrainerWorkloadSummaryResponseDTO> responseEntity =
                trainerWorkloadSummaryController.getTrainerWorkloadSummary(trainerUsername, year, month);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals(emptyWorkloadDTO, responseEntity.getBody());
        assertNotNull(responseEntity.getBody());
        assertNotNull(responseEntity.getBody().getWorkloadSummaryInYears());
        assertEquals(0, responseEntity.getBody().getWorkloadSummaryInYears().size());

        verify(trainerWorkloadSummaryService, times(1))
                .getTrainerWorkloadSummary(trainerUsername, year, month);
    }
}