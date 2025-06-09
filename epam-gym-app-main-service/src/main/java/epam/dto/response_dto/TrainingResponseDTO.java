package epam.dto.response_dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TrainingResponseDTO implements Serializable {
    private UUID trainingId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime trainingDate;

    private String trainingType;

    private Integer trainingDuration;

    private TraineeResponseDTO trainee;

    private TrainerResponseDTO trainer;

    private String trainingName;

}
