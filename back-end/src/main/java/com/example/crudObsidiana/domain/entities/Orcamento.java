package com.example.crudObsidiana.domain.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Orcamento {

    private Long id;
    private Date dataInicio;
    private Date dataTermino;
    private String localEvento;
    private String descricao;
    private String status;
    private Double valorTotal;
    private String idCalendar;

    private List<UsoEquipamento> usosEquipamentos = new ArrayList<>();
    private List<Servico>        servicos          = new ArrayList<>();
    private List<Equipamento>    equipamentos      = new ArrayList<>();
    private List<Profissional>   profissionais     = new ArrayList<>();

    // -------------------------------------------------------------------------
    // CONSTRUCTORS
    // -------------------------------------------------------------------------
    public Orcamento() {}

    public Orcamento(Date dataInicio, Date dataTermino, String localEvento,
                     String descricao, String status, Double valorTotal,
                     String idCalendar) {
        this.dataInicio   = dataInicio;
        this.dataTermino  = dataTermino;
        this.localEvento  = localEvento;
        this.descricao    = descricao;
        this.status       = status;
        this.valorTotal   = valorTotal;
        this.idCalendar   = idCalendar;
    }

    // -------------------------------------------------------------------------
    // GETTERS & SETTERS
    // -------------------------------------------------------------------------
    public Long getId()                               { return id; }
    public void setId(Long id)                       { this.id = id; }

    public Date getDataInicio()                      { return dataInicio; }
    public void setDataInicio(Date dataInicio)       { this.dataInicio = dataInicio; }

    public Date getDataTermino()                     { return dataTermino; }
    public void setDataTermino(Date dataTermino)     { this.dataTermino = dataTermino; }

    public String getLocalEvento()                   { return localEvento; }
    public void setLocalEvento(String localEvento)   { this.localEvento = localEvento; }

    public String getDescricao()                     { return descricao; }
    public void setDescricao(String descricao)       { this.descricao = descricao; }

    public String getStatus()                        { return status; }
    public void setStatus(String status)             { this.status = status; }

    public Double getValorTotal()                    { return valorTotal; }
    public void setValorTotal(Double valorTotal)     { this.valorTotal = valorTotal; }

    public String getIdCalendar()                    { return idCalendar; }
    public void setIdCalendar(String idCalendar)     { this.idCalendar = idCalendar; }

    public List<UsoEquipamento> getUsosEquipamentos()                            { return usosEquipamentos; }
    public void setUsosEquipamentos(List<UsoEquipamento> usosEquipamentos)       { this.usosEquipamentos = usosEquipamentos; }

    public List<Servico> getServicos()               { return servicos; }
    public void setServicos(List<Servico> servicos)  { this.servicos = servicos; }

    public List<Equipamento> getEquipamentos()                   { return equipamentos; }
    public void setEquipamentos(List<Equipamento> equipamentos)  { this.equipamentos = equipamentos; }

    public List<Profissional> getProfissionais()                     { return profissionais; }
    public void setProfissionais(List<Profissional> profissionais)   { this.profissionais = profissionais; }
}