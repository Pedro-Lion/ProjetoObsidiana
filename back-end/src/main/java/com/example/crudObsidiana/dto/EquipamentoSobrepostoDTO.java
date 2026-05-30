package com.example.crudObsidiana.dto;

import java.math.BigDecimal;

public class EquipamentoSobrepostoDTO {
    private Long idEquipamento;
    private Integer quantidadeTotal;
    private BigDecimal quantidadeUsada;
    private String[] orcamentos;

    public EquipamentoSobrepostoDTO(Long idEquipamento, Integer quantidadeTotal, BigDecimal quantidadeUsada, String orcamentos) {
        this.idEquipamento = idEquipamento;
        this.quantidadeTotal = quantidadeTotal;
        this.quantidadeUsada = quantidadeUsada;
        this.orcamentos = orcamentos.split(",");
    }

    public Long getIdEquipamento() { return idEquipamento; }
    public void setIdEquipamento(Long idEquipamento) { this.idEquipamento = idEquipamento; }

    public Integer getQuantidadeTotal() { return quantidadeTotal; }
    public void setQuantidadeTotal(Integer quantidadeTotal) { this.quantidadeTotal = quantidadeTotal; }

    public BigDecimal getQuantidadeUsada() { return quantidadeUsada; }
    public void setQuantidadeUsada(BigDecimal quantidadeUsada) { this.quantidadeUsada = quantidadeUsada; }

    public String getOrcamentos() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < orcamentos.length; i++) {
            builder.append(orcamentos[i]);

            if (i < orcamentos.length - 1) {
                builder.append(", ");
            }
        }

        return builder.toString();
    }
    public void setOrcamentos(String[] orcamentos) { this.orcamentos = orcamentos; }
}
