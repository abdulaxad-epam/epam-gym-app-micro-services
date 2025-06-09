package epam.mapper;

import epam.dto.request_dto.RegisterTrainerRequestDTO;
import epam.dto.response_dto.TrainingTypeResponseDTO;
import epam.entity.TrainingType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TrainingTypeMapperTest {

    private final TrainingTypeMapper trainingTypeMapper = Mappers.getMapper(TrainingTypeMapper.class);

    @Test
    void testToTrainingType_FromRegisterTrainerRequestDTO() {
        RegisterTrainerRequestDTO requestDTO = new RegisterTrainerRequestDTO();
        requestDTO.setSpecialization("Yoga");

        TrainingType trainingType = trainingTypeMapper.toTrainingType(requestDTO);

        assertNotNull(trainingType);
        assertEquals("Yoga", trainingType.getDescription());
    }

    @Test
    void testToTrainingType_FromString() {
        TrainingType trainingType = trainingTypeMapper.toTrainingType("Pilates");

        assertNotNull(trainingType);
        assertEquals("Pilates", trainingType.getDescription());
    }

    @Test
    void testToTrainingType_FromNullString() {
        TrainingType trainingType = trainingTypeMapper.toTrainingType((String) null);

        assertNull(trainingType);
    }

    @Test
    void testToTrainingTypeResponseDTO() {
        TrainingType trainingType = TrainingType.builder().description("Cardio").build();

        TrainingTypeResponseDTO responseDTO = trainingTypeMapper.toTrainingTypeResponseDTO(trainingType);

        assertNotNull(responseDTO);
        assertEquals("Cardio", responseDTO.getTrainingType());
    }
}