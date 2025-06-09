package epam.service;

import epam.dto.TrainerWorkloadSummaryResponseDTO;
import epam.entity.TrainerWorkload;
import epam.service.impl.TrainerWorkloadSummaryServiceImpl;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
public class TrainerWorkloadSummaryServiceTest {

    @Mock
    private TrainerWorkloadService trainerWorkloadService;

    @InjectMocks
    private TrainerWorkloadSummaryServiceImpl trainerWorkloadSummaryService;

    private List<TrainerWorkload> workloadList;

    @BeforeEach
    void setUp() {
        TrainerWorkload trainerWorkload1 = new TrainerWorkload();
        trainerWorkload1.setTrainerFirstName("John");
        trainerWorkload1.setTrainerLastName("Doe");
        trainerWorkload1.setTrainerUsername("john_doe");
        trainerWorkload1.setIsActive(true);
        trainerWorkload1.setTrainingDate(LocalDate.parse("2025-03-15"));
        trainerWorkload1.setTrainingDuration(5);

        TrainerWorkload trainerWorkload2 = new TrainerWorkload();
        trainerWorkload2.setTrainerFirstName("John");
        trainerWorkload2.setTrainerLastName("Doe");
        trainerWorkload2.setTrainerUsername("john_doe");
        trainerWorkload2.setIsActive(true);
        trainerWorkload2.setTrainingDate(LocalDate.parse("2025-03-25"));
        trainerWorkload2.setTrainingDuration(3);

        workloadList = List.of(trainerWorkload1, trainerWorkload2);
    }

    @Test
    void testGetTrainerWorkloadSummary_NormalCase() {
        // Given
        String trainerUsername = "john_doe";
        Integer year = 2025;
        Integer month = 3;

        when(trainerWorkloadService.getTrainerWorkload(trainerUsername, year, month)).thenReturn(workloadList);

        // When
        TrainerWorkloadSummaryResponseDTO result = trainerWorkloadSummaryService.getTrainerWorkloadSummary(trainerUsername, year, month);

        // Then
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john_doe", result.getUsername());
        assertTrue(result.getStatus());
        assertNotNull(result.getWorkloadSummaryInYears());
        assertEquals(1, result.getWorkloadSummaryInYears().size());
        assertEquals("2025", result.getWorkloadSummaryInYears().get(0).getYear());
    }

    @Test
    void testGetTrainerWorkloadSummary_EmptyWorkload() {
        String trainerUsername = "john_doe";
        Integer year = 2025;
        Integer month = 6;

        when(trainerWorkloadService.getTrainerWorkload(trainerUsername, year, month)).thenReturn(List.of());

        TrainerWorkloadSummaryResponseDTO result = trainerWorkloadSummaryService.getTrainerWorkloadSummary(trainerUsername, year, month);

        assertNotNull(result);
        assertNull(result.getWorkloadSummaryInYears());
    }

    @Test
    void testGetTrainerWorkloadSummary_SingleWorkload() {
        // Given
        String trainerUsername = "john_doe";
        Integer year = 2025;
        Integer month = 3;

        TrainerWorkload singleWorkload = workloadList.get(0);

        when(trainerWorkloadService.getTrainerWorkload(trainerUsername, year, month)).thenReturn(Collections.singletonList(singleWorkload));

        // When
        TrainerWorkloadSummaryResponseDTO result = trainerWorkloadSummaryService.getTrainerWorkloadSummary(trainerUsername, year, month);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getWorkloadSummaryInYears().get(0).getWorkloadSummaryInMonths().size());
    }

    @Test
    void testGetTrainerWorkloadSummary_MultipleWorkloadEntries() {
        // Given
        String trainerUsername = "john_doe";
        Integer year = 2025;
        Integer month = 3;

        when(trainerWorkloadService.getTrainerWorkload(trainerUsername, year, month)).thenReturn(workloadList);

        // When
        TrainerWorkloadSummaryResponseDTO result = trainerWorkloadSummaryService.getTrainerWorkloadSummary(trainerUsername, year, month);

        // Then
        assertEquals(1, result.getWorkloadSummaryInYears().get(0).getWorkloadSummaryInMonths().size());
    }

    @Test
    void testGetTrainerWorkloadSummary_ActiveAndInactiveTrainer() {

        // Test when trainer is active
        TrainerWorkload activeTrainerWorkload = Instancio.of(TrainerWorkload.class)
                .set(field(TrainerWorkload::getIsActive), true)
                .set(field(TrainerWorkload::getTrainingDate), LocalDate.parse("2025-03-15"))
                .create();
        when(trainerWorkloadService.getTrainerWorkload("john_doe", 2025, 3)).thenReturn(List.of(activeTrainerWorkload));

        TrainerWorkloadSummaryResponseDTO activeResult = trainerWorkloadSummaryService.getTrainerWorkloadSummary("john_doe", 2025, 3);
        assertTrue(activeResult.getStatus());

        // Test when trainer is inactive
        TrainerWorkload inactiveTrainerWorkload = Instancio.of(TrainerWorkload.class)
                .set(field(TrainerWorkload::getIsActive), false)
                .set(field(TrainerWorkload::getTrainingDate), LocalDate.parse("2025-03-15"))
                .create();
        when(trainerWorkloadService.getTrainerWorkload("john_doe", 2025, 3)).thenReturn(List.of(inactiveTrainerWorkload));

        TrainerWorkloadSummaryResponseDTO inactiveResult = trainerWorkloadSummaryService.getTrainerWorkloadSummary("john_doe", 2025, 3);
        assertFalse(inactiveResult.getStatus());
    }
}
