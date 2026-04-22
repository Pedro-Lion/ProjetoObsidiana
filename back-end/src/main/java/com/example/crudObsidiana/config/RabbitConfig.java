package com.example.crudObsidiana.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("domain.events.exchange");
    }

    @Bean
    public Queue queue() {
        return new Queue("orcamento.confirmado.queue", true);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder
                .bind(queue())
                .to(exchange())
                .with("orcamento.confirmado");
    }
}