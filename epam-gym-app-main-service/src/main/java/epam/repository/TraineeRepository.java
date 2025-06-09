package epam.repository;

import epam.entity.Trainee;
import epam.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface TraineeRepository extends JpaRepository<Trainee, UUID> {

    Optional<Trainee> findTraineeByUser_Username(String username);

    boolean existsTraineeByUser_Username(String username);

    @Modifying
    void deleteTraineeByUser_Username(String username);

    @Query("SELECT t FROM Training t " +
            "JOIN t.trainee tr " +
            "JOIN tr.user u " +
            "JOIN t.trainer trn " +
            "JOIN trn.user tu " +
            "WHERE u.username = :username " +
            "AND t.trainingDate >= :periodFrom " +
            "AND t.trainingDate <= :periodTo " +
            "AND (:trainerName = '' OR tu.username = :trainerName) " +
            "AND (:trainingType = '' OR t.trainingType.description = :trainingType)")
    Optional<List<Training>> getTraineeTrainings(
            @Param("username") String username,
            @Param("periodFrom") LocalDateTime periodFrom,
            @Param("periodTo") LocalDateTime periodTo,
            @Param("trainerName") String trainerName,
            @Param("trainingType") String trainingType
    );
}

