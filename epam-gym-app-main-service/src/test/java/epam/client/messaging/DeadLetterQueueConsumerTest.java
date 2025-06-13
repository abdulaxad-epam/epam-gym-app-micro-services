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

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeadLetterQueueConsumerTest {

    private static final String DEADLETTER_QUEUE_COUNTER_FIELD = "DEADLETTER_QUEUE_COUNTER";
    private Map<String, Integer> staticCounterMap;

    @Mock
    private TrainerMessageProducer trainerMessageProducer;
    @Mock
    private TrainerWorkloadMessagePropertiesBuilder propertiesBuilder;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private TextMessage textMessage;
    @Mock
    private Message otherMessage;

    @InjectMocks
    private DeadLetterQueueConsumerImpl deadLetterQueueConsumer;

    private TrainerWorkloadRequestDTO testDto;
    private String testPayload;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException, JMSException {

        java.lang.reflect.Field field = DeadLetterQueueConsumerImpl.class.getDeclaredField(DEADLETTER_QUEUE_COUNTER_FIELD);
        field.setAccessible(true);
        staticCounterMap = (ConcurrentHashMap<String, Integer>) field.get(null);
        staticCounterMap.clear();

        testDto = TrainerWorkloadRequestDTO.builder()
                .trainerUsername("john.doe")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .isActive(true)
                .trainingDate(LocalDate.of(2025, 7, 1))
                .trainingDurationInMinutes(60)
                .actionType("ADD")
                .build();
        testPayload = "{\"trainerUsername\":\"john.doe\", ...}";

        // Common mock behaviors
        lenient().when(textMessage.getText()).thenReturn(testPayload);
        lenient().when(objectMapper.readValue(testPayload, TrainerWorkloadRequestDTO.class)).thenReturn(testDto);
        lenient().when(propertiesBuilder.buildMessagePropertyOnProduce()).thenReturn(mock(MessagePostProcessor.class));
    }

    @Test
    @DisplayName("Should retry message on first attempt for a trainer")
    void shouldRetryOnFirstAttempt() {
        deadLetterQueueConsumer.deadLetterConsumer(textMessage);

        verify(trainerMessageProducer, times(1)).produceOnAction(any(MessagePostProcessor.class), eq(testDto));

        assertEquals(1, staticCounterMap.get(testDto.getTrainerUsername()));
    }

    @Test
    @DisplayName("Should retry message on second attempt for the same trainer")
    void shouldRetryOnSecondAttempt() {
        staticCounterMap.put(testDto.getTrainerUsername(), 1);

        deadLetterQueueConsumer.deadLetterConsumer(textMessage);

        verify(trainerMessageProducer, times(1)).produceOnAction(any(MessagePostProcessor.class), eq(testDto));
        assertEquals(2, staticCounterMap.get(testDto.getTrainerUsername()));
    }

    @Test
    @DisplayName("Should retry message on fourth attempt (count 4), the last successful retry")
    void shouldRetryOnFourthAttempt() {
        staticCounterMap.put(testDto.getTrainerUsername(), 3);

        deadLetterQueueConsumer.deadLetterConsumer(textMessage);

        verify(trainerMessageProducer, times(1)).produceOnAction(any(MessagePostProcessor.class), eq(testDto));
        assertEquals(4, staticCounterMap.get(testDto.getTrainerUsername()));
    }

    @Test
    @DisplayName("Should NOT retry message on fifth attempt (count 5), and remove from counter")
    void shouldNotRetryOnFifthAttemptAndRemove() {
        staticCounterMap.put(testDto.getTrainerUsername(), 4);

        deadLetterQueueConsumer.deadLetterConsumer(textMessage);

        verify(trainerMessageProducer, never()).produceOnAction(any(MessagePostProcessor.class), any(TrainerWorkloadRequestDTO.class));
        assertFalse(staticCounterMap.containsKey(testDto.getTrainerUsername()), "Entry should be removed from map");
    }

    @Test
    @DisplayName("Should handle non-TextMessage and log warning")
    void shouldHandleNonTextMessage() {
        deadLetterQueueConsumer.deadLetterConsumer(otherMessage);
        verify(trainerMessageProducer, never()).produceOnAction(any(), any());
    }

    @Test
    @DisplayName("Should handle JsonProcessingException during deserialization")
    void shouldHandleJsonProcessingException() throws JsonProcessingException, JMSException {
        when(objectMapper.readValue(testPayload, TrainerWorkloadRequestDTO.class)).thenThrow(mock(JsonProcessingException.class));

        deadLetterQueueConsumer.deadLetterConsumer(textMessage);

        verify(trainerMessageProducer, never()).produceOnAction(any(), any()); // No retry attempts
        assertTrue(staticCounterMap.isEmpty(), "Map should be empty or untouched if deserialization fails early");
    }

    @Test
    @DisplayName("Should handle generic JMSException during message processing")
    void shouldHandleJmsException() throws JMSException {
        when(textMessage.getText()).thenThrow(mock(JMSException.class));

        deadLetterQueueConsumer.deadLetterConsumer(textMessage);

        verify(trainerMessageProducer, never()).produceOnAction(any(), any());
        assertTrue(staticCounterMap.isEmpty(), "Map should be empty or untouched if JMS exception occurs early");
    }

    @Test
    @DisplayName("Should handle other generic Exceptions")
    void shouldHandleOtherExceptions() throws JsonProcessingException {
        when(objectMapper.readValue(testPayload, TrainerWorkloadRequestDTO.class)).thenReturn(testDto);
        when(propertiesBuilder.buildMessagePropertyOnProduce()).thenThrow(new RuntimeException("Simulated unexpected error"));

        deadLetterQueueConsumer.deadLetterConsumer(textMessage);

        assertEquals(1, staticCounterMap.get(testDto.getTrainerUsername()));
        verify(trainerMessageProducer, never()).produceOnAction(any(), any());
    }


    @Test
    @DisplayName("Should demonstrate shared counter flaw for different trainers")
    void shouldDemonstrateSharedCounterFlaw() {
        TrainerWorkloadRequestDTO trainer1 = testDto;
        TrainerWorkloadRequestDTO trainer2 = TrainerWorkloadRequestDTO.builder().trainerUsername("jane.doe").build();

        staticCounterMap.put(trainer1.getTrainerUsername(), 3);
        deadLetterQueueConsumer.deadLetterConsumer(textMessage);
        verify(trainerMessageProducer, times(1)).produceOnAction(any(), eq(trainer1));

        try {
            when(objectMapper.readValue(testPayload, TrainerWorkloadRequestDTO.class)).thenReturn(trainer2);
        } catch (JsonProcessingException e) {
            fail("Should not throw JsonProcessingException");
        }

        deadLetterQueueConsumer.deadLetterConsumer(textMessage);
        verify(trainerMessageProducer, times(1)).produceOnAction(any(), eq(trainer2));

        try {
            when(objectMapper.readValue(testPayload, TrainerWorkloadRequestDTO.class)).thenReturn(trainer1);
        } catch (JsonProcessingException e) {
            fail("Should not throw JsonProcessingException");
        }
        deadLetterQueueConsumer.deadLetterConsumer(textMessage);
        verify(trainerMessageProducer, times(1)).produceOnAction(any(), eq(trainer1));
        assertFalse(staticCounterMap.containsKey(trainer1.getTrainerUsername()), "John Doe's entry should be removed");

        assertEquals(1, staticCounterMap.get(trainer2.getTrainerUsername()));
    }
}