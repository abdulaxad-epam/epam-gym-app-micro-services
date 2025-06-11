package epam.client.messaging.consumer.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import epam.client.dto.TrainerWorkloadRequestDTO;
import epam.client.messaging.consumer.DeadLetterQueueConsumer;
import epam.client.messaging.producer.TrainerMessageProducer;
import epam.client.service.impl.TrainerWorkloadMessagePropertiesBuilder;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class DeadLetterQueueConsumerImpl implements DeadLetterQueueConsumer {

    private final TrainerMessageProducer trainerMessageProducer;

    private final TrainerWorkloadMessagePropertiesBuilder propertiesBuilder;

    private final ObjectMapper objectMapper;

    public static final String DEADLETTER_QUEUE = "ActiveMQ.DLQ";

    @JmsListener(destination = DEADLETTER_QUEUE)
    public void deadLetterConsumer(Message message, @Header(name = "JMSXDeliveryCount", required = false) Integer deliveryCount) {
        log.info("DeadLetterQueue consumer called");
        try {
            if (message instanceof TextMessage textMessage) {
                String payload = textMessage.getText();

                TrainerWorkloadRequestDTO trainerWorkloadRequestDTO = objectMapper.readValue(payload, TrainerWorkloadRequestDTO.class);

                log.info("Deserialized TrainerWorkloadRequestDTO: {}", trainerWorkloadRequestDTO.toString());

                if (deliveryCount != null && deliveryCount < 5) {
                    log.info("Retrying to deliver trainerWorkloadRequestDTO: {}", trainerWorkloadRequestDTO);
                    MessagePostProcessor url = propertiesBuilder.buildMessagePropertyOnProduce();
                    trainerMessageProducer.produceOnAction(url, trainerWorkloadRequestDTO);
                } else {
                    log.error("Unable to deliver trainerWorkloadRequestDTO counts {}: {}", deliveryCount, trainerWorkloadRequestDTO);
                }
            } else {
                log.warn("Received non-TextMessage in DLQ: {}", message.getClass().getName());
            }
        } catch (JmsException e) {
            log.error("Exception occurred while consuming dead letter queue {}", e.getMessage());
        } catch (Exception e) {
            log.error("Exception on consuming dead letter queue {}", e.getMessage() + e.getCause());
        }

    }
}
