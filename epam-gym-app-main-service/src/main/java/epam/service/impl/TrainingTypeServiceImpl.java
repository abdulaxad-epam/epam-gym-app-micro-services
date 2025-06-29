package epam.service.impl;

import epam.dto.response_dto.TrainingTypeResponseDTO;
import epam.entity.TrainingType;
import epam.exception.exception.TrainingTypeNotFoundException;
import epam.mapper.TrainingTypeMapper;
import epam.repository.TrainingTypeRepository;
import epam.service.TrainingTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

@RequiredArgsConstructor
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingTypeMapper trainingTypeMapper;

    @Override
    public TrainingType getTrainingByTrainingName(String trainingName) {

        return trainingTypeRepository.findTrainingTypeByDescription(trainingName)
                .orElseThrow(() -> new TrainingTypeNotFoundException("Training with training name " + trainingName + " not found"));
    }

    @Override
    public List<TrainingTypeResponseDTO> findAll() {
        List<TrainingType> trainingTypes = trainingTypeRepository.findAll();
        return trainingTypes.stream().map(trainingTypeMapper::toTrainingTypeResponseDTO).toList();
    }
}
