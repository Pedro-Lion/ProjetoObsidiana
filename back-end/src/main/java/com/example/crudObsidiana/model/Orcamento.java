package com.example.crudObsidiana.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Modelo de orçamento")
@Entity
@Table(name = "orcamentos")
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idOrcamento")
    private Long id;

    @Schema(description = "Data do evento", example = "2025-11-05")
    @Column(name = "data_evento")
    private LocalDate dataEvento;

    @Schema(description = "Duração do evento em horas", example = "8")
    @Column(name = "duracao")
    private Integer duracaoEvento;

    @Schema(description = "Local do evento")
    @Column(name = "local_evento")
    private String localEvento;

    @Schema(description = "Descrição geral do orçamento")
    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Schema(description = "Status atual do orçamento")
    private String status;

    @Schema(description = "Valor total estimado")
    @Column(name = "valorTotal")
    private Double valorTotal;

    @OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<UsoEquipamento> usosEquipamentos;

    // GETTERS & SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDataEvento() { return dataEvento; }
    public void setDataEvento(LocalDate dataEvento) { this.dataEvento = dataEvento; }

    public Integer getDuracaoEvento() { return duracaoEvento; }
    public void setDuracaoEvento(Integer duracaoEvento) { this.duracaoEvento = duracaoEvento; }

    public String getLocalEvento() { return localEvento; }
    public void setLocalEvento(String localEvento) { this.localEvento = localEvento; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }

    public List<UsoEquipamento> getUsosEquipamentos() { return usosEquipamentos; }
    public void setUsosEquipamentos(List<UsoEquipamento> usosEquipamentos) { this.usosEquipamentos = usosEquipamentos; }
}
