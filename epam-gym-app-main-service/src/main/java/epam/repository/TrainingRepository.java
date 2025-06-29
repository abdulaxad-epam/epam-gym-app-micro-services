package epam.repository;

import epam.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrainingRepository extends JpaRepository<Training, UUID> {

    @Modifying
    void deleteTrainingByTrainingId(UUID trainingId);

    Optional<Training> findByTrainingIdAndTrainer_User_Username(UUID trainingId, String trainerUserUsername);

    List<Training> findTrainingsByTrainer_User_Username(String trainerUserUsername);

    Optional<Training> findTrainingByTrainingId(UUID trainingId);

    boolean existsByTrainingIdAndTrainee_User_Username(UUID trainingId, String traineeUserUsername);

    @Query("""
            SELECT COUNT(t)
                FROM Training t
                JOIN t.trainee tr
                JOIN tr.user tru
                JOIN t.trainer trn
                JOIN trn.user trnu
                WHERE t.trainingId = :trainingId
                AND (tru.username = :username OR trnu.username = :username)
            """)
    Long existsByTrainingIdAndUsername(UUID trainingId, String username);
}
