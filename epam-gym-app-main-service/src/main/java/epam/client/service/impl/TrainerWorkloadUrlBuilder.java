package epam.client.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainerWorkloadUrlBuilder {

    @Value("${training.service.name}")
    private String trainingServiceName;

    public String buildEndpointUrl(String endpoint) {
        return "http://" + trainingServiceName + "/api/v1/trainer-workload" + endpoint;
    }

    public String buildWorkloadSummaryUrl(String trainerUsername, Integer year, Integer month) {
        return buildEndpointUrl("-summary") + "?trainerUsername=" + trainerUsername + "&year=" + year + "&month=" + month;
    }
}
