package com.example.crudObsidiana.observer;

import com.example.crudObsidiana.dto.OrcamentoStatusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrcamentoConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrcamentoConsumer.class);

    @RabbitListener(queues = "${orcamento.rabbitmq.queue}")
    public void consumir(OrcamentoStatusEvent evento) {
        log.info("[Consumer] Recebido evento — orçamento {} | {} -> {}",
                evento.getOrcamentoId(),
                evento.getStatusAnterior(),
                evento.getNovoStatus()
        );
    }
}
