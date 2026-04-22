package com.example.crudObsidiana.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQPublisher {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarEvento(Object evento) {
        rabbitTemplate.convertAndSend(
                "domain.events.exchange",
                "orcamento.confirmado",
                evento
        );
    }
}