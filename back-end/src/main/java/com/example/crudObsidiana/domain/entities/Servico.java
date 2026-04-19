package com.example.crudObsidiana.domain.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade de domínio — zero dependências de framework.
 * Nota: horas usa Integer (em vez de int primitivo) para permitir
 * comparações null-safe nos use cases.
 */
public class Servico {

    private Long id;
    private String nome;
    private String descricao;
    private Integer horas;        // Integer (não int) para evitar NPE nos use cases
    private Double valorPorHora;

    // Relacionamento como lista de domain entities — sem @ManyToMany
    private List<Equipamento> equipamentos = new ArrayList<>();

    // -------------------------------------------------------------------------
    // CONSTRUCTORS
    // -------------------------------------------------------------------------
    public Servico() {}

    public Servico(Long id, String nome, Integer horas, Double valorPorHora) {
        this.id          = id;
        this.nome        = nome;
        this.horas       = horas;
        this.valorPorHora = valorPorHora;
    }

    // -------------------------------------------------------------------------
    // GETTERS & SETTERS
    // -------------------------------------------------------------------------
    public Long getId()                              { return id; }
    public void setId(Long id)                      { this.id = id; }

    public String getNome()                          { return nome; }
    public void setNome(String nome)                 { this.nome = nome; }

    public String getDescricao()                     { return descricao; }
    public void setDescricao(String descricao)       { this.descricao = descricao; }

    public Integer getHoras()                        { return horas; }
    public void setHoras(Integer horas)              { this.horas = horas; }

    public Double getValorPorHora()                  { return valorPorHora; }
    public void setValorPorHora(Double valorPorHora) { this.valorPorHora = valorPorHora; }

    public List<Equipamento> getEquipamentos()                   { return equipamentos; }
    public void setEquipamentos(List<Equipamento> equipamentos)  { this.equipamentos = equipamentos; }
}