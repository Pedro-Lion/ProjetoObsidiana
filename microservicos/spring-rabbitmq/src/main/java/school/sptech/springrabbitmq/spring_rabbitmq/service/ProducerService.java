package school.sptech.springrabbitmq.spring_rabbitmq.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import school.sptech.springrabbitmq.spring_rabbitmq.dto.MessageDto;
import school.sptech.springrabbitmq.spring_rabbitmq.dto.RabbitPropertiesConfiguration;

@Service
@RequiredArgsConstructor
public class ProducerService {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitPropertiesConfiguration properties;

    public void send(MessageDto message) {
        String exchangeName = properties.exchange().name();
        String routingKey = "";

        rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
    }
}