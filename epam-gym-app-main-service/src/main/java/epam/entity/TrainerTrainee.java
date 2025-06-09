package epam.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
