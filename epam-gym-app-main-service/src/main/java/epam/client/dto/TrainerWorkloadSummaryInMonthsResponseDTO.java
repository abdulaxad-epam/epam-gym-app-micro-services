package epam.client.dto;


import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TrainerWorkloadSummaryInMonthsResponseDTO {
    private String month;
    private String durationInMinutes;
}

