package epam.dto.request_dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Trainer username is required")
    @Size(min = 7, max = 30, message = "Trainer username must be between 10 and 30 characters long")
    @Pattern(regexp = "^[a-zA-Z]+\\.[a-zA-Z]+[0-9]{0,2}$",
            message = "Trainer username must start with letters, contain exactly one dot ('.') followed by more letters, and optionally end with up to two digits. Length 5-30.")
    private String trainerUsername;

    @NotBlank(message = "Trainee username is required")
    @Size(min = 7, max = 30, message = "Trainee username must be between 10 and 30 characters long")
    @Pattern(regexp = "^[a-zA-Z]+\\.[a-zA-Z]+[0-9]{0,2}$",
            message = "Trainee username must start with letters, contain exactly one dot ('.') followed by more letters, and optionally end with up to two digits. Length 5-30.")
    private String traineeUsername;

    @NotBlank(message = "Training name is required")
    @Size(min = 4, max = 30, message = "Training name must be between 10 and 30 characters long")
    @Pattern(regexp = "^[a-zA-Z]+[a-zA-Z]*[0-9]*$", message = "Training name must contain only letters and digits, start with letters, and digits can only appear at the very end.")
    @Pattern(regexp = "^(?i)(?!true$|false$)^[a-zA-Z]+[a-zA-Z]*[0-9]*$",
            message = "Training name must not be boolean value!!!")
    private String trainingName;

    @FutureOrPresent(message = "The training start date must be in the future or today")
    @NotNull(message = "Training date must be specified")
    private LocalDate trainingDate;

    @NotBlank(message = "Training type cannot be blank or null")
    @Size(min = 3, max = 50, message = "Training type must be between 3 and 50 characters long")
    @Pattern(regexp = "^[a-zA-Z]+(?:[_-]?[a-zA-Z]+)*$", message = "Training type must start with letters, can contain letters, and optionally have underscores or hyphens between letter groups. Length 3-50.")
    @Pattern(regexp = "^(STRENGTH_TRAINING|CARDIOVASCULAR_TRAINING|HYPERTROPHY_TRAINING|FUNCTIONAL_TRAINING|FLEXIBILITY|YOGA|HIIT)$",
            message = "Invalid training type, currently on our gym only exists " +
                    "[ STRENGTH_TRAINING, CARDIOVASCULAR_TRAINING, HYPERTROPHY_TRAINING, FUNCTIONAL_TRAINING, FLEXIBILITY, YOGA, HIIT ]")
    private String trainingType;

    @Min(value = 30, message = "Training duration must be at least 30 minutes")
    @Max(value = 480, message = "Training duration cannot exceed 8 hours (480 minutes)")
    @NotNull(message = "Training duration is required")
    @Digits(integer = 3, fraction = 0, message = "Training duration must be a whole number with at most 3 digits.")
    private Integer trainingDurationInMinutes;
}
