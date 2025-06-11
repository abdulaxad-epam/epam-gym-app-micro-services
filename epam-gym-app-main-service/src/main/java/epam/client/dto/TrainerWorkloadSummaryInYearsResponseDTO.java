package epam.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Builder
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TrainerWorkloadSummaryInYearsResponseDTO implements Serializable {
    private String year;
    private List<TrainerWorkloadSummaryInMonthsResponseDTO> workloadSummaryInMonths;
}
