package epam.client.service.impl;

import epam.aop.Logging;
import epam.client.dto.TrainerWorkloadRequestDTO;
import epam.client.dto.TrainerWorkloadResponseDTO;
import epam.client.dto.TrainerWorkloadSummaryResponseDTO;
import epam.client.service.TrainerRestClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class TrainerRestClientServiceImpl implements TrainerRestClientService {

    private final RestTemplate restTemplate;

    @Override
    public ResponseEntity<TrainerWorkloadResponseDTO> postTrainingAction(String url, TrainerWorkloadRequestDTO dto, String token) {
        HttpHeaders headers = new HttpHeaders();
        String transactionId = Logging.getTransactionId();

        headers.set("X-Internal-Token", "Bearer " + token);
        headers.set("X-Transaction-ID", transactionId);
        return restTemplate.postForEntity(url, new HttpEntity<>(dto, headers), TrainerWorkloadResponseDTO.class);
    }

    @Override
    public ResponseEntity<TrainerWorkloadSummaryResponseDTO> getTrainingSummary(String url, String token) {
        HttpHeaders headers = new HttpHeaders();
        String transactionId = Logging.getTransactionId();
        headers.set("X-Internal-Token", "Bearer " + token);
        headers.set("X-Transaction-ID", transactionId);
        return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<>() {
        });
    }


}
