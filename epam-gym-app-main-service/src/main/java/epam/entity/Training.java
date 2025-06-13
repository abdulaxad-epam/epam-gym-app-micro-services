package epam.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trainings")
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID trainingId;

    @JoinColumn(name = "trainee_id")
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Trainee.class)
    private Trainee trainee;

    @JoinColumn(name = "trainer_id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, targetEntity = Trainer.class)
    private Trainer trainer;

    @Column(nullable = false)
    private String trainingName;

    @Column(nullable = false)
    private LocalDateTime trainingDate;

    @JoinColumn(name = "training_type_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private TrainingType trainingType;

    @Column(nullable = false)
    private Integer trainingDuration;

}