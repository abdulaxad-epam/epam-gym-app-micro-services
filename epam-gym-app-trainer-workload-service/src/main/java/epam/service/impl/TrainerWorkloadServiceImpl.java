package epam.service.impl;

import epam.dto.TrainerWorkloadRequestDTO;
import epam.dto.TrainerWorkloadResponseDTO;
import epam.entity.DailyWorkload;
import epam.entity.MonthlyWorkload;
import epam.entity.TrainerWorkload;
import epam.entity.YearlyWorkload;
import epam.enums.ActionType;
import epam.exception.DailyTrainingDurationExceededException;
import epam.exception.TrainerWorkloadNotFoundException;
import epam.repostiory.TrainerWorkloadRepository;
import epam.service.TrainerWorkloadService;
import epam.service.TrainerWorkloadSummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {

    @Value("${spring.data.mongodb.max-daily-working-hour}")
    private Integer maxDailyWorkingHours;

    private final TrainerWorkloadRepository trainerWorkloadRepository;
    private final TrainerWorkloadSummaryService trainerWorkloadSummaryService;


    @Override
    public TrainerWorkloadResponseDTO actionOn(TrainerWorkloadRequestDTO trainerWorkloadRequestDTO) {
        return trainerWorkloadRequestDTO.getActionType().equals(ActionType.ADD.name()) ?
                actionOnADD(trainerWorkloadRequestDTO) : actionOnDELETE(trainerWorkloadRequestDTO);
    }

    @Override
    public TrainerWorkloadResponseDTO actionOnADD(TrainerWorkloadRequestDTO trainerWorkloadRequestDTO) {
        TrainerWorkload trainerWorkload = trainerWorkloadRepository.findTrainerWorkloadByTrainerUsername(trainerWorkloadRequestDTO.getTrainerUsername());

        TrainerWorkloadResponseDTO trainerWorkloadResponseDTO = trainerWorkload == null
                ? createNewTrainerWorkload(trainerWorkloadRequestDTO)
                : updateExistingTrainerWorkload(trainerWorkloadRequestDTO, ActionType.ADD);

        trainerWorkloadSummaryService.produce(trainerWorkloadResponseDTO, trainerWorkloadRequestDTO.getIsActive());


        return trainerWorkloadResponseDTO;

    }

    @Override
    public TrainerWorkloadResponseDTO actionOnDELETE(TrainerWorkloadRequestDTO requestDTO) {
        TrainerWorkload trainerWorkload = trainerWorkloadRepository.findTrainerWorkloadByTrainerUsername(requestDTO.getTrainerUsername());
        if (trainerWorkload == null) {
            log.error("Trainer workload associated to username {} does not exist.", requestDTO.getTrainerUsername());
            throw new TrainerWorkloadNotFoundException(String.format("Trainer workload associated to username %s does not exist.", requestDTO.getTrainerUsername()));
        }
        TrainerWorkloadResponseDTO trainerWorkloadResponseDTO = updateExistingTrainerWorkload(requestDTO, ActionType.DELETE);
        trainerWorkloadSummaryService.produce(trainerWorkloadResponseDTO, requestDTO.getIsActive());
        return trainerWorkloadResponseDTO;
    }

    @Override
    public TrainerWorkloadResponseDTO updateExistingTrainerWorkload(TrainerWorkloadRequestDTO trainerWorkloadRequestDTO, ActionType actionType) {

        TrainerWorkload trainerWorkload = trainerWorkloadRepository.findTrainerWorkloadByTrainerUsername(trainerWorkloadRequestDTO.getTrainerUsername());

        Integer year = trainerWorkloadRequestDTO.getTrainingDate().getYear();
        Integer month = trainerWorkloadRequestDTO.getTrainingDate().getMonth().getValue();
        Integer day = trainerWorkloadRequestDTO.getTrainingDate().getDayOfMonth();
        Integer trainingDurationInMinutes = actionType.name().equals(ActionType.ADD.name()) ?
                trainerWorkloadRequestDTO.getTrainingDurationInMinutes() : -trainerWorkloadRequestDTO.getTrainingDurationInMinutes();

        if (trainerWorkload.getYears() == null) {
            trainerWorkload.setYears(new ArrayList<>());
        }
        YearlyWorkload yearlyWorkload = trainerWorkload
                .getYears()
                .stream()
                .filter(y -> y.getYear().equals(year))
                .findFirst()
                .orElseGet(() -> {
                    YearlyWorkload build = YearlyWorkload.builder()
                            .year(year)
                            .months(new ArrayList<>())
                            .build();
                    trainerWorkload.getYears().add(build);
                    return build;
                });

        if (yearlyWorkload.getMonths() == null) {
            yearlyWorkload.setMonths(new ArrayList<>());
        }
        MonthlyWorkload monthlyWorkload = yearlyWorkload
                .getMonths()
                .stream()
                .filter(m -> m.getMonth().equals(month))
                .findFirst()
                .orElseGet(() -> {
                    MonthlyWorkload build = MonthlyWorkload.builder()
                            .month(month)
                            .monthlyTrainingDuration(0)
                            .days(new ArrayList<>())
                            .build();
                    yearlyWorkload.getMonths().add(build);
                    return build;
                });

        if (monthlyWorkload.getDays() == null) {
            monthlyWorkload.setDays(new ArrayList<>());
        }
        DailyWorkload dailyWorkload = monthlyWorkload
                .getDays()
                .stream()
                .filter(d -> d.getDay().equals(day))
                .findFirst()
                .orElseGet(() -> {
                    DailyWorkload build = DailyWorkload.builder()
                            .day(day)
                            .dailyTrainingDuration(0)
                            .build();
                    monthlyWorkload.getDays().add(build);
                    return build;
                });

        log.info("Duration: {}", dailyWorkload.getDailyTrainingDuration() + trainingDurationInMinutes);
        synchronized (this) {
            if (dailyWorkload.getDailyTrainingDuration() + trainingDurationInMinutes > maxDailyWorkingHours) {
                throw new DailyTrainingDurationExceededException("Training duration cannot be more than 8 hours (480 minutes) for a day");
            }

            dailyWorkload.setDailyTrainingDuration(Math.max(dailyWorkload.getDailyTrainingDuration() + trainingDurationInMinutes, 0));

            int sum = monthlyWorkload.getDays().stream().mapToInt(DailyWorkload::getDailyTrainingDuration).sum();
            monthlyWorkload.setMonthlyTrainingDuration(sum);

            trainerWorkloadRepository.save(trainerWorkload);
        }
        return toTrainerWorkloadResponse(trainerWorkload, year, month, day);
    }

    @Override
    public TrainerWorkloadResponseDTO updateTrainer() {
        /*       if (!trainerFirstName.equals(trainerWorkload.getTrainerFirstName())) {
            Update updateTrainerTopLevelField = new Update();
            boolean isTrainerWorkloadTopLevelFieldUpdated = false;

                log.info("[{}] -  Trainer with username [{}], requested to update its firstname to [{}].", Logging.getTransactionId(), trainerUsername, trainerFirstName);
                updateTrainerTopLevelField.set("trainerFirstName", trainerUsername);
                isTrainerWorkloadTopLevelFieldUpdated = true;
            }
            if (!trainerLastName.equals(trainerWorkload.getTrainerLastName())) {
                log.info("[{}] -  Trainer with username [{}], requested to update its lastname to [{}]", Logging.getTransactionId(), trainerUsername, trainerLastName);
                updateTrainerTopLevelField.set("trainerLastName", trainerLastName);
                isTrainerWorkloadTopLevelFieldUpdated = true;
            }
            if (!isActive.equals(trainerWorkload.getIsActive())) {
                log.info("[{}] -  Trainer with username [{}], requested to update its status to [{}]", Logging.getTransactionId(), trainerUsername, isActive);
                updateTrainerTopLevelField.set("isActive", isActive);
                isTrainerWorkloadTopLevelFieldUpdated = true;
            }

            if (isTrainerWorkloadTopLevelFieldUpdated) {
                log.info("[{}] -  Updating trainer workload top level fields", Logging.getTransactionId());
                mongoTemplate.updateFirst(query, updateTrainerTopLevelField, TrainerWorkload.class);
            }
 **/
        return null;
    }


    private TrainerWorkloadResponseDTO createNewTrainerWorkload(TrainerWorkloadRequestDTO trainerWorkloadRequestDTO) {
        TrainerWorkload build = toTrainerWorkload(trainerWorkloadRequestDTO);
        TrainerWorkload save = trainerWorkloadRepository.save(build);

        return toTrainerWorkloadResponseDTO(save,
                trainerWorkloadRequestDTO.getTrainingDate().getYear(),
                trainerWorkloadRequestDTO.getTrainingDate().getMonth().getValue(),
                trainerWorkloadRequestDTO.getTrainingDate().getDayOfMonth(),
                trainerWorkloadRequestDTO.getTrainingDurationInMinutes());
    }

    private TrainerWorkload toTrainerWorkload(TrainerWorkloadRequestDTO trainerWorkloadRequestDTO) {

        Integer year = trainerWorkloadRequestDTO.getTrainingDate().getYear();
        Integer month = trainerWorkloadRequestDTO.getTrainingDate().getMonth().getValue();
        int day = trainerWorkloadRequestDTO.getTrainingDate().getDayOfMonth();
        Integer trainingDuration = trainerWorkloadRequestDTO.getTrainingDurationInMinutes();

        return TrainerWorkload.builder()
                .id(UUID.randomUUID().toString())
                .trainerFirstName(trainerWorkloadRequestDTO.getTrainerFirstName())
                .trainerLastName(trainerWorkloadRequestDTO.getTrainerLastName())
                .trainerUsername(trainerWorkloadRequestDTO.getTrainerUsername())
                .isActive(trainerWorkloadRequestDTO.getIsActive())
                .years(Collections.singletonList(YearlyWorkload.builder()
                        .year(year)
                        .months(Collections.singletonList(MonthlyWorkload.builder()
                                .month(month)
                                .monthlyTrainingDuration(trainingDuration)
                                .days(Collections.singletonList(DailyWorkload.builder()
                                        .day(day)
                                        .dailyTrainingDuration(trainingDuration)
                                        .build()))
                                .build()))
                        .build()))
                .build();
    }


    private TrainerWorkloadResponseDTO toTrainerWorkloadResponse(TrainerWorkload trainerWorkload, Integer year, Integer month, Integer day) {
        log.info("Trainer workload {}, year {}, month {}", trainerWorkload, year, month);
        return toTrainerWorkloadResponseDTO(trainerWorkload, year, month, day,
                trainerWorkload.getYears()
                        .stream()
                        .filter(yearlyWorkload -> yearlyWorkload.getYear().equals(year))
                        .findFirst()
                        .get()
                        .getMonths()
                        .stream()
                        .filter(monthlyWorkload -> monthlyWorkload.getMonth().equals(month))
                        .findFirst()
                        .get()
                        .getMonthlyTrainingDuration()
        );

    }

    private TrainerWorkloadResponseDTO toTrainerWorkloadResponseDTO(TrainerWorkload workload, Integer year, Integer month, Integer day, Integer trainingDurationInMinutes) {
        return TrainerWorkloadResponseDTO.builder()
                .trainerUsername(workload.getTrainerUsername())
                .trainerFirstName(workload.getTrainerFirstName())
                .trainerLastName(workload.getTrainerLastName())
                .trainingDate(LocalDate.of(year, month, day))
                .trainingDurationInMinutes(trainingDurationInMinutes)
                .build();
    }

}
