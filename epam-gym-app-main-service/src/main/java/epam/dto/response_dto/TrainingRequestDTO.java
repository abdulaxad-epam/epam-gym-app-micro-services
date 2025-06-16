package epam.dto.response_dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;


@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TrainingRequestDTO implements Serializable {

    @NotBlank(message = "Trainee username is required")
    private String trainerUsername;

    @NotBlank(message = "Trainee username is required")
    private String traineeUsername;

    @NotBlank(message = "Training name is required")
    private String trainingName;

    @Future(message = "The training start date must be in the future")
    @NotNull(message = "Training date must be specified")
    private LocalDate trainingDate;

    @NotBlank(message = "Training type cannot be blank or null")
    private String trainingType;

    @Min(value = 30, message = "Training duration must be at least 30 minute")
    @Max(value = 480, message = "Training duration cannot exceed 8 hours (480 minutes)")
    @NotNull(message = "Training duration is required")
    private Integer trainingDurationInMinutes;
}
