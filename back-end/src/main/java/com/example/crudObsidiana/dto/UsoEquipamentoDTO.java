package com.example.crudObsidiana.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para representar o uso de um equipamento em um orçamento ou serviço")
public class UsoEquipamentoDTO {

    @Schema(description = "ID do equipamento utilizado", example = "1")
    private Long equipamentoId;

    @Schema(description = "Quantidade de unidades utilizadas", example = "3")
    private int quantidadeUsada;

    // Getters e Setters
    public Long getEquipamentoId() { return equipamentoId; }
    public void setEquipamentoId(Long equipamentoId) { this.equipamentoId = equipamentoId; }

    public int getQuantidadeUsada() { return quantidadeUsada; }
    public void setQuantidadeUsada(int quantidadeUsada) { this.quantidadeUsada = quantidadeUsada; }
}
