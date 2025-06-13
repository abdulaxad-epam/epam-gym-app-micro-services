package epam.dto.response_dto;


import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TrainingTypeResponseDTO implements Serializable {
    private UUID trainingTypeId;
    private String trainingType;
}
