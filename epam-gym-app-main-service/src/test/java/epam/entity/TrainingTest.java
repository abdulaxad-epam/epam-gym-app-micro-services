package epam.entity;

import org.instancio.Instancio;
import org.instancio.Select;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(InstancioExtension.class)
class TrainingTest {

    private Training training;
    private UUID trainingId;
    private LocalDateTime trainingDate;

    @BeforeEach
    void setUp() {
        trainingId = UUID.randomUUID();
        trainingDate = LocalDateTime.now();

        training = Instancio.of(Training.class)
                .set(Select.field(Training::getTrainingId), trainingId)
                .set(Select.field(Training::getTrainingName), "Strength Training")
                .set(Select.field(Training::getTrainingDate), trainingDate)
                .set(Select.field(Training::getTrainingDuration), 60)
                .create();
    }

    @Test
    void testTrainingCreation() {
        assertNotNull(training);
        assertEquals(trainingId, training.getTrainingId());
        assertEquals("Strength Training", training.getTrainingName());
        assertEquals(trainingDate, training.getTrainingDate());
        assertEquals(60, training.getTrainingDuration());
    }

    @Test
    void testTrainingEquality() {
        Training anotherTraining = Instancio.of(Training.class)
                .set(Select.field(Training::getTrainingId), trainingId)
                .set(Select.field(Training::getTrainingName), "Strength Training")
                .set(Select.field(Training::getTrainingDate), trainingDate)
                .set(Select.field(Training::getTrainingDuration), 60)
                .create();

        assertEquals(training.getTrainingId(), anotherTraining.getTrainingId());
        assertEquals(training.getTrainingName(), anotherTraining.getTrainingName());
        assertEquals(training.getTrainingDate(), anotherTraining.getTrainingDate());
        assertEquals(training.getTrainingDuration(), anotherTraining.getTrainingDuration());
    }

    @Test
    void testTrainingInequality() {
        Training differentTraining = Instancio.of(Training.class)
                .set(Select.field(Training::getTrainingId), UUID.randomUUID())
                .set(Select.field(Training::getTrainingName), "Strength Training")
                .set(Select.field(Training::getTrainingDate), trainingDate)
                .set(Select.field(Training::getTrainingDuration), 60)
                .create();

        assertNotEquals(training, differentTraining);
    }

    @Test
    void testTrainingHandlesNullTrainee() {
        training.setTrainee(null);
        assertNull(training.getTrainee());
    }

    @Test
    void testTrainingHandlesNullTrainer() {
        training.setTrainer(null);
        assertNull(training.getTrainer());
    }

    @Test
    void testTrainingHandlesNullTrainingType() {
        training.setTrainingType(null);
        assertNull(training.getTrainingType());
    }

    @Test
    void testTrainingHandlesNullName() {
        training.setTrainingName(null);
        assertNull(training.getTrainingName());
    }

    @Test
    void testTrainingHandlesZeroDuration() {
        training.setTrainingDuration(0);
        assertEquals(0, training.getTrainingDuration());
    }

    @Test
    void testTrainingHandlesNegativeDuration() {
        training.setTrainingDuration(-10);
        assertTrue(training.getTrainingDuration() < 0);
    }

    @Test
    void testTrainingHandlesFutureDate() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(10);
        training.setTrainingDate(futureDate);
        assertTrue(training.getTrainingDate().isAfter(LocalDateTime.now()));
    }
}
