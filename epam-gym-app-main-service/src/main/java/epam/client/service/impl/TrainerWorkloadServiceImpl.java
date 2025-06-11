package epam.client.service.impl;

import epam.client.dto.TrainerWorkloadRequestDTO;
import epam.client.dto.TrainerWorkloadResponseDTO;
import epam.client.dto.TrainerWorkloadSummaryResponseDTO;
import epam.client.messaging.consumer.TrainerWorkloadSummaryMessageConsumer;
import epam.client.messaging.producer.TrainerMessageProducer;
import epam.client.service.TrainerWorkloadService;
import epam.entity.Training;
import epam.enums.ActionType;
import epam.exception.exception.TrainerWorkloadIsUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {

    private final TrainerMessageProducer trainerMessageProducer;
    private final TrainerWorkloadSummaryMessageConsumer trainerWorkloadSummaryMessageConsumer;
    private final TrainerWorkloadMessagePropertiesBuilder trainerWorkloadUrlBuilder;

    private static final String TRAINER_WORKLOAD_SERVICE = "trainerWorkloadService";

    @Override
    @CircuitBreaker(name = TRAINER_WORKLOAD_SERVICE, fallbackMethod = "actionOnFallback")
    public TrainerWorkloadResponseDTO actionOnADD(Training training) {
        TrainerWorkloadRequestDTO build = getTrainerWorkloadRequestDTO(training, ActionType.ADD);

        MessagePostProcessor url = trainerWorkloadUrlBuilder.buildMessagePropertyOnProduce();
        trainerMessageProducer.produceOnAction(url, build);

        return getTrainerWorkloadResponseDTO(build);
    }

    @Override
    @CircuitBreaker(name = TRAINER_WORKLOAD_SERVICE, fallbackMethod = "actionOnFallback")
    public TrainerWorkloadResponseDTO actionOnDELETE(Training training) {
        TrainerWorkloadRequestDTO build = getTrainerWorkloadRequestDTO(training, ActionType.DELETE);

        MessagePostProcessor url = trainerWorkloadUrlBuilder.buildMessagePropertyOnProduce();
        trainerMessageProducer.produceOnAction(url, build);

        return getTrainerWorkloadResponseDTO(build);
    }

    @Override
    @PreAuthorize("hasRole('TRAINER')")
    @CircuitBreaker(name = TRAINER_WORKLOAD_SERVICE, fallbackMethod = "getTrainerWorkloadSummaryFallback")
    public TrainerWorkloadSummaryResponseDTO getTrainerWorkloadSummary(Integer year, Integer month, Authentication connectedUser) {
        UserDetails user = (UserDetails) connectedUser.getPrincipal();

        TrainerWorkloadSummaryResponseDTO body = trainerWorkloadSummaryMessageConsumer.getTrainerWorkloadSummary(user.getUsername(), year, month);
        return body == null || body.getUsername() == null ? null : body;
    }

    public TrainerWorkloadResponseDTO actionOnFallback(Training training, Throwable t) {
        log.warn("Fallback triggered for action on Trainer Workload Service for training ID: {} due to: {}", training.getTrainingId(), t.getMessage() + t);
        throw new TrainerWorkloadIsUnavailableException("Trainer workload action services currently unavailable");
    }

    public TrainerWorkloadSummaryResponseDTO getTrainerWorkloadSummaryFallback(Integer year, Integer month, Authentication connectedUser, Throwable t) {
        log.warn("Fallback triggered for action on Trainer Workload Service Summary for trainer username: {} due to: {}", ((UserDetails) connectedUser.getPrincipal()).getUsername(), t.getMessage());
        log.warn("Request parameters: {}", ((UserDetails) connectedUser.getPrincipal()).getUsername() + ":" + year + ":" + month);
        throw new TrainerWorkloadIsUnavailableException("Trainer workload summary currently unavailable.");
    }

    private TrainerWorkloadRequestDTO getTrainerWorkloadRequestDTO(Training training, ActionType actionType) {
        return TrainerWorkloadRequestDTO.builder()
                .trainerUsername(training.getTrainer().getUser().getUsername())
                .trainerFirstName(training.getTrainer().getUser().getFirstname())
                .trainerLastName(training.getTrainer().getUser().getLastname())
                .trainingDate(training.getTrainingDate().toLocalDate())
                .trainingDuration(training.getTrainingDuration())
                .isActive(training.getTrainer().getUser().getIsActive())
                .actionType(actionType.name())
                .build();
    }

    private TrainerWorkloadResponseDTO getTrainerWorkloadResponseDTO(TrainerWorkloadRequestDTO build) {
        return TrainerWorkloadResponseDTO.builder()
                .trainerFirstName(build.getTrainerFirstName())
                .trainerLastName(build.getTrainerLastName())
                .trainerUsername(build.getTrainerUsername())
                .trainingDate(build.getTrainingDate())
                .trainingDuration(build.getTrainingDuration())
                .build();
    }
}
