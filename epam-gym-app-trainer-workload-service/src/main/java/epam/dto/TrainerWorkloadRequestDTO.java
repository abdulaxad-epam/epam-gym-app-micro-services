package epam.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;


@Builder
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TrainerWorkloadRequestDTO implements Serializable {

    @NotBlank(message = "Trainer username is required")
    private String trainerUsername;

    @NotBlank(message = "Trainer firstname is required")
    private String trainerFirstName;

    @NotBlank(message = "Trainer lastname is required")
    private String trainerLastName;

    @NotNull(message = "IsActive field is required")
    private Boolean isActive;

    @NotNull(message = "Training date must be specified")
    private LocalDate trainingDate;

    @Min(value = 30, message = "Training duration must be at least 30 minute")
    @Max(value = 480, message = "Training duration cannot exceed 8 hours (480 minutes)")
    @NotNull(message = "Training duration is required")
    private Integer trainingDuration;

    @Pattern(regexp = "^(ADD|DELETE)$", message = "Action type has to be either ADD or DELETE")
    @NotBlank(message = "Action type is required")
    private String actionType;
}
