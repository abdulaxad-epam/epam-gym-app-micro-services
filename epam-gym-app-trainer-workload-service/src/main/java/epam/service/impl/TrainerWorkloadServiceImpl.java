package epam.service.impl;

import epam.dto.TrainerWorkloadRequestDTO;
import epam.dto.TrainerWorkloadResponseDTO;
import epam.entity.TrainerWorkload;
import epam.enums.ActionType;
import epam.exception.TrainerWorkloadNotFoundException;
import epam.mapper.TrainerWorkloadMapper;
import epam.repostiory.TrainerWorkloadRepository;
import epam.service.TrainerWorkloadService;
import epam.service.TrainerWorkloadSummaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {

    private final TrainerWorkloadRepository trainerWorkloadRepository;
    private final TrainerWorkloadMapper trainerWorkloadMapper;
    private final TrainerWorkloadSummaryService trainerWorkloadSummaryService;

    public TrainerWorkloadServiceImpl(@Lazy TrainerWorkloadRepository trainerWorkloadRepository,
                                      @Lazy TrainerWorkloadMapper trainerWorkloadMapper,
                                      @Lazy TrainerWorkloadSummaryService trainerWorkloadSummaryService) {
        this.trainerWorkloadRepository = trainerWorkloadRepository;
        this.trainerWorkloadMapper = trainerWorkloadMapper;
        this.trainerWorkloadSummaryService = trainerWorkloadSummaryService;
    }

    @Override
    public List<TrainerWorkload> getTrainerWorkload(String trainerUsername, Integer year, Integer month) {
        if (year == null) {
            return trainerWorkloadRepository.findTrainerWorkloadsByTrainerUsername(trainerUsername);
        }
        return trainerWorkloadRepository.findTrainerWorkloadsByTrainerUsernameAndTrainingDate(
                trainerUsername, LocalDate.of(year, month, 1));
    }

    @Override
    @Transactional
    public TrainerWorkloadResponseDTO actionOn(TrainerWorkloadRequestDTO trainerWorkloadRequestDTO) {
        return trainerWorkloadRequestDTO.getActionType().equals(ActionType.ADD.name()) ?
                actionOnADD(trainerWorkloadRequestDTO) : actionOnDELETE(trainerWorkloadRequestDTO);
    }

    @Transactional(rollbackFor = Exception.class)
    public TrainerWorkloadResponseDTO actionOnADD(TrainerWorkloadRequestDTO trainerWorkloadRequestDTO) {

        LocalDate trainingDate = trainerWorkloadRequestDTO.getTrainingDate();
        int year = trainingDate.getYear();
        int month = trainingDate.getMonthValue();

        Optional<TrainerWorkload> trainerWorkload = trainerWorkloadRepository.findTrainerWorkloadByTrainerUsernameAndTrainingDate(
                trainerWorkloadRequestDTO.getTrainerUsername(), LocalDate.of(year, month, 1));


        if (trainerWorkload.isPresent()) {
            TrainerWorkload workload = trainerWorkload.get();
            int updatedDuration = workload.getTrainingDuration() + trainerWorkloadRequestDTO.getTrainingDuration();
            workload.setTrainingDuration(updatedDuration);
            TrainerWorkloadResponseDTO workloadResponseDTO = trainerWorkloadMapper.toTrainerWorkloadResponseDTO(workload);

            trainerWorkloadSummaryService.produce(workloadResponseDTO.getTrainerUsername(), year, month);
            return workloadResponseDTO;
        }

        trainerWorkloadRequestDTO.setTrainingDate(LocalDate.of(year, month, 1));

        TrainerWorkload workload = trainerWorkloadMapper.toTrainerWorkload(trainerWorkloadRequestDTO);
        TrainerWorkload save = trainerWorkloadRepository.save(workload);

        TrainerWorkloadResponseDTO workloadResponseDTO = trainerWorkloadMapper.toTrainerWorkloadResponseDTO(save);
        trainerWorkloadSummaryService.produce(workloadResponseDTO.getTrainerUsername(), year, month);

        return workloadResponseDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    public TrainerWorkloadResponseDTO actionOnDELETE(TrainerWorkloadRequestDTO trainerWorkloadRequestDTO) {

        LocalDate trainingDate = trainerWorkloadRequestDTO.getTrainingDate();
        int year = trainingDate.getYear();
        int month = trainingDate.getMonthValue();

        Optional<TrainerWorkload> trainerWorkload = trainerWorkloadRepository.findTrainerWorkloadByTrainerUsernameAndTrainingDate(
                trainerWorkloadRequestDTO.getTrainerUsername(), LocalDate.of(year, month, 1)
        );

        return trainerWorkload.map(workload -> {
            int updatedDuration = workload.getTrainingDuration() - trainerWorkloadRequestDTO.getTrainingDuration();
            workload.setTrainingDuration(updatedDuration);
            TrainerWorkloadResponseDTO workloadResponseDTO = trainerWorkloadMapper.toTrainerWorkloadResponseDTO(workload);

            trainerWorkloadSummaryService.produce(workloadResponseDTO.getTrainerUsername(), year, month);

            return workloadResponseDTO;
        }).orElseThrow(() ->
                new TrainerWorkloadNotFoundException("Trainer workload on year [" + year + "] and month [" + month + "] not found")
        );
    }

}
