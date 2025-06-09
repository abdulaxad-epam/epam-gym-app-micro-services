package epam.mapper;

import epam.dto.request_dto.RegisterTraineeRequestDTO;
import epam.entity.Trainee;
import epam.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class TraineeMapperTest {

    private final TraineeMapper traineeMapper = TraineeMapper.INSTANCE;

    @Test
    void instance_ShouldNotBeNull() {
        assertThat(TraineeMapper.INSTANCE).isNotNull();
    }

    @Test
    void shouldMapRegisterTraineeRequestDTOToTrainee() {
        // Given
        RegisterTraineeRequestDTO registerDTO = new RegisterTraineeRequestDTO();
        registerDTO.setDateOfBirth(LocalDate.of(1998, 8, 25));
        registerDTO.setAddress("789 Boulevard, State");

        User connectedUser = new User();
        connectedUser.setFirstname("Alice");
        connectedUser.setLastname("Smith");
        connectedUser.setUsername("alicesmith");
        connectedUser.setIsActive(true);

        // When
        Trainee trainee = traineeMapper.toTrainee(registerDTO, connectedUser);

        // Then
        assertThat(trainee).isNotNull();
        assertThat(trainee.getDateOfBirth()).isEqualTo(LocalDate.of(1998, 8, 25));
        assertThat(trainee.getAddress()).isEqualTo("789 Boulevard, State");
        assertThat(trainee.getUser()).isNotNull();
        assertThat(trainee.getUser().getFirstname()).isEqualTo("Alice");
        assertThat(trainee.getUser().getLastname()).isEqualTo("Smith");
        assertThat(trainee.getUser().getUsername()).isEqualTo("alicesmith");
    }
}
