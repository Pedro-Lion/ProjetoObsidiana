package com.example.crudObsidiana.domain.entities;

/**
 * Entidade de domínio — zero dependências de framework.
 *
 * A regra de integridade (orçamento XOR serviço) que no model
 * original era @PrePersist/@PreUpdate foi movida para um método
 * de domínio chamado explicitamente pelos use cases, mantendo
 * a lógica de negócio dentro do domínio, sem depender do JPA.
 */
public class UsoEquipamento {

    private Long id;
    private Integer quantidadeUsada;

    // Relacionamentos como domain entities (sem @ManyToOne / @JoinColumn)
    private Equipamento equipamento;
    private Orcamento   orcamento;
    private Servico     servico;

    // -------------------------------------------------------------------------
    // REGRA DE NEGÓCIO (migrada do @PrePersist original)
    // Chame este método nos use cases antes de persistir.
    // -------------------------------------------------------------------------
    public void validarDonoUnico() {
        boolean temOrcamento = this.orcamento != null;
        boolean temServico   = this.servico   != null;
        if (temOrcamento && temServico) {
            throw new IllegalStateException(
                    "UsoEquipamento deve referenciar apenas ORÇAMENTO ou SERVIÇO, não ambos."
            );
        }
    }

    // -------------------------------------------------------------------------
    // CONSTRUCTORS
    // -------------------------------------------------------------------------
    public UsoEquipamento() {}

    // -------------------------------------------------------------------------
    // GETTERS & SETTERS
    // -------------------------------------------------------------------------
    public Long getId()                              { return id; }
    public void setId(Long id)                      { this.id = id; }

    public Integer getQuantidadeUsada()              { return quantidadeUsada; }
    public void setQuantidadeUsada(Integer q)        { this.quantidadeUsada = q; }

    public Equipamento getEquipamento()              { return equipamento; }
    public void setEquipamento(Equipamento e)        { this.equipamento = e; }

    public Orcamento getOrcamento()                  { return orcamento; }
    public void setOrcamento(Orcamento o)            { this.orcamento = o; }

    public Servico getServico()                      { return servico; }
    public void setServico(Servico s)                { this.servico = s; }
}