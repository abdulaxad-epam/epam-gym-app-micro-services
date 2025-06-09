package epam.service.impl;

import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@AllArgsConstructor
@EnableScheduling
@Component
public class TrainerWorkloadCleaner {

    private final EntityManager entityManager;

    @Scheduled(fixedRate = 120000, initialDelay = 120000)
    @Transactional
    public void cleanUpNonWorkingTrainerWorkloads() {
        log.info("Cleaning up trainer workloads");
        entityManager.createQuery("DELETE FROM TrainerWorkload WHERE trainingDuration <= 0").executeUpdate();
    }
}
