package epam.dto.request_dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
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
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO implements Serializable {

    @NotBlank(message = "First name is required")
    @Size(min = 4, message = "First name must have at least 4 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 4, message = "Last name must have at least 4 characters")
    private String lastName;

    @NotNull(message = "isActive status must be provided")
    private Boolean isActive;

    @Null
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private transient String role;

    @Null
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private transient String password;
}
