package epam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TrainerWorkloadSummaryInYearsResponseDTO implements Serializable {
    private Integer year;
    private List<TrainerWorkloadSummaryInMonthsResponseDTO> workloadSummaryInMonths;
}

