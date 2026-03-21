package school.sptech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AuthMicroserviceApplication  {
    public static void main(String[] args) {
        SpringApplication.run(AuthMicroserviceApplication.class, args);
    }
}