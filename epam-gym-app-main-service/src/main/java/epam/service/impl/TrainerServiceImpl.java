package epam.service.impl;

import epam.dto.request_dto.TrainerRequestDTO;
import epam.dto.response_dto.RegisterTrainerResponseDTO;
import epam.dto.response_dto.TrainerResponseDTO;
import epam.dto.response_dto.TrainingResponseDTO;
import epam.entity.Trainer;
import epam.entity.Training;
import epam.exception.exception.TrainerNotFoundException;
import epam.mapper.TrainerMapper;
import epam.mapper.TrainingMapper;
import epam.repository.TrainerRepository;
import epam.service.TrainerService;
import epam.service.TrainingService;
import epam.service.TrainingTypeService;
import epam.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;

    private final TrainingTypeService trainingTypeService;

    private final TrainerMapper trainerMapper;

    private final TrainingMapper trainingMapper;

    private final UserService userService;

    private final TrainingService trainingService;


    @Override
    public RegisterTrainerResponseDTO createTrainer(TrainerRequestDTO trainerRequestDTO) {

        return trainerMapper.toRegisterTrainerResponseDTO(
                trainerRepository.save(
                        trainerMapper.toTrainer(
                                trainerRequestDTO, trainingTypeService.getTrainingByTrainingName(trainerRequestDTO.getSpecialization())
                        )
                )
        );
    }

    @Transactional
    @Override
    public TrainerResponseDTO updateTrainer(Authentication connectedUser, TrainerRequestDTO trainerRequestDTO) {
        UserDetails user = (UserDetails) connectedUser.getPrincipal();
        return trainerRepository.findTraineeByUser_Username(user.getUsername()).map(trainer -> {
            trainer.setSpecialization(
                    trainingTypeService.getTrainingByTrainingName(trainerRequestDTO.getSpecialization())
            );
            if (trainerRequestDTO.getUser() != null) {
                if (trainerRequestDTO.getUser().getFirstName() != null) {
                    trainer.getUser().setFirstname(trainerRequestDTO.getUser().getFirstName());
                }
                if (trainerRequestDTO.getUser().getLastName() != null) {
                    trainer.getUser().setLastname(trainerRequestDTO.getUser().getLastName());
                }
                if (trainerRequestDTO.getUser().getIsActive() != null) {
                    trainer.getUser().setIsActive(trainerRequestDTO.getUser().getIsActive());
                }
            }
            return trainerMapper.toTrainerResponseDTO(trainer);
        }).orElseThrow(() -> new TrainerNotFoundException("Trainer not found"));
    }

    @Override
    @Transactional
    public void deleteTrainer(Authentication connectedUser) {
        UserDetails user = (UserDetails) connectedUser.getPrincipal();
        String username = user.getUsername();

        userService.findByUsername(username).ifPresentOrElse(trainer -> {

            List<Training> trainerTrainings = trainingService.getTrainerTrainings(trainer.getUsername());

            trainerTrainings.forEach(training -> trainingService.deleteTraining(training, username));

            trainerRepository.deleteTrainerByUser_Username(username);

            log.info("Deleted trainings from user {}", username);
        }, () -> {
            throw new TrainerNotFoundException("Trainer not found");
        });

    }

    @Override
    public TrainerResponseDTO getTrainerByUsername(Authentication connectedUser) {
        UserDetails user = (UserDetails) connectedUser.getPrincipal();
        return trainerRepository.findTraineeByUser_Username(user.getUsername())
                .map(trainerMapper::toTrainerResponseDTO)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found"));
    }

    @Override
    public List<TrainingResponseDTO> getTrainerTrainings(Authentication connectedUser,
                                                         String periodFrom, String periodTo, String traineeName) {
        UserDetails user = (UserDetails) connectedUser.getPrincipal();

        LocalDateTime from = (periodFrom != null) ? LocalDate.parse(periodFrom).atStartOfDay() : LocalDateTime.of(LocalDate.EPOCH, LocalTime.MIDNIGHT);
        LocalDateTime to = (periodTo != null) ? LocalDate.parse(periodTo).atTime(LocalTime.MAX) : LocalDateTime.of(LocalDate.of(2035, 1, 1), LocalTime.MAX);


        List<Training> training = trainerRepository.getTrainerTrainings(
                user.getUsername(), from, to, traineeName).orElseThrow(
                () -> new TrainerNotFoundException("Trainer not found")
        );
        return training.stream().map(trainingMapper::toTrainingResponseDTO).toList();
    }

    @Transactional
    @Override
    public void updateTrainerStatus(Authentication connectedUser, Boolean isActive) {
        UserDetails user = (UserDetails) connectedUser.getPrincipal();
        Optional<Trainer> trainer = trainerRepository.findTraineeByUser_Username(user.getUsername());
        trainer.ifPresentOrElse(t -> t.getUser().setIsActive(isActive), () -> {
            throw new TrainerNotFoundException("Trainer not found");
        });
    }
}
