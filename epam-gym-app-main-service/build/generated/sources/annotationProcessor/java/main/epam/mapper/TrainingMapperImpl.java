package epam.mapper;

import epam.dto.response_dto.TrainingRequestDTO;
import epam.dto.response_dto.TrainingResponseDTO;
import epam.entity.Trainee;
import epam.entity.Trainer;
import epam.entity.Training;
import epam.entity.TrainingType;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-09T19:10:14+0500",
    comments = "version: 1.6.0.Beta1, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.13.jar, environment: Java 17.0.14 (Homebrew)"
)
@Component
public class TrainingMapperImpl implements TrainingMapper {

    @Autowired
    private TrainerMapper trainerMapper;
    @Autowired
    private TraineeMapper traineeMapper;

    @Override
    public TrainingResponseDTO toTrainingResponseDTO(Training training) {
        if ( training == null ) {
            return null;
        }

        TrainingResponseDTO.TrainingResponseDTOBuilder trainingResponseDTO = TrainingResponseDTO.builder();

        trainingResponseDTO.trainee( traineeMapper.toTraineeResponseDTO( training.getTrainee() ) );
        trainingResponseDTO.trainer( trainerMapper.toTrainerResponseDTO( training.getTrainer() ) );
        trainingResponseDTO.trainingType( trainingTrainingTypeDescription( training ) );
        trainingResponseDTO.trainingId( training.getTrainingId() );
        trainingResponseDTO.trainingDate( training.getTrainingDate() );
        trainingResponseDTO.trainingDuration( training.getTrainingDuration() );
        trainingResponseDTO.trainingName( training.getTrainingName() );

        return trainingResponseDTO.build();
    }

    @Override
    public Training toTraining(TrainingRequestDTO trainingRequestDTO, TrainingType trainingType, Trainer trainer, Trainee trainee) {
        if ( trainingRequestDTO == null && trainingType == null && trainer == null && trainee == null ) {
            return null;
        }

        Training.TrainingBuilder training = Training.builder();

        if ( trainingRequestDTO != null ) {
            if ( trainingRequestDTO.getTrainingDate() != null ) {
                training.trainingDate( trainingRequestDTO.getTrainingDate().atStartOfDay() );
            }
            training.trainingDuration( trainingRequestDTO.getTrainingDuration() );
            training.trainingName( trainingRequestDTO.getTrainingName() );
        }
        training.trainingType( trainingType );
        training.trainer( trainer );
        training.trainee( trainee );

        return training.build();
    }

    private String trainingTrainingTypeDescription(Training training) {
        TrainingType trainingType = training.getTrainingType();
        if ( trainingType == null ) {
            return null;
        }
        return trainingType.getDescription();
    }
}
