package epam.service.impl;

import epam.dto.request_dto.TraineeRequestDTO;
import epam.dto.request_dto.UpdateTraineeRequestDTO;
import epam.dto.response_dto.RegisterTraineeResponseDTO;
import epam.dto.response_dto.TraineeResponseDTO;
import epam.dto.response_dto.TrainingResponseDTO;
import epam.entity.Trainee;
import epam.entity.Training;
import epam.exception.exception.TraineeNotFoundException;
import epam.mapper.TraineeMapper;
import epam.mapper.TrainingMapper;
import epam.repository.TraineeRepository;
import epam.service.TraineeService;
import epam.service.TraineeTrainerService;
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
public class TraineeServiceImpl implements TraineeService {

    private final TraineeRepository traineeRepository;

    private final TraineeMapper traineeMapper;

    private final TraineeTrainerService traineeTrainerService;

    private final TrainingMapper trainingMapper;

    @Override
    public RegisterTraineeResponseDTO createTrainee(TraineeRequestDTO traineeRequestDTO) {


        return traineeMapper.toRegisterTraineeResponseDTO(
                traineeRepository.save(
                        traineeMapper.toTrainee(traineeRequestDTO)
                )
        );
    }

    @Override
    @Transactional
    public TraineeResponseDTO updateTrainee(Authentication connectedUser, UpdateTraineeRequestDTO requestDTO) {

        UserDetails user = (UserDetails) connectedUser.getPrincipal();
        String username = user.getUsername();

        Optional<Trainee> trainee = traineeRepository.findTraineeByUser_Username(username);

        trainee.ifPresentOrElse(t -> {


            t.getUser().setFirstname(requestDTO.getFirstname());
            t.getUser().setLastname(requestDTO.getLastname());
            if (requestDTO.getDateOfBirth() != null) {
                t.setDateOfBirth(requestDTO.getDateOfBirth());
            }
            if (requestDTO.getAddress() != null) {
                t.setAddress(requestDTO.getAddress());
            }
            if (requestDTO.getIsActive() != null) {
                t.getUser().setIsActive(requestDTO.getIsActive());
            }
        }, () -> {
            throw new TraineeNotFoundException(String.format("Trainee not found with username: %s", username));
        });
        return traineeMapper.toTraineeResponseDTO(trainee.get());
    }

    @Override
    @Transactional
    public void deleteTrainee(Authentication connectedUser) {

        UserDetails user = (UserDetails) connectedUser.getPrincipal();
        String username = user.getUsername();

        if (!traineeRepository.existsTraineeByUser_Username(username)) {
            throw new TraineeNotFoundException(String.format("Trainee not found with username: %s", username));
        }
//        traineeTrainerService.unassignTraineeFromTrainer(username);
        traineeRepository.deleteTraineeByUser_Username(username);
    }

    @Override
    public TraineeResponseDTO getTraineeProfile(Authentication connectedUser) {
        UserDetails user = (UserDetails) connectedUser.getPrincipal();
        return traineeMapper.toTraineeResponseDTO(traineeRepository.findTraineeByUser_Username(user.getUsername().toLowerCase())
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found")));
    }

    @Transactional
    @Override
    public void updateTraineeStatus(Authentication connectedUser, Boolean isActive) {
        UserDetails user = (UserDetails) connectedUser.getPrincipal();
        String username = user.getUsername();
        Optional<Trainee> trainee = traineeRepository.findTraineeByUser_Username(username);
        trainee.ifPresentOrElse(t -> t.getUser().setIsActive(isActive), () -> {
            throw new TraineeNotFoundException(String.format("Trainee not found with username: %s", username));
        });
    }

    public List<TrainingResponseDTO> getTraineeTrainings(String periodFrom, String periodTo,
                                                         String trainerName, String trainingType, Authentication connectedUser) {
        UserDetails user = (UserDetails) connectedUser.getPrincipal();
        String username = user.getUsername();

        LocalDateTime from = (periodFrom != null) ? LocalDate.parse(periodFrom).atStartOfDay() : LocalDateTime.of(LocalDate.EPOCH, LocalTime.MIDNIGHT);
        LocalDateTime to = (periodTo != null) ? LocalDate.parse(periodTo).atTime(LocalTime.MAX) : LocalDateTime.of(LocalDate.of(2030, 1, 1), LocalTime.MAX);


        if (trainerName == null) trainerName = "";
        if (trainingType == null) trainingType = "";

        List<Training> trainings = traineeRepository.getTraineeTrainings(
                username, from, to, trainerName, trainingType
        ).orElseThrow(() ->
                new TraineeNotFoundException(String.format("Trainee not found with username: %s", username))
        );

        return trainings.stream().map(trainingMapper::toTrainingResponseDTO).toList();
    }

}
