package com.example.crudObsidiana.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Schema(description = "Modelo do Profissional ")
@Entity
public class Profissional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID do profissional", example = "1")
    private Long id;

    @Schema(description = "Nome do profissional", example = "Osvaldo")
    private String nome;

    @Schema(description = "Disponibilidade do profissional", example = "Terça e quinta")
    private String disponibilidade;

    @Schema(description = "Contato do profissional", example = "(11)883992883 // osv.filma@email.com")
    private String contato;

    public Profissional() {
    }

    public Profissional(Long id, String nome, String disponibilidade, String contato) {
        this.id = id;
        this.nome = nome;
        this.disponibilidade = disponibilidade;
        this.contato = contato;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDisponibilidade() {
        return disponibilidade;
    }

    public void setDisponibilidade(String disponibilidade) {
        this.disponibilidade = disponibilidade;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }
}
