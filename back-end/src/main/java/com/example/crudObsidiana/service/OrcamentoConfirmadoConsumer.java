package com.example.crudObsidiana.service;

import com.example.crudObsidiana.events.OrcamentoConfirmadoEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrcamentoConfirmadoConsumer {

    @RabbitListener(queues = "orcamento.confirmado.queue")
    public void consumir(OrcamentoConfirmadoEvent event) {

        System.out.println("Orçamento confirmado: " + event.getOrcamentoId());

        // aqui você pode:
        // - integrar com outro microsserviço
        // - gerar ordem de serviço
        // - notificar cliente
    }
}