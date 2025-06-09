package epam.repository;

import epam.entity.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TrainingTypeRepository extends JpaRepository<TrainingType, UUID> {
    Optional<TrainingType> findTrainingTypeByDescription(String trainingName);
}
