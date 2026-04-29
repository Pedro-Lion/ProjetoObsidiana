package com.example.crudObsidiana.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${orcamento.rabbitmq.exchange}")
    private String exchange;

    @Value("${orcamento.rabbitmq.queue}")
    private String queue;

    @Value("${orcamento.rabbitmq.routing-key}")
    private String routingKey;

    @Bean
    public DirectExchange orcamentoExchange() {
        return new DirectExchange(exchange, true, false);
    }

    @Bean
    public Queue orcamentoQueue() {
        // durable=true: a fila sobrevive ao restart do RabbitMQ
        return QueueBuilder.durable(queue).build();
    }

    @Bean
    public Binding binding(Queue orcamentoQueue, DirectExchange orcamentoExchange) {
        return BindingBuilder.bind(orcamentoQueue).to(orcamentoExchange).with(routingKey);
    }

    // Serializa as mensagens como JSON automaticamente
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}