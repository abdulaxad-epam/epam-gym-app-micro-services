package epam.dto.response_dto;


import lombok.*;

import java.io.Serializable;

@Builder
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TraineeResponseDTO implements Serializable {

    private String traineeDateOfBirth;

    private String address;

    private UserResponseDTO user;
}
