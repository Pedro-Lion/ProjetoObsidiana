package com.example.crudObsidiana.domain.entities;

/**
 * Entidade de domínio — zero dependências de framework.
 * Profissional não expõe lista de Orcamento para evitar
 * referência cíclica; o vínculo é navegado pelo Orcamento.
 */
public class Profissional {

    private Long id;
    private String nome;
    private String disponibilidade;
    private String contato;

    // -------------------------------------------------------------------------
    // CONSTRUCTORS
    // -------------------------------------------------------------------------
    public Profissional() {}

    public Profissional(String nome, String disponibilidade, String contato) {
        this.nome            = nome;
        this.disponibilidade = disponibilidade;
        this.contato         = contato;
    }

    // -------------------------------------------------------------------------
    // GETTERS & SETTERS
    // -------------------------------------------------------------------------
    public Long getId()                              { return id; }
    public void setId(Long id)                      { this.id = id; }

    public String getNome()                          { return nome; }
    public void setNome(String nome)                 { this.nome = nome; }

    public String getDisponibilidade()               { return disponibilidade; }
    public void setDisponibilidade(String d)         { this.disponibilidade = d; }

    public String getContato()                       { return contato; }
    public void setContato(String contato)           { this.contato = contato; }
}