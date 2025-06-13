package epam.dto.response_dto;


import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TrainerResponseDTO implements Serializable {

    private String trainerSpecialization;

    private UserResponseDTO user;

    private TraineeResponseDTO trainees;
}
