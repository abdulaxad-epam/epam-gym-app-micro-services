package epam.service;


import epam.service.impl.TrainerWorkloadMessagePropertiesBuilder;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.MessagePostProcessor;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class TrainerWorkloadMessagePropertiesBuilderTest {

    @InjectMocks
    private TrainerWorkloadMessagePropertiesBuilder builder;

    @Mock
    private Message mockMessage;

    private static final Integer TEST_YEAR = 2024;
    private static final Integer TEST_MONTH = 7;
    private static final String EXPECTED_TYPE = "epam.client.dto.TrainerWorkloadSummaryResponseDTO";

    @Test
    void testBuildMessagePropertyOnProduce() throws JMSException {
        Integer year = TEST_YEAR;
        Integer month = TEST_MONTH;

        MessagePostProcessor postProcessor = builder.buildMessagePropertyOnProduce(year, month);

        postProcessor.postProcessMessage(mockMessage);

        verify(mockMessage, times(1)).setStringProperty(eq("_type"), eq(EXPECTED_TYPE));

        verify(mockMessage, times(1)).setIntProperty(eq("year"), eq(year));

        verify(mockMessage, times(1)).setIntProperty(eq("month"), eq(month));

        verifyNoMoreInteractions(mockMessage);
    }
}

