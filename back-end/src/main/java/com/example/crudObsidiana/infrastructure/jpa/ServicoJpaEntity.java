package com.example.crudObsidiana.infrastructure.jpa;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade JPA — espelho do banco de dados.
 * Nota: horas é mantido como int (primitivo) para garantir
 * compatibilidade com o schema DDL original (NOT NULL implícito).
 */
@Schema(description = "Modelo de Serviço")
@Entity
@Table(name = "servico")
public class ServicoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idServico")
    @Schema(description = "ID do serviço", example = "1")
    private Long id;

    @Schema(description = "Nome do serviço", example = "Instalação de impressora")
    private String nome;

    @Schema(description = "Descrição detalhada do serviço", example = "Configuração e instalação de impressora multifuncional")
    private String descricao;

    @Schema(description = "Quantidade de horas estimadas", example = "3")
    private int horas;

    @Schema(description = "Valor total do serviço em reais", example = "250")
    private Double valorPorHora;

    @ManyToMany
    @JoinTable(
            name = "servico_equipamento",
            joinColumns = @JoinColumn(name = "servico_id"),
            inverseJoinColumns = @JoinColumn(name = "equipamento_id")
    )
    private List<EquipamentoJpaEntity> equipamentos = new ArrayList<>();

    // -------------------------------------------------------------------------
    // CONSTRUCTORS
    // -------------------------------------------------------------------------
    public ServicoJpaEntity() {}

    public ServicoJpaEntity(Long id, String nome, int horas, Double valorPorHora) {
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

    public int getHoras()                            { return horas; }
    public void setHoras(int horas)                  { this.horas = horas; }

    public Double getValorPorHora()                  { return valorPorHora; }
    public void setValorPorHora(Double valorPorHora) { this.valorPorHora = valorPorHora; }

    public List<EquipamentoJpaEntity> getEquipamentos()                      { return equipamentos; }
    public void setEquipamentos(List<EquipamentoJpaEntity> equipamentos)     { this.equipamentos = equipamentos; }
}