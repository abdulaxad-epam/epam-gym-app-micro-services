package epam.mapper;

import epam.dto.TrainerWorkloadRequestDTO;
import epam.dto.TrainerWorkloadResponseDTO;
import epam.entity.TrainerWorkload;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrainerWorkloadMapperTest {

    private final TrainerWorkloadMapper mapper = Mappers.getMapper(TrainerWorkloadMapper.class);

    @Test
    void testToTrainerWorkload() {
        TrainerWorkloadRequestDTO requestDTO = TrainerWorkloadRequestDTO.builder()
                .trainerUsername("john_doe")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .isActive(true)
                .trainingDate(LocalDate.of(2025, 6, 1))
                .trainingDuration(90)
                .build();

        TrainerWorkload workload = mapper.toTrainerWorkload(requestDTO);

        assertNotNull(workload);
        assertEquals("john_doe", workload.getTrainerUsername());
        assertEquals("John", workload.getTrainerFirstName());
        assertEquals("Doe", workload.getTrainerLastName());
        assertTrue(workload.getIsActive());
        assertEquals(LocalDate.of(2025, 6, 1), workload.getTrainingDate());
        assertEquals(90, workload.getTrainingDuration());
    }

    @Test
    void testToTrainerWorkloadResponseDTO() {
        TrainerWorkload workload = new TrainerWorkload();
        workload.setTrainerUsername("jane_doe");
        workload.setTrainerFirstName("Jane");
        workload.setTrainerLastName("Doe");
        workload.setTrainingDate(LocalDate.of(2025, 5, 20));
        workload.setTrainingDuration(120);

        TrainerWorkloadResponseDTO responseDTO = mapper.toTrainerWorkloadResponseDTO(workload);

        assertNotNull(responseDTO);
        assertEquals("jane_doe", responseDTO.getTrainerUsername());
        assertEquals("Jane", responseDTO.getTrainerFirstName());
        assertEquals("Doe", responseDTO.getTrainerLastName());
        assertEquals(LocalDate.of(2025, 5, 20), responseDTO.getTrainingDate());
        assertEquals(120, responseDTO.getTrainingDuration());
    }
}
