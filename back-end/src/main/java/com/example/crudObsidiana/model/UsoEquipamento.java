package com.example.crudObsidiana.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Schema(description = "Representa o uso de um equipamento em um orçamento ou serviço")
@Entity
@Table(name = "uso_equipamento")
public class UsoEquipamento {

    @Schema(description = "Identificador único do registro de uso", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usoEquipamento")
    private Long id;

    @Schema(description = "Quantidade de unidades do equipamento utilizadas", example = "3")
    @Column(name = "quantidade_usada", nullable = false)
    private int quantidadeUsada;

    @Schema(description = "Equipamento utilizado")
    @ManyToOne(optional = false)
    @JoinColumn(name = "fkEquipamento", referencedColumnName = "idEquipamento")
    private Equipamento equipamento;

    @Schema(description = "Orçamento ao qual este uso pertence (opcional)")
    @ManyToOne
    @JoinColumn(name = "fkOrcamento", referencedColumnName = "idOrcamento")
    @JsonBackReference
    private Orcamento orcamento;

    @Schema(description = "Serviço ao qual este uso pertence (opcional)")
    @ManyToOne
    @JoinColumn(name = "fkServico", referencedColumnName = "idServico")
    private Servico servico;

    // --- Getters e Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getQuantidadeUsada() { return quantidadeUsada; }
    public void setQuantidadeUsada(int quantidadeUsada) { this.quantidadeUsada = quantidadeUsada; }

    public Equipamento getEquipamento() { return equipamento; }
    public void setEquipamento(Equipamento equipamento) { this.equipamento = equipamento; }

    public Orcamento getOrcamento() { return orcamento; }
    public void setOrcamento(Orcamento orcamento) { this.orcamento = orcamento; }

    public Servico getServico() { return servico; }
    public void setServico(Servico servico) { this.servico = servico; }

    @PrePersist
    @PreUpdate
    private void validarDonoUnico() {
        boolean temOrcamento = this.orcamento != null;
        boolean temServico = this.servico != null;

        if (temOrcamento == temServico) {
            throw new IllegalStateException("UsoEquipamento deve referenciar apenas ORÇAMENTO ou SERVIÇO (exatamente um).");
        }
    }
}
