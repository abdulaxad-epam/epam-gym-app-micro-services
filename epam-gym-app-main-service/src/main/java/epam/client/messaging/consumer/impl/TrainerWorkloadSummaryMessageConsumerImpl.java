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

    private final Map<String, Map<Integer, Map<Integer, TrainerWorkloadSummaryResponseDTO>>> trainerWorkloadSummaryPool = new ConcurrentHashMap<>();


    @Override
    public TrainerWorkloadSummaryResponseDTO getTrainerWorkloadSummary(String trainerUsername, Integer year, Integer month) {
        Map<Integer, Map<Integer, TrainerWorkloadSummaryResponseDTO>> yearsMap = trainerWorkloadSummaryPool.get(trainerUsername);
        if (yearsMap == null) {
            return null;
        }
        Map<Integer, TrainerWorkloadSummaryResponseDTO> monthsMap = yearsMap.get(year);
        if (monthsMap == null) {
            return null;
        }
        return monthsMap.get(month);
    }

    @JmsListener(destination = "trainer.workload.summary.queue")
    @Override
    public void consumeOnAction(TrainerWorkloadSummaryResponseDTO incomingDtoFromMessage, Message message)
            throws JMSException {
        log.info("Consuming trainer workload summary: {}", incomingDtoFromMessage);

        int year = message.getIntProperty("year");
        int month = message.getIntProperty("month");
        String trainerUsername = incomingDtoFromMessage.getUsername();

        Map<Integer, Map<Integer, TrainerWorkloadSummaryResponseDTO>> trainerYearsMap =
                trainerWorkloadSummaryPool.computeIfAbsent(trainerUsername, k -> new ConcurrentHashMap<>());

        Map<Integer, TrainerWorkloadSummaryResponseDTO> yearMonthsMap =
                trainerYearsMap.computeIfAbsent(year, k -> new ConcurrentHashMap<>());

        yearMonthsMap.put(month, incomingDtoFromMessage);

    }

    public void clearWorkloadSummaryPool() {
        this.trainerWorkloadSummaryPool.clear();
        log.info("TrainerWorkloadSummaryMessageConsumerImpl pool cleared for test.");
    }
}