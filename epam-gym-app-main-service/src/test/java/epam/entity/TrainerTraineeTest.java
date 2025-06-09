package epam.entity;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@ExtendWith(InstancioExtension.class)
class TrainerTraineeTest {

    private TrainerTrainee trainerTrainee;
    private TrainerTrainee.TraineeTrainerId traineeTrainerId;
    private Trainee trainee;
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        traineeTrainerId = new TrainerTrainee.TraineeTrainerId(UUID.randomUUID(), UUID.randomUUID());

        trainee = Instancio.of(Trainee.class).create();
        trainer = Instancio.of(Trainer.class).create();

        trainerTrainee = TrainerTrainee.builder()
                .id(traineeTrainerId)
                .trainee(trainee)
                .trainer(trainer)
                .build();
    }

    @Test
    void testEntityCreation() {
        assertNotNull(trainerTrainee);
        assertNotNull(trainerTrainee.getId());
        assertEquals(traineeTrainerId, trainerTrainee.getId());
    }

    @Test
    void testTraineeAssociation() {
        assertNotNull(trainerTrainee.getTrainee());
        assertEquals(trainee.getTraineeId(), trainerTrainee.getTrainee().getTraineeId());
    }

    @Test
    void testTrainerAssociation() {
        assertNotNull(trainerTrainee.getTrainer());
        assertEquals(trainer.getTrainerId(), trainerTrainee.getTrainer().getTrainerId());
    }

    @Test
    void testEmbeddedId() {
        assertEquals(traineeTrainerId.getTraineeId(), trainerTrainee.getId().getTraineeId());
        assertEquals(traineeTrainerId.getTrainerId(), trainerTrainee.getId().getTrainerId());
    }
}
