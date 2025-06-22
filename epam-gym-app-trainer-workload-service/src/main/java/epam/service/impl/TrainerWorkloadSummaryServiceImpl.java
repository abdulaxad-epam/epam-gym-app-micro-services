package epam.service.impl;

import epam.dto.TrainerWorkloadResponseDTO;
import epam.dto.TrainerWorkloadSummaryInMonthsResponseDTO;
import epam.dto.TrainerWorkloadSummaryInYearsResponseDTO;
import epam.dto.TrainerWorkloadSummaryResponseDTO;
import epam.messaging.producer.TrainerActionProducer;
import epam.service.TrainerWorkloadSummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerWorkloadSummaryServiceImpl implements TrainerWorkloadSummaryService {

    private final TrainerActionProducer trainerActionProducer;
    private final TrainerWorkloadMessagePropertiesBuilder propertiesBuilder;

    @Override
    public void produce(TrainerWorkloadResponseDTO trainerWorkloadResponseDTO, boolean status) {

        int year = trainerWorkloadResponseDTO.getTrainingDate().getYear();
        int month = trainerWorkloadResponseDTO.getTrainingDate().getMonth().getValue();

        MessagePostProcessor messagePostProcessor = propertiesBuilder.buildMessagePropertyOnProduce(year, month);

        TrainerWorkloadSummaryResponseDTO workloadSummaryResponseDTO = getTrainerWorkloadSummary(trainerWorkloadResponseDTO, status);

        trainerActionProducer.produceOnAction(messagePostProcessor, workloadSummaryResponseDTO);

    }


    private TrainerWorkloadSummaryResponseDTO getTrainerWorkloadSummary(TrainerWorkloadResponseDTO workloadResponseDTO, boolean status) {
        return TrainerWorkloadSummaryResponseDTO.builder()
                .username(workloadResponseDTO.getTrainerUsername())
                .firstName(workloadResponseDTO.getTrainerFirstName())
                .lastName(workloadResponseDTO.getTrainerLastName())
                .status(status)
                .workloadSummaryInYears(Collections.singletonList(TrainerWorkloadSummaryInYearsResponseDTO.builder()
                        .year(workloadResponseDTO.getTrainingDate().getYear())
                        .workloadSummaryInMonths(Collections.singletonList(TrainerWorkloadSummaryInMonthsResponseDTO.builder()
                                .month(workloadResponseDTO.getTrainingDate().getMonth().getValue())
                                .durationInMinutes(workloadResponseDTO.getTrainingDurationInMinutes())
                                .build()))
                        .build()))
                .build();
    }


}
