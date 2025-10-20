package com.example.crudObsidiana.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.List;

import java.util.Date;

@Schema(description = "Modelo de Orcamento")
@Entity
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID do orçamento", example = "1")
    private Long id;

    @Schema(description = "Dia do serviço", example = "Câmera Canon")
    private Date dataEvento;

    @Schema(description = "Definição da duração do evento/ serviços dentro do orçamento", example = "4 horas")
    private Integer duracaoEvento;

    @Schema(description = "Campo para anotação de localidade do evento", example = "Parque Iberapuera Portão B")
    private String localEvento;

    @Schema(description = "Descrição do evento ou do orçamento", example = "evento a céu aberto, considerar capa para equipe e equipamentos")
    private String descricao;

    @Schema(description = "Status atual do orçamento", example = "Pendente aprovação do cliente")
    private String status;

    @Schema(description = "Valor Final do orçamento", example = "R$2.000,00")
    private Long valorTotal;

    private List<Equipamento> equipamentos;


    public Orcamento() {}

    public Orcamento(Long id, Date dataEvento, Integer duracaoEvento, String localEvento, String descricao, String status, Long valorTotal) {
        this.id = id;
        this.dataEvento = dataEvento;
        this.duracaoEvento = duracaoEvento;
        this.localEvento = localEvento;
        this.descricao = descricao;
        this.status = status;
        this.valorTotal = valorTotal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(Date dataEvento) {
        this.dataEvento = dataEvento;
    }

    public Integer getDuracaoEvento() {
        return duracaoEvento;
    }

    public void setDuracaoEvento(Integer duracaoEvento) {
        this.duracaoEvento = duracaoEvento;
    }

    public String getLocalEvento() {
        return localEvento;
    }

    public void setLocalEvento(String localEvento) {
        this.localEvento = localEvento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Long valorTotal) {
        this.valorTotal = valorTotal;
    }
}