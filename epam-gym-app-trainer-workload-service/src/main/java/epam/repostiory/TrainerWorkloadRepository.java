package epam.repostiory;

import epam.entity.TrainerWorkload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainerWorkloadRepository extends JpaRepository<TrainerWorkload, Long> {
    List<TrainerWorkload> findTrainerWorkloadsByTrainerUsername(String trainerUsername);

    Optional<TrainerWorkload> findTrainerWorkloadByTrainerUsernameAndTrainingDate(String trainerUsername, LocalDate trainingDate);

    List<TrainerWorkload> findTrainerWorkloadsByTrainerUsernameAndTrainingDate(String trainerUsername, LocalDate trainingDate);
}

