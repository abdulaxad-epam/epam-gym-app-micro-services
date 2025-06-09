package epam.repository;

import epam.entity.Trainer;
import epam.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Integer> {

    Optional<Trainer> findTraineeByUser_Username(String username);

    @Modifying
    void deleteTrainerByUser_Username(String username);

    @Query("SELECT t FROM Training t " +
            "JOIN t.trainer tr " +
            "JOIN tr.user u " +
            "WHERE u.username = :username " +
            "AND t.trainingDate >= :periodFrom " +
            "AND t.trainingDate <= :periodTo " +
            "AND (:traineeName IS NULL OR t.trainee.user.username = :traineeName)")
    Optional<List<Training>> getTrainerTrainings(
            @Param("username") String username,
            @Param("periodFrom") LocalDateTime periodFrom,
            @Param("periodTo") LocalDateTime periodTo,
            @Param("traineeName") String traineeName
    );
}
