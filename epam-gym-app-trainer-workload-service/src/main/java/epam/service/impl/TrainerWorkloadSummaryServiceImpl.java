package epam.service.impl;

import epam.dto.TrainerWorkloadSummaryInMonthsResponseDTO;
import epam.dto.TrainerWorkloadSummaryInYearsResponseDTO;
import epam.dto.TrainerWorkloadSummaryResponseDTO;
import epam.entity.TrainerWorkload;
import epam.messaging.producer.TrainerActionProducer;
import epam.service.TrainerWorkloadService;
import epam.service.TrainerWorkloadSummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerWorkloadSummaryServiceImpl implements TrainerWorkloadSummaryService {

    private final TrainerWorkloadService trainerWorkloadService;

    private final TrainerActionProducer trainerActionProducer;

    private final TrainerWorkloadMessagePropertiesBuilder propertiesBuilder;

    @Override
    public TrainerWorkloadSummaryResponseDTO getTrainerWorkloadSummary(String trainerUsername, Integer year, Integer month) {
        List<TrainerWorkload> trainerWorkload = trainerWorkloadService.getTrainerWorkload(trainerUsername, year, month);


        if (trainerWorkload == null || trainerWorkload.isEmpty()) {
            return TrainerWorkloadSummaryResponseDTO.builder().build();
        }

        String trainerFirstName = trainerWorkload.get(0).getTrainerFirstName();
        String trainerLastName = trainerWorkload.get(0).getTrainerLastName();
        String username = trainerWorkload.get(0).getTrainerUsername();
        Boolean isActive = trainerWorkload.get(0).getIsActive();

        Map<Year, Map<Month, Integer>> aggregation = trainerWorkload.stream()
                .collect(Collectors.groupingBy(tw -> Year.of(tw.getTrainingDate().getYear()),
                                Collectors.groupingBy(tw -> tw.getTrainingDate().getMonth(),
                                        Collectors.summingInt(TrainerWorkload::getTrainingDuration)
                                )
                        )
                );


        List<TrainerWorkloadSummaryInYearsResponseDTO> summary = aggregation.entrySet().stream().map(yearMapEntry ->
                TrainerWorkloadSummaryInYearsResponseDTO.builder().year(String.valueOf(yearMapEntry.getKey()))
                        .workloadSummaryInMonths(yearMapEntry.getValue().entrySet().stream().map(
                                monthEntry -> {
                                    log.info("Year: {}, Month: {}, Duration {}", yearMapEntry.getKey(), monthEntry.getKey(), monthEntry.getValue());
                                    return TrainerWorkloadSummaryInMonthsResponseDTO.builder().month(String.valueOf(monthEntry.getKey()))
                                            .durationInMinutes(String.valueOf(monthEntry.getValue())).build();
                                }).toList()
                        ).build()).toList();

        return TrainerWorkloadSummaryResponseDTO.builder()
                .firstName(trainerFirstName)
                .lastName(trainerLastName)
                .username(username)
                .status(isActive)
                .workloadSummaryInYears(summary)
                .build();
    }

    @Override
    public void produce(String username, Integer year, Integer month) {
        MessagePostProcessor messagePostProcessor = propertiesBuilder.buildMessagePropertyOnProduce(year, month);
        TrainerWorkloadSummaryResponseDTO workloadSummaryResponseDTO = getTrainerWorkloadSummary(username, year, month);
        trainerActionProducer.produceOnAction(messagePostProcessor, workloadSummaryResponseDTO);
    }
}
