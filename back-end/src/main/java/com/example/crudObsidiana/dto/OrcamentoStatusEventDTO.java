package com.example.crudObsidiana.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Payload da mensagem publicada no RabbitMQ quando o status de um Orçamento muda.
 *
 * Implementa Serializable para que o Jackson consiga converter o objeto
 * para JSON ao publicar/consumir nas filas do RabbitMQ.
 *
 * Filas que utilizam este DTO:
 *  - fila.orcamento.confirmado
 *  - fila.orcamento.cancelado
 */
public class OrcamentoStatusEventDTO implements Serializable {

    private Long idOrcamento;
    private String statusAnterior;
    private String novoStatus;
    private String localEvento;
    private Date dataInicio;
    private Date dataTermino;
    private Double valorTotal;

    // -------------------------------------------------------------------------
    // CONSTRUTORES
    // -------------------------------------------------------------------------

    // Necessário para desserialização pelo Jackson
    public OrcamentoStatusEventDTO() {}

    public OrcamentoStatusEventDTO(Long idOrcamento,
                                   String statusAnterior,
                                   String novoStatus,
                                   String localEvento,
                                   Date dataInicio,
                                   Date dataTermino,
                                   Double valorTotal) {
        this.idOrcamento   = idOrcamento;
        this.statusAnterior = statusAnterior;
        this.novoStatus    = novoStatus;
        this.localEvento   = localEvento;
        this.dataInicio    = dataInicio;
        this.dataTermino   = dataTermino;
        this.valorTotal    = valorTotal;
    }

    // -------------------------------------------------------------------------
    // GETTERS E SETTERS
    // -------------------------------------------------------------------------

    public Long getIdOrcamento() { return idOrcamento; }
    public void setIdOrcamento(Long idOrcamento) { this.idOrcamento = idOrcamento; }

    public String getStatusAnterior() { return statusAnterior; }
    public void setStatusAnterior(String statusAnterior) { this.statusAnterior = statusAnterior; }

    public String getNovoStatus() { return novoStatus; }
    public void setNovoStatus(String novoStatus) { this.novoStatus = novoStatus; }

    public String getLocalEvento() { return localEvento; }
    public void setLocalEvento(String localEvento) { this.localEvento = localEvento; }

    public Date getDataInicio() { return dataInicio; }
    public void setDataInicio(Date dataInicio) { this.dataInicio = dataInicio; }

    public Date getDataTermino() { return dataTermino; }
    public void setDataTermino(Date dataTermino) { this.dataTermino = dataTermino; }

    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }

    @Override
    public String toString() {
        return "OrcamentoStatusEventDTO{" +
                "idOrcamento=" + idOrcamento +
                ", statusAnterior='" + statusAnterior + '\'' +
                ", novoStatus='" + novoStatus + '\'' +
                ", localEvento='" + localEvento + '\'' +
                ", valorTotal=" + valorTotal +
                '}';
    }

} //FIM CLASSE
