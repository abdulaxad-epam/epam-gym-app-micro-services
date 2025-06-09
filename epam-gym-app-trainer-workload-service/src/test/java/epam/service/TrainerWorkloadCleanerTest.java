package epam.service;

import epam.service.impl.TrainerWorkloadCleaner;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class TrainerWorkloadCleanerTest {

    private EntityManager entityManager;
    private TrainerWorkloadCleaner cleaner;
    private Query mockQuery;

    @BeforeEach
    void setUp() {
        entityManager = mock(EntityManager.class);
        cleaner = new TrainerWorkloadCleaner(entityManager);
        mockQuery = mock(Query.class);

        when(entityManager.createQuery(anyString())).thenReturn(mockQuery);
        when(mockQuery.executeUpdate()).thenReturn(1);
    }

    @Test
    void testCleanUpNonWorkingTrainerWorkloads_executesDeleteQuery() {
        cleaner.cleanUpNonWorkingTrainerWorkloads();

        verify(entityManager, times(1))
                .createQuery("DELETE FROM TrainerWorkload WHERE trainingDuration <= 0");
        verify(mockQuery, times(1)).executeUpdate();
    }
}
