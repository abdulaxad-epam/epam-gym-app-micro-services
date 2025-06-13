package epam.entity;


import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trainer_trainee")
public class TrainerTrainee {

    @EmbeddedId
    private TraineeTrainerId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("traineeId")
    @JoinColumn(name = "trainee_id")
    private Trainee trainee;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("trainerId")
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    @Data
    @Setter
    @Embeddable
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TraineeTrainerId implements Serializable {

        @Column(name = "trainee_id")
        private UUID traineeId;

        @Column(name = "trainer_id")
        private UUID trainerId;

    }

}
