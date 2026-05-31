package com.example.crudObsidiana.events;

import java.time.LocalDateTime;

public class OrcamentoConfirmadoEvent {

    private String eventId;
    private Long orcamentoId;
    private LocalDateTime ocorridoEm;

    public OrcamentoConfirmadoEvent(Long orcamentoId) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.orcamentoId = orcamentoId;
        this.ocorridoEm = LocalDateTime.now();
    }

    // getters


    public String getEventId() {
        return eventId;
    }

    public Long getOrcamentoId() {
        return orcamentoId;
    }

    public LocalDateTime getOcorridoEm() {
        return ocorridoEm;
    }
}