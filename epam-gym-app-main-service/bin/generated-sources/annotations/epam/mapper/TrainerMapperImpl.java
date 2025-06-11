package epam.mapper;

import epam.dto.request_dto.TrainerRequestDTO;
import epam.dto.response_dto.RegisterTrainerResponseDTO;
import epam.dto.response_dto.TrainerResponseDTO;
import epam.entity.Trainer;
import epam.entity.TrainingType;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-11T11:13:08+0500",
    comments = "version: 1.6.0.Beta1, compiler: Eclipse JDT (IDE) 3.42.0.v20250514-1000, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@Component
public class TrainerMapperImpl implements TrainerMapper {

    @Autowired
    private UserMapper userMapper;

    @Override
    public TrainerResponseDTO toTrainerResponseDTO(Trainer trainer) {
        if ( trainer == null ) {
            return null;
        }

        TrainerResponseDTO.TrainerResponseDTOBuilder trainerResponseDTO = TrainerResponseDTO.builder();

        trainerResponseDTO.trainerSpecialization( trainerSpecializationDescription( trainer ) );
        trainerResponseDTO.user( userMapper.toUserResponseDTO( trainer.getUser() ) );

        return trainerResponseDTO.build();
    }

    @Override
    public Trainer toTrainer(TrainerRequestDTO trainerRequestDTO, TrainingType trainingType) {
        if ( trainerRequestDTO == null && trainingType == null ) {
            return null;
        }

        Trainer.TrainerBuilder trainer = Trainer.builder();

        if ( trainerRequestDTO != null ) {
            trainer.user( userMapper.toUser( trainerRequestDTO.getUser() ) );
        }
        trainer.specialization( trainingType );

        return trainer.build();
    }

    @Override
    public RegisterTrainerResponseDTO toRegisterTrainerResponseDTO(Trainer insert) {
        if ( insert == null ) {
            return null;
        }

        RegisterTrainerResponseDTO.RegisterTrainerResponseDTOBuilder registerTrainerResponseDTO = RegisterTrainerResponseDTO.builder();

        registerTrainerResponseDTO.trainerSpecialization( trainerSpecializationDescription( insert ) );
        registerTrainerResponseDTO.user( userMapper.toUserResponseDTO( insert.getUser() ) );

        return registerTrainerResponseDTO.build();
    }

    private String trainerSpecializationDescription(Trainer trainer) {
        TrainingType specialization = trainer.getSpecialization();
        if ( specialization == null ) {
            return null;
        }
        return specialization.getDescription();
    }
}
