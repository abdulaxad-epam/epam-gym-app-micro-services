package epam.dto.request_dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Builder
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TrainerRequestDTO implements Serializable {

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @Valid
    @NotNull(message = "User details are required")
    private UserRequestDTO user;
}
