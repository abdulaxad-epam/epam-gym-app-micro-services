package epam.dto.response_dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserAuthenticationResponseDTO implements Serializable {

    private String username;

    private Boolean isActive;

    private String password;
}
