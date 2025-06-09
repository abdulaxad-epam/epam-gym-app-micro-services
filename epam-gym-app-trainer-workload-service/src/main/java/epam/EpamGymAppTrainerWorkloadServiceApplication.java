package epam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class EpamGymAppTrainerWorkloadServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EpamGymAppTrainerWorkloadServiceApplication.class, args);
    }

}
