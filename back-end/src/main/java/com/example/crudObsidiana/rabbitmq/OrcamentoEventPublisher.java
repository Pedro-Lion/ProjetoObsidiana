package com.example.crudObsidiana.rabbitmq;

import com.example.crudObsidiana.dto.OrcamentoStatusEventDTO;
import com.example.crudObsidiana.model.Orcamento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Publica eventos de mudança de status do Orçamento nas filas do RabbitMQ.
 *
 * Chamado diretamente pelo OrcamentoService nos pontos onde o status
 * muda para "Confirmado" ou "Cancelado" — em todos os fluxos:
 *   - criarOrcamento()    → quando criado já como Confirmado
 *   - editarOrcamento()   → quando status transiciona para/de Confirmado
 *   - atualizarStatus()   → endpoint dedicado de troca de status
 */
@Component
public class OrcamentoEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrcamentoEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public OrcamentoEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // -------------------------------------------------------------------------
    // PUBLICAR: Orçamento Confirmado
    // -------------------------------------------------------------------------

    /**
     * Publica mensagem na fila {@code fila.orcamento.confirmado}.
     *
     * @param orcamento     Orçamento que teve o status confirmado
     * @param statusAnterior Status que o orçamento tinha antes da confirmação
     */
    public void publicarConfirmado(Orcamento orcamento, String statusAnterior) {
        OrcamentoStatusEventDTO evento = construirEvento(orcamento, statusAnterior, "Confirmado");

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.RK_CONFIRMADO,
                evento
        );

        log.info("[RabbitMQ] ✅ Publicado em '{}' → Orçamento ID={} | '{}' → 'Confirmado'",
                RabbitMQConfig.FILA_CONFIRMADO, orcamento.getId(), statusAnterior);
    }

    // -------------------------------------------------------------------------
    // PUBLICAR: Orçamento Cancelado
    // -------------------------------------------------------------------------

    /**
     * Publica mensagem na fila {@code fila.orcamento.cancelado}.
     *
     * @param orcamento     Orçamento que foi cancelado
     * @param statusAnterior Status que o orçamento tinha antes do cancelamento
     */
    public void publicarCancelado(Orcamento orcamento, String statusAnterior) {
        OrcamentoStatusEventDTO evento = construirEvento(orcamento, statusAnterior, "Cancelado");

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.RK_CANCELADO,
                evento
        );

        log.info("[RabbitMQ] ❌ Publicado em '{}' → Orçamento ID={} | '{}' → 'Cancelado'",
                RabbitMQConfig.FILA_CANCELADO, orcamento.getId(), statusAnterior);
    }

    // -------------------------------------------------------------------------
    // CONSTRUIR O PAYLOAD (DTO do evento)
    // -------------------------------------------------------------------------

    /**
     * Monta o OrcamentoStatusEventDTO a partir do Orcamento e dos status.
     * Centralizado aqui para evitar duplicação no publicarConfirmado/publicarCancelado.
     */
    private OrcamentoStatusEventDTO construirEvento(Orcamento orcamento,
                                                    String statusAnterior,
                                                    String novoStatus) {
        return new OrcamentoStatusEventDTO(
                orcamento.getId(),
                statusAnterior,
                novoStatus,
                orcamento.getLocalEvento(),
                orcamento.getDataInicio(),
                orcamento.getDataTermino(),
                orcamento.getValorTotal()
        );
    }

} //FIM CLASSE
