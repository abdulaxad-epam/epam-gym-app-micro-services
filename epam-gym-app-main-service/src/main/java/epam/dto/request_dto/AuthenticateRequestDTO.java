package epam.dto.request_dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticateRequestDTO implements Serializable {


    @NotBlank(message = "Trainer username is required")
    @Size(min = 7, max = 30, message = "Trainer username must be between 10 and 30 characters long")
    @Pattern(regexp = "^[a-zA-Z]+\\.[a-zA-Z]+[0-9]{0,2}$",
            message = "Username must start with letters, contain exactly one dot ('.') followed by more letters, and optionally end with up to two digits. Length 5-30.")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}