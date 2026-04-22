package com.example.crudObsidiana.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do RabbitMQ para o Projeto Obsidiana.
 *
 * Topologia das filas (POC):
 * ┌──────────────────────────────────────────────────────────┐
 * │  Exchange: orcamento.exchange  (Direct)                  │
 * │                                                          │
 * │  routing key: status.confirmado  →  fila.orcamento.confirmado │
 * │  routing key: status.cancelado   →  fila.orcamento.cancelado  │
 * └──────────────────────────────────────────────────────────┘
 *
 * Painel de gestão: http://localhost:15672  (admin / admin)
 */
@Configuration
public class RabbitMQConfig {

    // -------------------------------------------------------------------------
    // CONSTANTES — reutilizadas pelo Publisher e pelo Consumer
    // -------------------------------------------------------------------------

    /** Nome do exchange que roteia as mensagens de status do Orçamento */
    public static final String EXCHANGE        = "orcamento.exchange";

    /** Fila que recebe eventos de orçamentos confirmados */
    public static final String FILA_CONFIRMADO = "fila.orcamento.confirmado";

    /** Fila que recebe eventos de orçamentos cancelados */
    public static final String FILA_CANCELADO  = "fila.orcamento.cancelado";

    /** Routing key usada ao publicar um evento de confirmação */
    public static final String RK_CONFIRMADO   = "status.confirmado";

    /** Routing key usada ao publicar um evento de cancelamento */
    public static final String RK_CANCELADO    = "status.cancelado";

    // -------------------------------------------------------------------------
    // EXCHANGE
    // -------------------------------------------------------------------------

    /**
     * DirectExchange: cada mensagem é entregue exatamente na fila
     * cujo binding key bate com a routing key da mensagem.
     */
    @Bean
    public DirectExchange orcamentoExchange() {
        return new DirectExchange(EXCHANGE);
    }

    // -------------------------------------------------------------------------
    // FILAS (durable = true → sobrevivem a restart do broker)
    // -------------------------------------------------------------------------

    @Bean
    public Queue filaConfirmado() {
        return new Queue(FILA_CONFIRMADO, true);
    }

    @Bean
    public Queue filaCancelado() {
        return new Queue(FILA_CANCELADO, true);
    }

    // -------------------------------------------------------------------------
    // BINDINGS — ligam as filas ao exchange pelas routing keys
    // -------------------------------------------------------------------------

    @Bean
    public Binding bindingConfirmado(Queue filaConfirmado, DirectExchange orcamentoExchange) {
        return BindingBuilder
                .bind(filaConfirmado)
                .to(orcamentoExchange)
                .with(RK_CONFIRMADO);
    }

    @Bean
    public Binding bindingCancelado(Queue filaCancelado, DirectExchange orcamentoExchange) {
        return BindingBuilder
                .bind(filaCancelado)
                .to(orcamentoExchange)
                .with(RK_CANCELADO);
    }

    // -------------------------------------------------------------------------
    // CONVERSOR DE MENSAGENS — serializa/desserializa o payload como JSON
    // -------------------------------------------------------------------------

    /**
     * Usa Jackson para converter o OrcamentoStatusEventDTO para JSON
     * automaticamente ao publicar e ao consumir.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // -------------------------------------------------------------------------
    // RABBITTEMPLATE — cliente principal para publicar mensagens
    // -------------------------------------------------------------------------

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

} //FIM CLASSE
