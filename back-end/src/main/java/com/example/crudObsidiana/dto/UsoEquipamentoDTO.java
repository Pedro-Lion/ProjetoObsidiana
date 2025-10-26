package com.example.crudObsidiana.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO para representar o uso de um equipamento em um orçamento ou serviço")
public class UsoEquipamentoDTO {

    @Schema(description = "ID do equipamento utilizado", example = "1", required = true)
    @NotNull(message = "O ID do equipamento é obrigatório.")
    private Long idEquipamento;

    @Schema(description = "ID do orçamento ao qual o uso pertence (ou null se for serviço)", example = "1")
    private Long idOrcamento;

    @Schema(description = "ID do serviço ao qual o uso pertence (ou null se for orçamento)", example = "null")
    private Long idServico;

    @Schema(description = "Quantidade de unidades do equipamento utilizadas", example = "3", required = true)
    @Min(value = 1, message = "A quantidade usada deve ser no mínimo 1.")
    private int quantidadeUsada;

    // Getters e Setters
    public Long getIdEquipamento() {
        return idEquipamento;
    }
    public void setIdEquipamento(Long idEquipamento) {
        this.idEquipamento = idEquipamento;
    }

    public Long getIdOrcamento() {
        return idOrcamento;
    }
    public void setIdOrcamento(Long idOrcamento) {
        this.idOrcamento = idOrcamento;
    }

    public Long getIdServico() {
        return idServico;
    }
    public void setIdServico(Long idServico) {
        this.idServico = idServico;
    }

    public int getQuantidadeUsada() {
        return quantidadeUsada;
    }
    public void setQuantidadeUsada(int quantidadeUsada) {
        this.quantidadeUsada = quantidadeUsada;
    }
}
