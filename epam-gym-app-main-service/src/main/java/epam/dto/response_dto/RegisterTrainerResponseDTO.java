package epam.dto.response_dto;

import lombok.*;

import java.io.Serializable;

@Builder
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterTrainerResponseDTO implements Serializable {

    private UserResponseDTO user;

    private String trainerSpecialization;
}
