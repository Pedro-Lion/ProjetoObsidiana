package com.example.crudObsidiana.dto;

import java.io.Serializable;
import java.util.List;

public class OrcamentoStatusEvent implements Serializable {

    private Long orcamentoId;
    private String statusAnterior;
    private String novoStatus;
    private List<UsoEquipamentoEvent> usosEquipamentos;

    public OrcamentoStatusEvent() {}

    public OrcamentoStatusEvent(Long orcamentoId, String statusAnterior,
                                String novoStatus, List<UsoEquipamentoEvent> usos) {
        this.orcamentoId = orcamentoId;
        this.statusAnterior = statusAnterior;
        this.novoStatus = novoStatus;
        this.usosEquipamentos = usos;
    }

    public static class UsoEquipamentoEvent implements Serializable {
        private Long equipamentoId;
        private Integer quantidadeUsada;

        public UsoEquipamentoEvent() {}
        public UsoEquipamentoEvent(Long equipamentoId, Integer quantidadeUsada) {
            this.equipamentoId = equipamentoId;
            this.quantidadeUsada = quantidadeUsada;
        }
        public Long getEquipamentoId() { return equipamentoId; }
        public void setEquipamentoId(Long id) { this.equipamentoId = id; }
        public Integer getQuantidadeUsada() { return quantidadeUsada; }
        public void setQuantidadeUsada(Integer q) { this.quantidadeUsada = q; }
    }

    public Long getOrcamentoId() { return orcamentoId; }
    public void setOrcamentoId(Long id) { this.orcamentoId = id; }
    public String getStatusAnterior() { return statusAnterior; }
    public void setStatusAnterior(String s) { this.statusAnterior = s; }
    public String getNovoStatus() { return novoStatus; }
    public void setNovoStatus(String s) { this.novoStatus = s; }
    public List<UsoEquipamentoEvent> getUsosEquipamentos() { return usosEquipamentos; }
    public void setUsosEquipamentos(List<UsoEquipamentoEvent> l) { this.usosEquipamentos = l; }
}