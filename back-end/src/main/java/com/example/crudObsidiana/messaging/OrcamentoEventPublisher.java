package com.example.crudObsidiana.messaging;

import com.example.crudObsidiana.dto.OrcamentoStatusEvent;
import com.example.crudObsidiana.model.Orcamento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrcamentoEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrcamentoEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${orcamento.rabbitmq.exchange}")
    private String exchange;

    @Value("${orcamento.rabbitmq.routing-key}")
    private String routingKey;

    public OrcamentoEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarMudancaStatus(Orcamento orcamento,
                                      String statusAnterior,
                                      String novoStatus) {
        List<OrcamentoStatusEvent.UsoEquipamentoEvent> usos =
                orcamento.getUsosEquipamentos() == null
                        ? Collections.emptyList()
                        : orcamento.getUsosEquipamentos().stream()
                        .filter(u -> u != null && u.getEquipamento() != null)
                        .map(u -> new OrcamentoStatusEvent.UsoEquipamentoEvent(
                                u.getEquipamento().getId(),
                                u.getQuantidadeUsada()))
                        .collect(Collectors.toList());

        OrcamentoStatusEvent event = new OrcamentoStatusEvent(
                orcamento.getId(), statusAnterior, novoStatus, usos);

        rabbitTemplate.convertAndSend(exchange, routingKey, event);

        log.info("[Publisher] Evento publicado — orçamento {} | {} -> {}",
                orcamento.getId(), statusAnterior, novoStatus);
    }
}