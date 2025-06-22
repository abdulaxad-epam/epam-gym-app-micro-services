package epam.client.service.impl;


import epam.aop.Logging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainerWorkloadMessagePropertiesBuilder {

    public MessagePostProcessor buildMessagePropertyOnProduce() {
        return message -> {
            message.setStringProperty("_type", "epam.dto.TrainerWorkloadRequestDTO");
            message.setStringProperty("X-Transaction-ID", Logging.getTransactionId());
            return message;
        };
    }
}
