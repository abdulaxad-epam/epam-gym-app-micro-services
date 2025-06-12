package epam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TrainerWorkloadResponseDTO {
    private String trainerUsername;

    private String trainerFirstName;

    private String trainerLastName;

    private LocalDate trainingDate;

    private Integer trainingDurationInMinutes;
}
