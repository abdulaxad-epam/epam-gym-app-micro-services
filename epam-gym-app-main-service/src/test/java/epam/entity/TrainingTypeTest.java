package epam.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TrainingTypeTest {

    private TrainingType trainingType;
    private UUID trainingTypeId;

    @BeforeEach
    void setUp() {
        trainingTypeId = UUID.randomUUID();
        trainingType = TrainingType.builder()
                .trainingTypeId(trainingTypeId)
                .description("Cardio Training")
                .build();
    }

    @Test
    void testTrainingTypeCreation() {
        assertNotNull(trainingType);
        assertEquals(trainingTypeId, trainingType.getTrainingTypeId());
        assertEquals("Cardio Training", trainingType.getDescription());
    }

    @Test
    void testTrainingTypeEquality() {
        TrainingType anotherTrainingType = TrainingType.builder()
                .trainingTypeId(trainingTypeId)
                .description("Cardio Training")
                .build();

        assertEquals(trainingType, anotherTrainingType);
    }

    @Test
    void testTrainingTypeInequality() {
        TrainingType differentTrainingType = TrainingType.builder()
                .trainingTypeId(UUID.randomUUID())
                .description("Strength Training")
                .build();

        assertNotEquals(trainingType, differentTrainingType);
    }

    @Test
    void testTrainingTypeHandlesNullDescription() {
        trainingType.setDescription(null);
        assertNull(trainingType.getDescription());
    }

    @Test
    void testTrainingTypeHandlesEmptyDescription() {
        trainingType.setDescription("");
        assertEquals("", trainingType.getDescription());
    }
}
