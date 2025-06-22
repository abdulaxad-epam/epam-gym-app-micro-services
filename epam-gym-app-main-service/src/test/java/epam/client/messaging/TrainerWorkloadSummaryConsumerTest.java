package epam.client.messaging;

import epam.client.dto.TrainerWorkloadSummaryResponseDTO;
import epam.client.messaging.consumer.impl.TrainerWorkloadSummaryMessageConsumerImpl;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainerWorkloadSummaryConsumerTest {

    @InjectMocks
    private TrainerWorkloadSummaryMessageConsumerImpl consumer;

    @Mock
    private Map<String, Map<Integer, Map<Integer, TrainerWorkloadSummaryResponseDTO>>> trainer_workload_summary_pool;

    @Mock
    private Message mockMessage;

    @BeforeEach
    void setUp() {
        trainer_workload_summary_pool = new ConcurrentHashMap<>();
    }

    @Test
    @DisplayName("Should throw NullPointerException when trainerUsername not found")
    void getTrainerWorkloadSummary_trainerNotFound_throwsNullPointerException() {
        String username = "non.existent";
        int year = 2023;
        int month = 10;

        assertNull(consumer.getTrainerWorkloadSummary(username, year, month));
    }

    @Test
    @DisplayName("Should throw NullPointerException when year not found")
    void getTrainerWorkloadSummary_yearNotFound_throwsNullPointerException() {
        String username = "john.doe";
        int year = 2023;
        int month = 10;

        trainer_workload_summary_pool.put(username, new ConcurrentHashMap<>());

        assertNull(consumer.getTrainerWorkloadSummary(username, year, month));
    }

    @Test
    @DisplayName("Should throw NullPointerException when month not found")
    void getTrainerWorkloadSummary_monthNotFound_throwsNullPointerException() {
        // Given
        String username = "john.doe";
        int year = 2023;
        int month = 10;

        trainer_workload_summary_pool.put(username, new ConcurrentHashMap<>());
        trainer_workload_summary_pool.get(username).put(year, new ConcurrentHashMap<>()); // Only year exists

        assertNull(consumer.getTrainerWorkloadSummary(username, year, month));
    }

    @Test
    @DisplayName("Should handle JMSException when year property is missing or invalid")
    void consumeOnAction_missingYearProperty_throwsJMSException() throws JMSException {
        TrainerWorkloadSummaryResponseDTO dto = new TrainerWorkloadSummaryResponseDTO();
        dto.setUsername("jane.doe");

        when(mockMessage.getIntProperty("year")).thenThrow(new JMSException("Year property missing"));

        assertThrows(JMSException.class, () -> {
            consumer.consumeOnAction(dto, mockMessage);
        });

        assertFalse(trainer_workload_summary_pool.containsKey("jane.doe"));
    }

    @Test
    @DisplayName("Should handle JMSException when month property is missing or invalid")
    void consumeOnAction_missingMonthProperty_throwsJMSException() throws JMSException {
        TrainerWorkloadSummaryResponseDTO dto = new TrainerWorkloadSummaryResponseDTO();
        dto.setUsername("jane.doe");

        int year = 2024;
        when(mockMessage.getIntProperty("year")).thenReturn(year);
        when(mockMessage.getIntProperty("month")).thenThrow(new JMSException("Month property missing"));


        assertThrows(JMSException.class, () -> {
            consumer.consumeOnAction(dto, mockMessage);
        });

        assertFalse(trainer_workload_summary_pool.containsKey("jane.doe"));
    }

}