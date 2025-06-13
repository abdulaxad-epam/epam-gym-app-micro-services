package epam.client.messaging.consumer.impl;

import epam.client.dto.TrainerWorkloadSummaryResponseDTO;
import epam.client.messaging.consumer.TrainerWorkloadSummaryMessageConsumer;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class TrainerWorkloadSummaryMessageConsumerImpl implements TrainerWorkloadSummaryMessageConsumer {

    private final Map<String, Map<Integer, Map<Integer, TrainerWorkloadSummaryResponseDTO>>> trainer_workload_summary_pool = new ConcurrentHashMap<>();

    @Override
    public TrainerWorkloadSummaryResponseDTO getTrainerWorkloadSummary(String trainerUsername, Integer year,
                                                                       Integer month) {

        return trainer_workload_summary_pool.get(trainerUsername).get(year).get(month);

    }

    @JmsListener(destination = "trainer.workload.summary.queue")
    public void consumeOnAction(TrainerWorkloadSummaryResponseDTO trainerWorkloadSummaryResponseDTO, Message message)
            throws JMSException {
        log.info("Consuming trainer workload summary: {}", trainerWorkloadSummaryResponseDTO);

        int year = message.getIntProperty("year");
        int month = message.getIntProperty("month");

        Map<Integer, Map<Integer, TrainerWorkloadSummaryResponseDTO>> map = Map.of(year,
                Map.of(month, trainerWorkloadSummaryResponseDTO));

        trainer_workload_summary_pool.put(trainerWorkloadSummaryResponseDTO.getUsername(), map);

    }
}
