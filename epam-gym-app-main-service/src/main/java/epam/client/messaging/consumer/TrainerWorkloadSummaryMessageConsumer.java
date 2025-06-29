package epam.client.messaging.consumer;

import epam.client.dto.TrainerWorkloadSummaryResponseDTO;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.springframework.jms.annotation.JmsListener;

public interface TrainerWorkloadSummaryMessageConsumer {
    TrainerWorkloadSummaryResponseDTO getTrainerWorkloadSummary(String trainerUsername, Integer year, Integer month);

    @JmsListener(destination = "trainer.workload.summary.queue")
    void consumeOnAction(TrainerWorkloadSummaryResponseDTO incomingDtoFromMessage, Message message)
            throws JMSException;
}
