package epam.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerTest {

    @Mock
    private User mockUser;

    @Mock
    private TrainingType mockTrainingType;

    private Trainer trainer;
    private UUID trainerId;

    @BeforeEach
    void setUp() {
        trainerId = UUID.randomUUID();

        trainer = Trainer.builder()
                .trainerId(trainerId)
                .user(mockUser)
                .specialization(mockTrainingType)
                .build();
    }

    @Test
    void testTrainerCreation() {

        when(mockTrainingType.getDescription()).thenReturn("Fitness");
        assertNotNull(trainer);
        assertEquals(trainerId, trainer.getTrainerId());
        assertEquals(mockUser, trainer.getUser());
        assertEquals(mockTrainingType, trainer.getSpecialization());
        assertEquals("Fitness", trainer.getSpecialization().getDescription());
    }

    @Test
    void testUpdatingTrainerDetails() {

        User newUser = mock(User.class);
        TrainingType newTrainingType = mock(TrainingType.class);

        trainer.setUser(newUser);
        trainer.setSpecialization(newTrainingType);

        //assertion
        assertEquals(newUser, trainer.getUser());
        assertEquals(newTrainingType, trainer.getSpecialization());
    }

    @Test
    void testTrainerEquality() {
        Trainer anotherTrainer = Trainer.builder()
                .trainerId(trainerId)
                .user(mockUser)
                .specialization(mockTrainingType)
                .build();

        assertEquals(trainer, anotherTrainer);
    }

    @Test
    void testTrainerNotEqual() {
        Trainer differentTrainer = Trainer.builder()
                .trainerId(UUID.randomUUID())
                .user(mockUser)
                .specialization(mockTrainingType)
                .build();

        assertNotEquals(trainer, differentTrainer);
    }
}
