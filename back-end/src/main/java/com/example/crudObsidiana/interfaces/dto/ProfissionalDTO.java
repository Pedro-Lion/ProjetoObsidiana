package com.example.crudObsidiana.interfaces.dto;

public class ProfissionalDTO {

    private String nome;
    private String disponibilidade;
    private String contato;

    public ProfissionalDTO() {}

    public ProfissionalDTO(String nome, String disponibilidade, String contato) {
        this.nome            = nome;
        this.disponibilidade = disponibilidade;
        this.contato         = contato;
    }

    public String getNome()                              { return nome; }
    public void   setNome(String nome)                  { this.nome = nome; }
    public String getDisponibilidade()                   { return disponibilidade; }
    public void   setDisponibilidade(String d)           { this.disponibilidade = d; }
    public String getContato()                           { return contato; }
    public void   setContato(String contato)             { this.contato = contato; }
}