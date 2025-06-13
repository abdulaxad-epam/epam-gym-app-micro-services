package epam.messaging.producer;

import epam.dto.TrainerWorkloadSummaryInYearsResponseDTO;
import epam.dto.TrainerWorkloadSummaryResponseDTO;
import epam.messaging.producer.impl.TrainerActionProducerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

import java.util.Collections;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class TrainerActionProducerTest {

    @Value("${spring.activemq.trainer-workload-summary-queue}")
    private String trainerWorkloadSummaryQueue;

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private MessagePostProcessor messagePostProcessor;

    @InjectMocks
    private TrainerActionProducerImpl trainerActionProducer;

    private TrainerWorkloadSummaryResponseDTO testSummaryResponseDTO;

    @BeforeEach
    void setUp() {
        TrainerWorkloadSummaryInYearsResponseDTO yearSummary = TrainerWorkloadSummaryInYearsResponseDTO.builder()
                .year(String.valueOf(2023))
                .workloadSummaryInMonths(Collections.emptyList())
                .build();

        testSummaryResponseDTO = TrainerWorkloadSummaryResponseDTO.builder()
                .username("john.doe")
                .firstName("John")
                .lastName("Doe")
                .status(true)
                .workloadSummaryInYears(Collections.singletonList(yearSummary))
                .build();
    }


    @Test
    void testProduceOnAction() {
        trainerActionProducer.produceOnAction(messagePostProcessor, testSummaryResponseDTO);

        verify(jmsTemplate, times(1)).convertAndSend(
                eq(trainerWorkloadSummaryQueue),
                eq(testSummaryResponseDTO),
                eq(messagePostProcessor)
        );

        verifyNoMoreInteractions(jmsTemplate);
        verifyNoInteractions(messagePostProcessor);
    }
}
