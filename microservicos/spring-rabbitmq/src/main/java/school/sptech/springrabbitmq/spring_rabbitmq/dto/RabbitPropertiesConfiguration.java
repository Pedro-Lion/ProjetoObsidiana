package school.sptech.springrabbitmq.spring_rabbitmq.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "broker")
public record RabbitPropertiesConfiguration(
        Exchange exchange,
        Queue queue
) {
    public record Exchange(String name){
    }

    public record Queue(String name){
    }
}
