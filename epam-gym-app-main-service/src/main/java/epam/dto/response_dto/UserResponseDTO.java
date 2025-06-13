package epam.dto.response_dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO implements Serializable {

    private String firstName;

    private String lastName;

    private String username;

    private Boolean isActive;
}
