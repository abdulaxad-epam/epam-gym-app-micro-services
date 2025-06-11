package epam.service;

import epam.dto.TrainerWorkloadRequestDTO;
import epam.dto.TrainerWorkloadResponseDTO;
import epam.entity.TrainerWorkload;
import epam.enums.ActionType;
import epam.exception.TrainerWorkloadNotFoundException;
import epam.mapper.TrainerWorkloadMapper;
import epam.repostiory.TrainerWorkloadRepository;
import epam.service.impl.TrainerWorkloadServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainerWorkloadServiceTest {

    @Mock
    private TrainerWorkloadRepository trainerWorkloadRepository;

    @Mock
    private TrainerWorkloadMapper trainerWorkloadMapper;

    @Mock
    private TrainerWorkloadSummaryService trainerWorkloadSummaryService;

    @InjectMocks
    private TrainerWorkloadServiceImpl trainerWorkloadService;

    private TrainerWorkloadRequestDTO addRequestDTO;
    private TrainerWorkloadRequestDTO deleteRequestDTO;
    private TrainerWorkload existingTrainerWorkload;
    private TrainerWorkload newTrainerWorkload;
    private TrainerWorkloadResponseDTO expectedResponseDTO;

    @BeforeEach
    void setUp() {
        addRequestDTO = TrainerWorkloadRequestDTO.builder()
                .trainerUsername("john.doe")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .isActive(true)
                .trainingDate(LocalDate.of(2024, 6, 15))
                .trainingDuration(60)
                .actionType(ActionType.ADD.name())
                .build();

        deleteRequestDTO = TrainerWorkloadRequestDTO.builder()
                .trainerUsername("john.doe")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .isActive(true)
                .trainingDate(LocalDate.of(2024, 6, 15))
                .trainingDuration(30)
                .actionType(ActionType.DELETE.name())
                .build();

        existingTrainerWorkload = TrainerWorkload.builder()
                .trainerWorkloadId(UUID.randomUUID())
                .trainerUsername("john.doe")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .isActive(true)
                .trainingDate(LocalDate.of(2024, 6, 1))
                .trainingDuration(120)
                .build();

        newTrainerWorkload = TrainerWorkload.builder()
                .trainerUsername("john.doe")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .isActive(true)
                .trainingDate(LocalDate.of(2024, 6, 1))
                .trainingDuration(60)
                .build();

        expectedResponseDTO = TrainerWorkloadResponseDTO.builder()
                .trainerUsername("john.doe")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .trainingDate(LocalDate.of(2024, 6, 1))
                .trainingDuration(180)
                .build();
    }

    @Test
    void testActionOnADD_existingWorkload() {
        when(trainerWorkloadRepository.findTrainerWorkloadByTrainerUsernameAndTrainingDate(
                eq(addRequestDTO.getTrainerUsername()), eq(LocalDate.of(2024, 6, 1))))
                .thenReturn(Optional.of(existingTrainerWorkload));

        when(trainerWorkloadMapper.toTrainerWorkloadResponseDTO(any(TrainerWorkload.class)))
                .thenReturn(expectedResponseDTO);

        TrainerWorkloadResponseDTO result = trainerWorkloadService.actionOnADD(addRequestDTO);

        assertEquals(180, existingTrainerWorkload.getTrainingDuration());

        // Verify repository interaction (no save, just find)
        verify(trainerWorkloadRepository, times(1)).findTrainerWorkloadByTrainerUsernameAndTrainingDate(
                eq(addRequestDTO.getTrainerUsername()), eq(LocalDate.of(2024, 6, 1)));
        verify(trainerWorkloadRepository, never()).save(any(TrainerWorkload.class)); // Should not save a new one

        // Verify mapper interaction
        verify(trainerWorkloadMapper, times(1)).toTrainerWorkloadResponseDTO(existingTrainerWorkload);

        // Verify summary service is called
        verify(trainerWorkloadSummaryService, times(1)).produce(
                eq(addRequestDTO.getTrainerUsername()), eq(2024), eq(6));

        // Verify response DTO matches
        assertEquals(expectedResponseDTO, result);
    }

    @Test
    void testActionOnADD_newWorkload() {
        // Mock repository to return empty optional (no existing workload)
        when(trainerWorkloadRepository.findTrainerWorkloadByTrainerUsernameAndTrainingDate(
                eq(addRequestDTO.getTrainerUsername()), eq(LocalDate.of(2024, 6, 1))))
                .thenReturn(Optional.empty());

        // Mock mapper to convert request DTO to entity and entity to response DTO
        when(trainerWorkloadMapper.toTrainerWorkload(addRequestDTO)).thenReturn(newTrainerWorkload);
        when(trainerWorkloadRepository.save(newTrainerWorkload)).thenReturn(newTrainerWorkload); // Simulate save
        when(trainerWorkloadMapper.toTrainerWorkloadResponseDTO(newTrainerWorkload)).thenReturn(expectedResponseDTO);


        // Adjust expectedResponseDTO for this scenario (new workload will have duration of addRequestDTO)
        TrainerWorkloadResponseDTO newWorkloadExpectedResponse = TrainerWorkloadResponseDTO.builder()
                .trainerUsername("john.doe")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .trainingDate(LocalDate.of(2024, 6, 1))
                .trainingDuration(60) // Initial duration from DTO
                .build();
        when(trainerWorkloadMapper.toTrainerWorkloadResponseDTO(newTrainerWorkload)).thenReturn(newWorkloadExpectedResponse);


        // When actionOnADD is called
        TrainerWorkloadResponseDTO result = trainerWorkloadService.actionOnADD(addRequestDTO);

        // Verify repository interactions (find and save)
        verify(trainerWorkloadRepository, times(1)).findTrainerWorkloadByTrainerUsernameAndTrainingDate(
                eq(addRequestDTO.getTrainerUsername()), eq(LocalDate.of(2024, 6, 1)));
        verify(trainerWorkloadRepository, times(1)).save(newTrainerWorkload);

        // Verify mapper interactions
        verify(trainerWorkloadMapper, times(1)).toTrainerWorkload(addRequestDTO);
        verify(trainerWorkloadMapper, times(1)).toTrainerWorkloadResponseDTO(newTrainerWorkload);

        // Verify summary service is called
        verify(trainerWorkloadSummaryService, times(1)).produce(
                eq(addRequestDTO.getTrainerUsername()), eq(2024), eq(6));

        // Verify response DTO matches
        assertEquals(newWorkloadExpectedResponse, result);
        assertEquals(LocalDate.of(2024, 6, 1), addRequestDTO.getTrainingDate()); // Ensure DTO's date is set to start of month
    }

    @Test
    void testActionOnDELETE_existingWorkload() {
        // Mock repository to return an existing workload
        when(trainerWorkloadRepository.findTrainerWorkloadByTrainerUsernameAndTrainingDate(
                eq(deleteRequestDTO.getTrainerUsername()), eq(LocalDate.of(2024, 6, 1))))
                .thenReturn(Optional.of(existingTrainerWorkload));

        // Configure expected response after deletion
        TrainerWorkloadResponseDTO deleteExpectedResponse = TrainerWorkloadResponseDTO.builder()
                .trainerUsername("john.doe")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .trainingDate(LocalDate.of(2024, 6, 1))
                .trainingDuration(90) // 120 - 30
                .build();
        when(trainerWorkloadMapper.toTrainerWorkloadResponseDTO(any(TrainerWorkload.class)))
                .thenReturn(deleteExpectedResponse);

        // When actionOnDELETE is called
        TrainerWorkloadResponseDTO result = trainerWorkloadService.actionOnDELETE(deleteRequestDTO);

        // Then verify the workload duration is updated
        assertEquals(90, existingTrainerWorkload.getTrainingDuration()); // 120 - 30

        // Verify repository interaction (only find)
        verify(trainerWorkloadRepository, times(1)).findTrainerWorkloadByTrainerUsernameAndTrainingDate(
                eq(deleteRequestDTO.getTrainerUsername()), eq(LocalDate.of(2024, 6, 1)));
        verify(trainerWorkloadRepository, never()).save(any(TrainerWorkload.class)); // Should not save a new one

        // Verify mapper interaction
        verify(trainerWorkloadMapper, times(1)).toTrainerWorkloadResponseDTO(existingTrainerWorkload);

        // Verify summary service is called
        verify(trainerWorkloadSummaryService, times(1)).produce(
                eq(deleteRequestDTO.getTrainerUsername()), eq(2024), eq(6));

        // Verify response DTO matches
        assertEquals(deleteExpectedResponse, result);
    }

    @Test
    void testActionOnDELETE_workloadNotFound() {
        // Mock repository to return empty optional (no existing workload)
        when(trainerWorkloadRepository.findTrainerWorkloadByTrainerUsernameAndTrainingDate(
                eq(deleteRequestDTO.getTrainerUsername()), eq(LocalDate.of(2024, 6, 1))))
                .thenReturn(Optional.empty());

        // When actionOnDELETE is called, then expect TrainerWorkloadNotFoundException
        TrainerWorkloadNotFoundException thrown = assertThrows(TrainerWorkloadNotFoundException.class, () ->
                trainerWorkloadService.actionOnDELETE(deleteRequestDTO)
        );

        // Verify exception message
        assertEquals("Trainer workload on year [2024] and month [6] not found", thrown.getMessage());

        // Verify repository interaction (only find)
        verify(trainerWorkloadRepository, times(1)).findTrainerWorkloadByTrainerUsernameAndTrainingDate(
                eq(deleteRequestDTO.getTrainerUsername()), eq(LocalDate.of(2024, 6, 1)));

        // Verify no other interactions
        verifyNoInteractions(trainerWorkloadMapper);
        verifyNoInteractions(trainerWorkloadSummaryService);
        verify(trainerWorkloadRepository, never()).save(any(TrainerWorkload.class));
    }

    @Test
    void testActionOn_ADD_dispatch() {
        // Mock service behavior for ADD
        when(trainerWorkloadRepository.findTrainerWorkloadByTrainerUsernameAndTrainingDate(
                anyString(), any(LocalDate.class))).thenReturn(Optional.of(existingTrainerWorkload));
        when(trainerWorkloadMapper.toTrainerWorkloadResponseDTO(any(TrainerWorkload.class)))
                .thenReturn(expectedResponseDTO);

        // Set action type to ADD
        addRequestDTO.setActionType(ActionType.ADD.name());

        // When actionOn is called
        TrainerWorkloadResponseDTO result = trainerWorkloadService.actionOn(addRequestDTO);

        // Verify that actionOnADD was called
        // Since actionOnADD is a public method within the same class, Mockito can't directly verify calls to it
        // when it's called internally by a non-mocked @InjectMocks instance.
        // Instead, we verify the effects of actionOnADD by checking interactions with its dependencies.
        verify(trainerWorkloadRepository, times(1)).findTrainerWorkloadByTrainerUsernameAndTrainingDate(
                eq(addRequestDTO.getTrainerUsername()), eq(LocalDate.of(2024, 6, 1)));
        verify(trainerWorkloadMapper, times(1)).toTrainerWorkloadResponseDTO(any(TrainerWorkload.class));
        verify(trainerWorkloadSummaryService, times(1)).produce(
                eq(addRequestDTO.getTrainerUsername()), eq(2024), eq(6));
        assertEquals(expectedResponseDTO, result);
    }

    @Test
    void testActionOn_DELETE_dispatch() {
        // Mock service behavior for DELETE
        when(trainerWorkloadRepository.findTrainerWorkloadByTrainerUsernameAndTrainingDate(
                anyString(), any(LocalDate.class))).thenReturn(Optional.of(existingTrainerWorkload));
        TrainerWorkloadResponseDTO deleteExpectedResponse = TrainerWorkloadResponseDTO.builder()
                .trainerUsername("john.doe")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .trainingDate(LocalDate.of(2024, 6, 1))
                .trainingDuration(90) // 120 - 30
                .build();
        when(trainerWorkloadMapper.toTrainerWorkloadResponseDTO(any(TrainerWorkload.class)))
                .thenReturn(deleteExpectedResponse);

        // Set action type to DELETE
        deleteRequestDTO.setActionType(ActionType.DELETE.name());

        // When actionOn is called
        TrainerWorkloadResponseDTO result = trainerWorkloadService.actionOn(deleteRequestDTO);

        // Verify that actionOnDELETE effects occurred
        verify(trainerWorkloadRepository, times(1)).findTrainerWorkloadByTrainerUsernameAndTrainingDate(
                eq(deleteRequestDTO.getTrainerUsername()), eq(LocalDate.of(2024, 6, 1)));
        verify(trainerWorkloadMapper, times(1)).toTrainerWorkloadResponseDTO(any(TrainerWorkload.class));
        verify(trainerWorkloadSummaryService, times(1)).produce(
                eq(deleteRequestDTO.getTrainerUsername()), eq(2024), eq(6));
        assertEquals(deleteExpectedResponse, result);
    }
}
