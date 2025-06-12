package epam.mapper;

import epam.dto.request_dto.RegisterTrainerRequestDTO;
import epam.dto.response_dto.TrainingTypeResponseDTO;
import epam.entity.TrainingType;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-12T14:49:26+0500",
    comments = "version: 1.6.0.Beta1, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.13.jar, environment: Java 17.0.14 (Homebrew)"
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
