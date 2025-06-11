package epam.mapper;

import epam.dto.request_dto.RegisterTrainerRequestDTO;
import epam.dto.response_dto.TrainingTypeResponseDTO;
import epam.entity.TrainingType;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-11T11:13:08+0500",
    comments = "version: 1.6.0.Beta1, compiler: Eclipse JDT (IDE) 3.42.0.v20250514-1000, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@Component
public class TrainingTypeMapperImpl implements TrainingTypeMapper {

    @Override
    public TrainingType toTrainingType(RegisterTrainerRequestDTO userRequestDTO) {
        if ( userRequestDTO == null ) {
            return null;
        }

        TrainingType.TrainingTypeBuilder trainingType = TrainingType.builder();

        trainingType.description( userRequestDTO.getSpecialization() );

        return trainingType.build();
    }

    @Override
    public TrainingTypeResponseDTO toTrainingTypeResponseDTO(TrainingType s) {
        if ( s == null ) {
            return null;
        }

        TrainingTypeResponseDTO.TrainingTypeResponseDTOBuilder trainingTypeResponseDTO = TrainingTypeResponseDTO.builder();

        trainingTypeResponseDTO.trainingType( s.getDescription() );
        trainingTypeResponseDTO.trainingTypeId( s.getTrainingTypeId() );

        return trainingTypeResponseDTO.build();
    }
}
