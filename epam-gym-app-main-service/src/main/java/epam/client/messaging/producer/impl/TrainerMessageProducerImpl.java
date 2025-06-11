package epam.client.messaging.producer.impl;

import epam.client.dto.TrainerWorkloadRequestDTO;
import epam.client.dto.TrainerWorkloadResponseDTO;
import epam.client.messaging.producer.TrainerMessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerMessageProducerImpl implements TrainerMessageProducer {

    @Value("${spring.activemq.trainer-workload-queue}")
    private String trainerWorkloadQueue;

    private final JmsTemplate jmsTemplate;

    @Transactional(value = "jmsTransactionManager")
    @Override
    public void produceOnAction(MessagePostProcessor url, TrainerWorkloadRequestDTO dto) {
        log.info("Post training action");
        jmsTemplate.convertAndSend(trainerWorkloadQueue, dto, url);
    }
}
