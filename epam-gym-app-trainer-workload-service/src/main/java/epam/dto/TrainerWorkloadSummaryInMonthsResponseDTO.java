package epam.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TrainerWorkloadSummaryInMonthsResponseDTO {
    private Integer month;
    private Integer durationInMinutes;
}

