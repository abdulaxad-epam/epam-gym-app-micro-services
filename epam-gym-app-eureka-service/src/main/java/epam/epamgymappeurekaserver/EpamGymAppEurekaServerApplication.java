package epam.epamgymappeurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EpamGymAppEurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EpamGymAppEurekaServerApplication.class, args);
    }

}
