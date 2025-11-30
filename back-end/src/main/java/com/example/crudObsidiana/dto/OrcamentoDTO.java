package com.example.crudObsidiana.dto;
import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Dados para criação de um novo orçamento")
public class OrcamentoDTO {

    @Schema(description = "Descrição ou título do orçamento", example = "Gravação de vídeo institucional")
    private String descricao;

    @Schema(description = "Data e hora do evento", example = "2025-11-10")
    private LocalDate dataEvento;

    @Schema(description = "Duração do evento em horas", example = "8")
    private Integer duracaoEvento;

    @Schema(description = "Local onde o evento ocorrerá", example = "Estúdio Central")
    private String localEvento;

    @Schema(description = "Status atual do orçamento", example = "ATIVO")
    private String status;

    @Schema(description = "Valor total calculado do orçamento", example = "2500.0")
    private Double valorTotal;

    private List<Long> servicos;

    private List<Long> equipamentos;

    // Getters e Setters
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDate getDataEvento() { return dataEvento; }
    public void setDataEvento(LocalDate dataEvento) { this.dataEvento = dataEvento; }

    public Integer getDuracaoEvento() { return duracaoEvento; }
    public void setDuracaoEvento(Integer duracaoEvento) { this.duracaoEvento = duracaoEvento; }

    public String getLocalEvento() { return localEvento; }
    public void setLocalEvento(String localEvento) { this.localEvento = localEvento; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }

    public List<Long> getServicos() { return servicos; }
    public void setServicos(List<Long> servicos) { this.servicos = servicos; }

    public List<Long> getEquipamentos() { return equipamentos; }
    public void setEquipamentos(List<Long> equipamentos) { this.equipamentos = equipamentos; }
}
