package com.example.crudObsidiana.infrastructure.jpa.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

/**
 * Entidade JPA — espelho do banco de dados.
 *
 * O @PrePersist/@PreUpdate é mantido aqui para garantia de integridade
 * no nível do banco, mesmo que os use cases também chamem
 * UsoEquipamento.validarDonoUnico() no domínio.
 * Dupla proteção — nenhuma rota de persistência escapa da regra.
 */
@Schema(description = "Representa o uso de um equipamento em um orçamento ou serviço")
@Entity
@Table(name = "uso_equipamento")
public class UsoEquipamentoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usoEquipamento")
    @Schema(description = "Identificador único do registro de uso", example = "1")
    private Long id;

    @Schema(description = "Quantidade de unidades do equipamento utilizadas", example = "3", required = true)
    @Column(name = "quantidade_usada", nullable = false)
    private Integer quantidadeUsada;

    @Schema(description = "Equipamento utilizado", required = true)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "fkEquipamento", referencedColumnName = "idEquipamento")
    private EquipamentoJpaEntity equipamento;

    @Schema(description = "Orçamento ao qual este uso pertence (opcional)")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fkOrcamento", referencedColumnName = "idOrcamento")
    @JsonBackReference
    private OrcamentoJpaEntity orcamento;

    @Schema(description = "Serviço ao qual este uso pertence (opcional)")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fkServico", referencedColumnName = "idServico")
    @JsonBackReference
    private ServicoJpaEntity servico;

    // -------------------------------------------------------------------------
    // REGRA DE INTEGRIDADE — mantida no JPA como segunda linha de defesa
    // -------------------------------------------------------------------------
    @PrePersist
    @PreUpdate
    private void validarDonoUnico() {
        boolean temOrcamento = this.orcamento != null;
        boolean temServico   = this.servico   != null;
        if (temOrcamento && temServico) {
            throw new IllegalStateException(
                    "UsoEquipamento deve referenciar apenas ORÇAMENTO ou SERVIÇO, não ambos."
            );
        }
    }

    // -------------------------------------------------------------------------
    // GETTERS & SETTERS
    // -------------------------------------------------------------------------
    public Long getId()                                    { return id; }
    public void setId(Long id)                            { this.id = id; }

    public Integer getQuantidadeUsada()                    { return quantidadeUsada; }
    public void setQuantidadeUsada(Integer q)              { this.quantidadeUsada = q; }

    public EquipamentoJpaEntity getEquipamento()           { return equipamento; }
    public void setEquipamento(EquipamentoJpaEntity e)     { this.equipamento = e; }

    public OrcamentoJpaEntity getOrcamento()               { return orcamento; }
    public void setOrcamento(OrcamentoJpaEntity o)         { this.orcamento = o; }

    public ServicoJpaEntity getServico()                   { return servico; }
    public void setServico(ServicoJpaEntity s)             { this.servico = s; }
}