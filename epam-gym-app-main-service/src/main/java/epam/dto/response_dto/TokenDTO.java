package epam.dto.response_dto;


import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenDTO implements Serializable {
    private String accessToken;
    private String refreshToken;
}
