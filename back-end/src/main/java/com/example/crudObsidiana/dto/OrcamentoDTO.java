package com.example.crudObsidiana.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;


@Schema(description = "Dados para criação de um novo orçamento")
public class OrcamentoDTO {

    private String descricao;
    private LocalDate dataEvento;
    private Integer duracaoEvento;
    private String localEvento;
    private String status;
    private Double valorTotal;

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
}
