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
public class TrainerWorkloadSummaryResponseDTO implements Serializable {

    private String username;

    private String firstName;

    private String lastName;

    private Boolean status;

    private List<TrainerWorkloadSummaryInYearsResponseDTO> workloadSummaryInYears;
}
