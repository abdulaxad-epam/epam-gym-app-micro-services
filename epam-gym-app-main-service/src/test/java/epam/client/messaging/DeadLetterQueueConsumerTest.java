package epam.client.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import epam.client.dto.TrainerWorkloadRequestDTO;
import epam.client.messaging.consumer.impl.DeadLetterQueueConsumerImpl;
import epam.client.messaging.producer.TrainerMessageProducer;
import epam.client.service.impl.TrainerWorkloadMessagePropertiesBuilder;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.MessagePostProcessor;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeadLetterQueueConsumerTest {

    @Mock
    private TrainerMessageProducer trainerMessageProducer;
    @Mock
    private TrainerWorkloadMessagePropertiesBuilder propertiesBuilder;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private TextMessage mockTextMessage;
    @Mock
    private Message mockGenericMessage;

    @InjectMocks
    private DeadLetterQueueConsumerImpl deadLetterQueueConsumer;

    private TrainerWorkloadRequestDTO testDto;
    private String testPayload;
    private MessagePostProcessor mockMessagePostProcessor;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        testDto = new TrainerWorkloadRequestDTO();
        testDto.setTrainerUsername("test.trainer");
        testDto.setIsActive(true);
        testDto.setTrainingDuration(60);

        testPayload = "{\"trainerUsername\":\"test.trainer\",\"isActive\":true,\"trainingDuration\":60}";

        mockMessagePostProcessor = mock(MessagePostProcessor.class);

        lenient().when(objectMapper.readValue(eq(testPayload), eq(TrainerWorkloadRequestDTO.class))).thenReturn(testDto);
        lenient().when(propertiesBuilder.buildMessagePropertyOnProduce()).thenReturn(mockMessagePostProcessor);
    }

    @Test
    @DisplayName("Should deserialize, log, and retry when deliveryCount is less than 5")
    void deadLetterConsumer_retrySuccess_deliveryCountLessThan5() throws JMSException, JsonProcessingException {
        when(mockTextMessage.getText()).thenReturn(testPayload);

        deadLetterQueueConsumer.deadLetterConsumer(mockTextMessage, 3);

        verify(mockTextMessage).getText();
        verify(objectMapper).readValue(eq(testPayload), eq(TrainerWorkloadRequestDTO.class));
        verify(propertiesBuilder).buildMessagePropertyOnProduce();
        verify(trainerMessageProducer).produceOnAction(eq(mockMessagePostProcessor), eq(testDto));
    }

    @Test
    @DisplayName("Should deserialize, log error, and not retry when deliveryCount is 5 or more")
    void deadLetterConsumer_noRetry_deliveryCountGreaterThanOrEqualTo5() throws JMSException, JsonProcessingException {
        when(mockTextMessage.getText()).thenReturn(testPayload);

        deadLetterQueueConsumer.deadLetterConsumer(mockTextMessage, 5);

        verify(mockTextMessage).getText();
        verify(objectMapper).readValue(eq(testPayload), eq(TrainerWorkloadRequestDTO.class));
        verifyNoInteractions(propertiesBuilder);
        verifyNoInteractions(trainerMessageProducer);
    }

    @Test
    @DisplayName("Should deserialize, log error, and not retry when deliveryCount is null")
    void deadLetterConsumer_noRetry_deliveryCountIsNull() throws JMSException, JsonProcessingException {
        when(mockTextMessage.getText()).thenReturn(testPayload);

        deadLetterQueueConsumer.deadLetterConsumer(mockTextMessage, null);

        verify(mockTextMessage).getText();
        verify(objectMapper).readValue(eq(testPayload), eq(TrainerWorkloadRequestDTO.class));
        verifyNoInteractions(propertiesBuilder);
        verifyNoInteractions(trainerMessageProducer);
    }

    @Test
    @DisplayName("Should log warning when message is not a TextMessage")
    void deadLetterConsumer_nonTextMessage_logsWarning() {

        deadLetterQueueConsumer.deadLetterConsumer(mockGenericMessage, 3);

        verifyNoInteractions(objectMapper);
        verifyNoInteractions(propertiesBuilder);
        verifyNoInteractions(trainerMessageProducer);
    }

    @Test
    @DisplayName("Should catch and log JmsException when getting text from message fails")
    void deadLetterConsumer_jmsExceptionOnGetText_logsError() throws JMSException {
        doThrow(new JMSException("Simulated JMS Exception during getText")).when(mockTextMessage).getText();

        deadLetterQueueConsumer.deadLetterConsumer(mockTextMessage, 3);

        verify(mockTextMessage).getText();
        verifyNoInteractions(objectMapper);
        verifyNoInteractions(propertiesBuilder);
        verifyNoInteractions(trainerMessageProducer);
    }

    @Test
    @DisplayName("Should catch and log Exception when JSON deserialization fails")
    void deadLetterConsumer_jsonProcessingException_logsError() throws JMSException, JsonProcessingException {
        when(mockTextMessage.getText()).thenReturn(testPayload);
        lenient().doThrow(new JsonProcessingException("Simulated JSON processing error") {
        }).when(objectMapper).readValue(eq(testPayload), eq(TrainerWorkloadRequestDTO.class));

        deadLetterQueueConsumer.deadLetterConsumer(mockTextMessage, 3);

        verify(mockTextMessage).getText();
        verify(objectMapper).readValue(eq(testPayload), eq(TrainerWorkloadRequestDTO.class));
        verifyNoInteractions(propertiesBuilder);
        verifyNoInteractions(trainerMessageProducer);
    }
}