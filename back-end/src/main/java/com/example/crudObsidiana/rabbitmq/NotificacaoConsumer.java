package com.example.crudObsidiana.rabbitmq;

import com.example.crudObsidiana.dto.OrcamentoStatusEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consome eventos das filas do RabbitMQ e simula o envio de notificações.
 *
 * @RabbitListener escuta continuamente a fila e processa cada mensagem recebida.
 * O Spring AMQP faz o ACK automático após execução bem-sucedida do método.
 *
 * Em uma implementação real, aqui poderia ser feito:
 *  - Envio de e-mail ao cliente (via JavaMailSender / SendGrid)
 *  - Push notification (Firebase / OneSignal)
 *  - Integração com WhatsApp ou SMS
 *  - Registro de evento em sistema externo de CRM
 *  - Atualização de outro microsserviço via HTTP/event
 */
@Component
public class NotificacaoConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoConsumer.class);

    // -------------------------------------------------------------------------
    // CONSUMER: Fila de Orçamentos Confirmados
    // -------------------------------------------------------------------------

    /**
     * Processa mensagens da fila {@code fila.orcamento.confirmado}.
     * Disparado toda vez que um Orçamento tem seu status mudado para "Confirmado".
     *
     * @param evento Payload desserializado automaticamente pelo Jackson
     */
    @RabbitListener(queues = RabbitMQConfig.FILA_CONFIRMADO)
    public void processarConfirmado(OrcamentoStatusEventDTO evento) {
        log.info("=============================================================");
        log.info("[NOTIFICAÇÃO] ✅ Orçamento CONFIRMADO recebido da fila!");
        log.info("  → ID do Orçamento  : {}", evento.getIdOrcamento());
        log.info("  → Local do Evento   : {}", evento.getLocalEvento());
        log.info("  → Valor Total       : R$ {}", evento.getValorTotal());
        log.info("  → Status anterior   : {}", evento.getStatusAnterior());
        log.info("  → Novo status       : {}", evento.getNovoStatus());
        log.info("  → Data de início    : {}", evento.getDataInicio());
        log.info("[SIMULAÇÃO] Enviando e-mail de confirmação ao cliente...");
        log.info("[SIMULAÇÃO] ✅ E-mail enviado com sucesso para o orçamento #{}!", evento.getIdOrcamento());
        log.info("=============================================================");
    }

    // -------------------------------------------------------------------------
    // CONSUMER: Fila de Orçamentos Cancelados
    // -------------------------------------------------------------------------

    /**
     * Processa mensagens da fila {@code fila.orcamento.cancelado}.
     * Disparado toda vez que um Orçamento tem seu status mudado para "Cancelado".
     *
     * @param evento Payload desserializado automaticamente pelo Jackson
     */
    @RabbitListener(queues = RabbitMQConfig.FILA_CANCELADO)
    public void processarCancelado(OrcamentoStatusEventDTO evento) {
        log.info("=============================================================");
        log.info("[NOTIFICAÇÃO] ❌ Orçamento CANCELADO recebido da fila!");
        log.info("  → ID do Orçamento  : {}", evento.getIdOrcamento());
        log.info("  → Local do Evento   : {}", evento.getLocalEvento());
        log.info("  → Valor Total       : R$ {}", evento.getValorTotal());
        log.info("  → Status anterior   : {}", evento.getStatusAnterior());
        log.info("  → Novo status       : {}", evento.getNovoStatus());
        log.info("  → Data de início    : {}", evento.getDataInicio());
        log.info("[SIMULAÇÃO] Enviando e-mail de cancelamento ao cliente...");
        log.info("[SIMULAÇÃO] ❌ Notificação de cancelamento enviada para o orçamento #{}!", evento.getIdOrcamento());
        log.info("=============================================================");
    }

} //FIM CLASSE
