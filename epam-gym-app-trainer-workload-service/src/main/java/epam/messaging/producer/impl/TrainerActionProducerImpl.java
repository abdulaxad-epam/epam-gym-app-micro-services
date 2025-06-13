package epam.messaging.producer.impl;

import epam.dto.TrainerWorkloadSummaryResponseDTO;
import epam.messaging.producer.TrainerActionProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TrainerActionProducerImpl implements TrainerActionProducer {
    private final JmsTemplate jmsTemplate;

    @Value("${spring.activemq.trainer-workload-summary-queue}")
    private String trainerWorkloadSummaryQueue;

    @Transactional(value = "jmsTransactionManager")
    @Override
    public void produceOnAction(MessagePostProcessor messagePostProcessor, TrainerWorkloadSummaryResponseDTO summaryResponseDTO) {
        jmsTemplate.convertAndSend(trainerWorkloadSummaryQueue, summaryResponseDTO, messagePostProcessor);
    }

}
