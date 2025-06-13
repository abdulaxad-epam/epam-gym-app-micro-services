package epam.mapper;


import epam.dto.response_dto.TrainingRequestDTO;
import epam.dto.response_dto.TrainingResponseDTO;
import epam.entity.Trainee;
import epam.entity.Trainer;
import epam.entity.Training;
import epam.entity.TrainingType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {TrainerMapper.class, TraineeMapper.class})
public interface TrainingMapper {
    TrainingMapper INSTANCE = Mappers.getMapper(TrainingMapper.class);

    @Named("toTrainingResponseDTO")
    @Mapping(source = "trainee", target = "trainee", qualifiedByName = "toTraineeResponseDTO")
    @Mapping(source = "trainer", target = "trainer", qualifiedByName = "toTrainerResponseDTO")
    @Mapping(source = "trainingType.description", target = "trainingType")
    @Mapping(source = "trainingId", target = "trainingId")
    @Mapping(source = "trainingDuration", target = "trainingDurationInMinutes")
    TrainingResponseDTO toTrainingResponseDTO(Training training);

    @Named("toTraining")
    @Mapping(source = "trainingRequestDTO.trainingDate", target = "trainingDate")
    @Mapping(source = "trainingRequestDTO.trainingDurationInMinutes", target = "trainingDuration")
    @Mapping(source = "trainingRequestDTO.trainingName", target = "trainingName")
    @Mapping(source = "trainingType", target = "trainingType")
    @Mapping(source = "trainer", target = "trainer")
    @Mapping(source = "trainee", target = "trainee")
    Training toTraining(TrainingRequestDTO trainingRequestDTO, TrainingType trainingType, Trainer trainer, Trainee trainee);
}
