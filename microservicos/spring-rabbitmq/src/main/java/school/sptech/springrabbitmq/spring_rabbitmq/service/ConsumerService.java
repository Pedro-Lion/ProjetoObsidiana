package school.sptech.springrabbitmq.spring_rabbitmq.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import school.sptech.springrabbitmq.spring_rabbitmq.dto.MessageDto;

@Service
public class ConsumerService {

    @RabbitListener(queues = "${broker.queue.name}")
    public void receive(MessageDto dto){
        System.out.println("Received message:" + dto.message());
    }
}
