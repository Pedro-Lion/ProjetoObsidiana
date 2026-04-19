package com.example.crudObsidiana.infrastructure.jpa;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade JPA — espelho do banco de dados.
 */
@Schema(description = "Modelo do Profissional")
@Entity
@Table(name = "profissional")
public class ProfissionalJpaEntity {

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

    // mappedBy: o lado dono é OrcamentoJpaEntity.profissionais
    @ManyToMany(mappedBy = "profissionais")
    private List<OrcamentoJpaEntity> orcamentos = new ArrayList<>();

    // -------------------------------------------------------------------------
    // CONSTRUCTORS
    // -------------------------------------------------------------------------
    public ProfissionalJpaEntity() {}

    public ProfissionalJpaEntity(String nome, String disponibilidade, String contato) {
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

    public List<OrcamentoJpaEntity> getOrcamentos()              { return orcamentos; }
    public void setOrcamentos(List<OrcamentoJpaEntity> o)        { this.orcamentos = o; }
}