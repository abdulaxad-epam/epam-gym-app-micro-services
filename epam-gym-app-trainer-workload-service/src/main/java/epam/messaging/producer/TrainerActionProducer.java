package epam.messaging.producer;

import epam.dto.TrainerWorkloadSummaryResponseDTO;
import org.springframework.jms.core.MessagePostProcessor;

public interface TrainerActionProducer {
    void produceOnAction(MessagePostProcessor messagePostProcessor, TrainerWorkloadSummaryResponseDTO trainerWorkloadRequestDTO);
}
