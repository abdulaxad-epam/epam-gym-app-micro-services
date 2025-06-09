package epam.entity;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeTest {

    @Mock
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldPersistTraineeWithUser() {
        // Given
        User user = User.builder()
                .firstname("First Name")
                .lastname("Last Name")
                .username("username")
                .password("password")
                .isActive(true)
                .build();

        Trainee trainee = Trainee.builder()
                .traineeId(UUID.randomUUID())
                .dateOfBirth(LocalDate.of(1995, 5, 15))
                .address("123 Main Street")
                .user(user)
                .build();

        // When
        when(entityManager.find(Trainee.class, trainee.getTraineeId())).thenReturn(trainee);

        entityManager.persist(user);
        entityManager.persist(trainee);

        // Then
        Trainee foundTrainee = entityManager.find(Trainee.class, trainee.getTraineeId());
        assertThat(foundTrainee).isNotNull();
        assertThat(foundTrainee.getUser()).isNotNull();
        assertThat(foundTrainee.getUser().getUsername()).isEqualTo("username");

        // Verify interactions
        verify(entityManager, times(1)).persist(user);
        verify(entityManager, times(1)).persist(trainee);
        verify(entityManager, times(1)).find(Trainee.class, trainee.getTraineeId());
    }
}
