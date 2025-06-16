package epam.service.impl;

import epam.dto.response_dto.TrainerResponseDTO;
import epam.entity.Trainee;
import epam.entity.Trainer;
import epam.entity.TrainerTrainee;
import epam.entity.Training;
import epam.exception.exception.TraineeHasAssignedBeforeException;
import epam.exception.exception.TraineeNotFoundException;
import epam.exception.exception.TrainerNotFoundException;
import epam.mapper.TrainerMapper;
import epam.repository.TraineeRepository;
import epam.repository.TrainerRepository;
import epam.repository.TrainerTraineeRepository;
import epam.service.TraineeTrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainerTraineeServiceImpl implements TraineeTrainerService {

    private final TrainerTraineeRepository trainerTraineeRepository;

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    private final TrainerMapper trainerMapper;

    @Transactional
    @Override
    public void assignTrainerToTrainee(String currentUsername, String trainerUsername) {
        TrainerTraineeRecord result = getTrainerTraineeRecord(currentUsername, trainerUsername);

        if (!trainerTraineeRepository.existsById_TrainerIdAndId_TraineeId(result.trainer().getTrainerId(), result.trainee().getTraineeId())) {
            TrainerTrainee trainerTrainee = TrainerTrainee.builder()
                    .trainee(result.trainee())
                    .trainer(result.trainer())
                    .id(new TrainerTrainee.TraineeTrainerId(result.trainee().getTraineeId(), result.trainer().getTrainerId()))
                    .build();

            trainerTraineeRepository.save(trainerTrainee);
            return;
        }
        throw new TraineeHasAssignedBeforeException("Trainee has been assigned to trainer before");
    }


    @Override
    @Transactional
    public void unassignTrainerFromTrainee(Training training) {
        trainerTraineeRepository.removeTrainerTraineeByTrainee_TraineeIdAndTrainer_TrainerId(training.getTrainee().getTraineeId(), training.getTrainer().getTrainerId());
    }

    @PreAuthorize("hasRole('TRAINEE')")
    @Transactional
    @Override
    public List<TrainerResponseDTO> updateTraineeTrainer(Authentication connectedUser, List<String> trainerUsernames) {

        UserDetails user = (UserDetails) connectedUser.getPrincipal();
        String username = user.getUsername();
        trainerTraineeRepository.removeTrainerTraineeByTrainee_User_Username(username);

        List<Trainer> trainers = trainerUsernames.stream().map(trainerUsername -> {
            TrainerTraineeRecord trainerTraineeRecord = getTrainerTraineeRecord(username, trainerUsername);

            TrainerTrainee trainerTrainee = TrainerTrainee.builder()
                    .trainee(trainerTraineeRecord.trainee())
                    .trainer(trainerTraineeRecord.trainer())
                    .id(new TrainerTrainee.TraineeTrainerId(trainerTraineeRecord.trainee().getTraineeId(), trainerTraineeRecord.trainer().getTrainerId()))
                    .build();

            trainerTraineeRepository.save(trainerTrainee);
            return trainerTraineeRecord.trainer();
        }).toList();

        return trainers.stream().map(trainerMapper::toTrainerResponseDTO).toList();
    }

    @PreAuthorize("hasRole('TRAINEE')")
    @Transactional(readOnly = true)
    @Override
    public List<TrainerResponseDTO> getAllNotAssignedTrainers(Authentication connectedUser) {

        UserDetails user = (UserDetails) connectedUser.getPrincipal();
        String username = user.getUsername();
        if (!traineeRepository.existsTraineeByUser_Username(username)) {
            throw new TraineeNotFoundException("Trainee not found");
        }
        return trainerTraineeRepository.findByUsernameNotAssignedToTrainee(username)
                .stream().map(trainerMapper::toTrainerResponseDTO).toList();
    }


    private TrainerTraineeRecord getTrainerTraineeRecord(String currentUsername, String trainerUsername) {
        Trainee trainee = traineeRepository.findTraineeByUser_Username(currentUsername)
                .orElseThrow(() -> new TraineeNotFoundException(currentUsername));
        Trainer trainer = trainerRepository.findTrainerByUser_Username(trainerUsername)
                .orElseThrow(() -> new TrainerNotFoundException(trainerUsername));
        return new TrainerTraineeRecord(trainee, trainer);
    }

    private record TrainerTraineeRecord(Trainee trainee, Trainer trainer) {
    }

}
