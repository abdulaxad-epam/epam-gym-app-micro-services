package epam.service.impl;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@AllArgsConstructor
@EnableScheduling
@Component
public class TrainerWorkloadCleaner {

    private final MongoTemplate mongoTemplate;

    @Scheduled(fixedRate = 120000, initialDelay = 120000)
    @Transactional
    public void cleanUpNonWorkingTrainerWorkloads() {
        log.info("Cleaning up trainer workloads");
        mongoTemplate.remove(Query.query(Criteria.where("trainingYears.trainingMonths.trainingsSummaryDuration").lte("0")),
                "trainer_workload");
    }
}
