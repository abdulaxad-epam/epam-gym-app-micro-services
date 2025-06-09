package epam.service;

import epam.dto.response_dto.TrainingTypeResponseDTO;
import epam.entity.TrainingType;

import java.util.List;

public interface TrainingTypeService {
    TrainingType getTrainingByTrainingName(String trainingName);

    List<TrainingTypeResponseDTO> findAll();
}
