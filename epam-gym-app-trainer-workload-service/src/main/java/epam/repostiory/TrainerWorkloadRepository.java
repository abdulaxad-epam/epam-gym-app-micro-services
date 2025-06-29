package epam.repostiory;

import epam.entity.TrainerWorkload;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerWorkloadRepository extends MongoRepository<TrainerWorkload, Long> {

    @Query("{'trainerUsername': ?0}")
    TrainerWorkload findTrainerWorkloadByTrainerUsername(String trainerUsername);


}

