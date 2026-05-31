package com.example.crudObsidiana.observer;

import com.example.crudObsidiana.messaging.OrcamentoEventPublisher;
import com.example.crudObsidiana.model.Orcamento;
import org.springframework.stereotype.Component;

/**
 * Observer responsável por publicar eventos de mudança de status de orçamento
 * no RabbitMQ. Segue o mesmo padrão do EquipamentoObserver — o OrcamentoService
 * não precisa conhecer o RabbitMQ diretamente, apenas notifica seus observers.
 */
@Component
public class RabbitMQOrcamentoObserver implements OrcamentoObserver {

    private final OrcamentoEventPublisher eventPublisher;

    // Injeção via construtor — boa prática de clean arch
    public RabbitMQOrcamentoObserver(OrcamentoEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void onOrcamentoUpdated(Orcamento orcamento,
                                   String statusAnterior,
                                   String novoStatus) {
        // Delega a publicação para o publisher, que cuida do transporte via RabbitMQ
        eventPublisher.publicarMudancaStatus(orcamento, statusAnterior, novoStatus);
    }

} // FIM CLASSE
