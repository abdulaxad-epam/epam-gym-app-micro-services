package epam.client.messaging.producer;

import epam.client.dto.TrainerWorkloadRequestDTO;
import epam.client.dto.TrainerWorkloadResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.MessagePostProcessor;

public interface TrainerMessageProducer {
    void produceOnAction(MessagePostProcessor url, TrainerWorkloadRequestDTO dto);
}
