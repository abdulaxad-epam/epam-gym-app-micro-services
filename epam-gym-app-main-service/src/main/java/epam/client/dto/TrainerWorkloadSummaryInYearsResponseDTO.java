package epam.client.dto;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TrainerWorkloadSummaryInYearsResponseDTO implements Serializable {
    private String year;
    private List<TrainerWorkloadSummaryInMonthsResponseDTO> workloadSummaryInMonths;
}
