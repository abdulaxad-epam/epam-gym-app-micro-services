package epam.mapper;

import epam.dto.TrainerWorkloadRequestDTO;
import epam.dto.TrainerWorkloadResponseDTO;
import epam.entity.TrainerWorkload;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-11T11:34:58+0500",
    comments = "version: 1.6.0.Beta1, compiler: Eclipse JDT (IDE) 3.42.0.v20250514-1000, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@Component
public class TrainerWorkloadMapperImpl implements TrainerWorkloadMapper {

    @Override
    public TrainerWorkload toTrainerWorkload(TrainerWorkloadRequestDTO trainerWorkloadRequestDTO) {
        if ( trainerWorkloadRequestDTO == null ) {
            return null;
        }

        TrainerWorkload.TrainerWorkloadBuilder trainerWorkload = TrainerWorkload.builder();

        trainerWorkload.trainerUsername( trainerWorkloadRequestDTO.getTrainerUsername() );
        trainerWorkload.trainerFirstName( trainerWorkloadRequestDTO.getTrainerFirstName() );
        trainerWorkload.trainerLastName( trainerWorkloadRequestDTO.getTrainerLastName() );
        trainerWorkload.isActive( trainerWorkloadRequestDTO.getIsActive() );
        trainerWorkload.trainingDate( trainerWorkloadRequestDTO.getTrainingDate() );
        trainerWorkload.trainingDuration( trainerWorkloadRequestDTO.getTrainingDuration() );

        return trainerWorkload.build();
    }

    @Override
    public TrainerWorkloadResponseDTO toTrainerWorkloadResponseDTO(TrainerWorkload trainerWorkload) {
        if ( trainerWorkload == null ) {
            return null;
        }

        TrainerWorkloadResponseDTO.TrainerWorkloadResponseDTOBuilder trainerWorkloadResponseDTO = TrainerWorkloadResponseDTO.builder();

        trainerWorkloadResponseDTO.trainerUsername( trainerWorkload.getTrainerUsername() );
        trainerWorkloadResponseDTO.trainerFirstName( trainerWorkload.getTrainerFirstName() );
        trainerWorkloadResponseDTO.trainerLastName( trainerWorkload.getTrainerLastName() );
        trainerWorkloadResponseDTO.trainingDate( trainerWorkload.getTrainingDate() );
        trainerWorkloadResponseDTO.trainingDuration( trainerWorkload.getTrainingDuration() );

        return trainerWorkloadResponseDTO.build();
    }
}
