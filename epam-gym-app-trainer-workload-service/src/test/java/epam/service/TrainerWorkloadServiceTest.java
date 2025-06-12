package epam.service;

import epam.dto.TrainerWorkloadRequestDTO;
import epam.dto.TrainerWorkloadResponseDTO;
import epam.entity.TrainerWorkload;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainerWorkloadServiceTest {

    @Mock
    private TrainerWorkloadRepository repository;

    @Mock
    private TrainerWorkloadMapper mapper;

    @InjectMocks
    private TrainerWorkloadServiceImpl service;

    private TrainerWorkload workload;
    private TrainerWorkloadRequestDTO requestDTO;
    private TrainerWorkloadResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        workload = new TrainerWorkload();
        workload.setTrainerUsername("trainer1");
        workload.setTrainingDate(LocalDate.of(2025, 3, 1));
        workload.setTrainingDuration(5);

        requestDTO = new TrainerWorkloadRequestDTO();
        requestDTO.setTrainerUsername("trainer1");
        requestDTO.setTrainingDate(LocalDate.of(2025, 3, 10));
        requestDTO.setTrainingDurationInMinutes(3);

        responseDTO = new TrainerWorkloadResponseDTO();
        responseDTO.setTrainerUsername("trainer1");
        responseDTO.setTrainingDurationInMinutes(8);
    }

    @Test
    void testGetTrainerWorkload_WithYearAndMonth() {
        when(repository.findTrainerWorkloadsByTrainerUsernameAndTrainingDate("trainer1", LocalDate.of(2025, 3, 1)))
                .thenReturn(List.of(workload));

        List<TrainerWorkload> result = service.getTrainerWorkload("trainer1", 2025, 3);
        assertEquals(1, result.size());
        assertEquals("trainer1", result.get(0).getTrainerUsername());
    }

    @Test
    void testGetTrainerWorkload_WithoutYear() {
        when(repository.findTrainerWorkloadsByTrainerUsername("trainer1")).thenReturn(List.of(workload));
        List<TrainerWorkload> result = service.getTrainerWorkload("trainer1", null, 3);
        assertEquals(1, result.size());
    }

    @Test
    void testActionOn_AddDelegation() {
        requestDTO.setActionType("ADD");
        TrainerWorkloadServiceImpl spyService = spy(service);

        doReturn(responseDTO).when(spyService).actionOnADD(requestDTO);
        TrainerWorkloadResponseDTO result = spyService.actionOn(requestDTO);

        assertEquals("trainer1", result.getTrainerUsername());
    }

    @Test
    void testActionOn_DeleteDelegation() {
        requestDTO.setActionType("DELETE");
        TrainerWorkloadServiceImpl spyService = spy(service);

        doReturn(responseDTO).when(spyService).actionOnDELETE(requestDTO);
        TrainerWorkloadResponseDTO result = spyService.actionOn(requestDTO);

        assertEquals("trainer1", result.getTrainerUsername());
    }

    @Test
    void testActionOnADD_WhenExistingWorkloadPresent() {
        when(repository.findTrainerWorkloadByTrainerUsernameAndTrainingDate("trainer1", LocalDate.of(2025, 3, 1)))
                .thenReturn(Optional.of(workload));

        workload.setTrainingDuration(8);
        when(mapper.toTrainerWorkloadResponseDTO(workload)).thenReturn(responseDTO);

        TrainerWorkloadResponseDTO result = service.actionOnADD(requestDTO);

        assertEquals(8, result.getTrainingDurationInMinutes());
        verify(repository, never()).save(any());
    }

    @Test
    void testActionOnADD_WhenNoExistingWorkload() {
        when(repository.findTrainerWorkloadByTrainerUsernameAndTrainingDate("trainer1", LocalDate.of(2025, 3, 1)))
                .thenReturn(Optional.empty());

        TrainerWorkload mappedEntity = new TrainerWorkload();
        mappedEntity.setTrainingDate(LocalDate.of(2025, 3, 1));
        mappedEntity.setTrainingDuration(3);

        when(mapper.toTrainerWorkload(requestDTO)).thenReturn(mappedEntity);
        when(repository.save(mappedEntity)).thenReturn(mappedEntity);
        when(mapper.toTrainerWorkloadResponseDTO(mappedEntity)).thenReturn(responseDTO);

        TrainerWorkloadResponseDTO result = service.actionOnADD(requestDTO);

        assertEquals("trainer1", result.getTrainerUsername());
        verify(repository).save(mappedEntity);
    }

    @Test
    void testActionOnDELETE_WhenWorkloadExists() {
        when(repository.findTrainerWorkloadByTrainerUsernameAndTrainingDate("trainer1", LocalDate.of(2025, 3, 1)))
                .thenReturn(Optional.of(workload));

        workload.setTrainingDuration(2);
        when(mapper.toTrainerWorkloadResponseDTO(workload)).thenReturn(responseDTO);

        TrainerWorkloadResponseDTO result = service.actionOnDELETE(requestDTO);

        assertEquals("trainer1", result.getTrainerUsername());
    }

    @Test
    void testActionOnDELETE_WhenWorkloadNotFound() {
        when(repository.findTrainerWorkloadByTrainerUsernameAndTrainingDate("trainer1", LocalDate.of(2025, 3, 1)))
                .thenReturn(Optional.empty());

        assertThrows(TrainerWorkloadNotFoundException.class, () ->
                service.actionOnDELETE(requestDTO));
    }
}
