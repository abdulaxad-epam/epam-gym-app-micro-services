package epam.repository;

import epam.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrainingRepository extends JpaRepository<Training, UUID> {

    @Modifying
    void deleteTrainingByTrainingId(UUID trainingId);

    Optional<Training> findByTrainingIdAndTrainer_User_Username(UUID trainingId, String trainerUserUsername);

    List<Training> findTrainingsByTrainer_User_Username(String trainerUserUsername);
}
