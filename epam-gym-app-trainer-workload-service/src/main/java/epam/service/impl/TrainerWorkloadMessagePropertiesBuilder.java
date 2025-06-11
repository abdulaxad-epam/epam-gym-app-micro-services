package epam.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainerWorkloadMessagePropertiesBuilder {

    public MessagePostProcessor buildMessagePropertyOnProduce(Integer year, Integer month) {
        return message -> {
            message.setStringProperty("_type", "epam.client.dto.TrainerWorkloadSummaryResponseDTO");
            message.setIntProperty("year", year);
            message.setIntProperty("month", month);
            return message;
        };
    }
}
