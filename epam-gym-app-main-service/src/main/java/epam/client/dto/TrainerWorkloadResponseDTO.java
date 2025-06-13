package epam.client.dto;


import lombok.*;

import java.time.LocalDate;

@Builder
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TrainerWorkloadResponseDTO {
    private String trainerUsername;

    private String trainerFirstName;

    private String trainerLastName;

    private LocalDate trainingDate;

    private Integer trainingDurationInMinutes;
}
