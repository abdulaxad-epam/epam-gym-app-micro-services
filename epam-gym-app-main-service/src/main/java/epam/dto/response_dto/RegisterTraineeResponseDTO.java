package epam.dto.response_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Builder
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterTraineeResponseDTO implements Serializable {

    private UserResponseDTO user;

    private String traineeDateOfBirth;

    private String address;

}
