package epam.mapper;


import epam.dto.request_dto.RegisterTrainerRequestDTO;
import epam.dto.response_dto.TrainingTypeResponseDTO;
import epam.entity.TrainingType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface TrainingTypeMapper {
    TrainingTypeMapper INSTANCE = Mappers.getMapper(TrainingTypeMapper.class);

    @Named("toTrainingType")
    @Mapping(source = "specialization", target = "description")
    TrainingType toTrainingType(RegisterTrainerRequestDTO userRequestDTO);

    default TrainingType toTrainingType(String specialization) {
        if (specialization == null) {
            return null;
        }
        return TrainingType.builder()
                .description(specialization)
                .build();
    }

    @Named("toTrainingTypeResponseDTO")
    @Mapping(source = "description", target = "trainingType")
    TrainingTypeResponseDTO toTrainingTypeResponseDTO(TrainingType s);
}
