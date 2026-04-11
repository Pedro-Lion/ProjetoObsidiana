package com.example.crudObsidiana.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Schema(description = "Representa o uso de um equipamento em um orçamento ou serviço")
@Entity
@Table(name = "uso_equipamento")
public class UsoEquipamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Quantidade de unidades do equipamento utilizadas", example = "3", required = true)
    @Column(name = "quantidade_usada", nullable = false)
    private Integer quantidadeUsada;

    @Schema(description = "Equipamento utilizado", required = true)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_equipamento")
    private Equipamento equipamento;

    @Schema(description = "Orçamento ao qual este uso pertence (opcional)")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_orcamento")
    @JsonBackReference
    private Orcamento orcamento;

    @Schema(description = "Serviço ao qual este uso pertence (opcional)")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_servico")
    @JsonBackReference
    private Servico servico;

    // --- Getters e Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getQuantidadeUsada() { return quantidadeUsada; }
    public void setQuantidadeUsada(Integer quantidadeUsada) { this.quantidadeUsada = quantidadeUsada; }

    public Equipamento getEquipamento() { return equipamento; }
    public void setEquipamento(Equipamento equipamento) { this.equipamento = equipamento; }

    public Orcamento getOrcamento() { return orcamento; }
    public void setOrcamento(Orcamento orcamento) { this.orcamento = orcamento; }

    public Servico getServico() { return servico; }
    public void setServico(Servico servico) { this.servico = servico; }

    // --- Regra de integridade ---
    @PrePersist
    @PreUpdate
    private void validarDonoUnico() {
        boolean temOrcamento = this.orcamento != null;
        boolean temServico = this.servico != null;

        if (temOrcamento && temServico) {
            throw new IllegalStateException("UsoEquipamento deve referenciar apenas ORÇAMENTO ou SERVIÇO, não ambos.");
        }
    }
}
