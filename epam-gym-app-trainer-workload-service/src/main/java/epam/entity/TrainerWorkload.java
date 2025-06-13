package epam.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "trainer_workload")
public class TrainerWorkload {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID trainerWorkloadId;

    @Column(nullable = false)
    private String trainerUsername;

    @Column(nullable = false)
    private String trainerFirstName;

    @Column(nullable = false)
    private String trainerLastName;

    @Column(nullable = false)

    private Boolean isActive;

    @Column(nullable = false)
    private LocalDate trainingDate;

    @Column(nullable = false)
    private Integer trainingDuration;

}
