package epam.messaging.consumer;

import epam.aop.Logging;
import epam.dto.TrainerWorkloadRequestDTO;
import epam.dto.TrainerWorkloadResponseDTO;
import epam.service.TrainerWorkloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainerActionConsumer {

    private final TrainerWorkloadService trainerWorkloadService;

    @JmsListener(destination = "trainer.workload.queue")
    public void receiveActionMessage(TrainerWorkloadRequestDTO trainerWorkloadRequestDTO,
                                     @Header(name = "X-Transaction-ID") String transactionId) {
        ThreadLocal<String> threadLocal = new ThreadLocal<>();
        threadLocal.set(transactionId);
        Logging.setTransactionId(threadLocal);
        TrainerWorkloadResponseDTO trainerWorkloadResponseDTO = trainerWorkloadService
                .actionOn(trainerWorkloadRequestDTO);

        log.info("Trainer action message received: {}", trainerWorkloadResponseDTO);
    }
}
