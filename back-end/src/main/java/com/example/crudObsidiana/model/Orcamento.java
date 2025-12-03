package com.example.crudObsidiana.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Schema(description = "Modelo de orçamento")
@Entity
@Table(name = "orcamentos")
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idOrcamento")
    @Schema(description = "ID do orçamento", example = "1")
    private Long id;

    @Schema(description = "Data do evento", example = "2025-11-05")
    @Column(name = "data_evento")
    private Date dataEvento;

    @Schema(description = "Duração do evento em horas", example = "8")
    @Column(name = "duracao")
    private Integer duracaoEvento;

    @Schema(description = "Local do evento", example = "Estúdio Principal")
    @Column(name = "local_evento")
    private String localEvento;

    @Schema(description = "Descrição geral do orçamento", example = "Gravação de videoclipe publicitário")
    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Schema(description = "Status atual do orçamento", example = "Confirmado")
    private String status;

    @Schema(description = "Valor total estimado", example = "3500")
    @Column(name = "valorTotal")
    private Double valorTotal;

    @OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "Lista de usos de equipamentos vinculados a este orçamento")
    @JsonManagedReference
    private List<UsoEquipamento> usosEquipamentos;

    @ManyToMany
    @JoinTable(
        name = "orcamento_servicos",
        joinColumns = @JoinColumn(name = "orcamento_id"),
        inverseJoinColumns = @JoinColumn(name = "servico_id")
    )
    private List<Servico> servicos = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "orcamento_equipamentos",
        joinColumns = @JoinColumn(name = "orcamento_id"),
        inverseJoinColumns = @JoinColumn(name = "equipamento_id")
    )
    private List<Equipamento> equipamentos = new ArrayList<>();

    @ManyToMany
    @JoinTable(
    name = "orcamento_profissionais",
    joinColumns = @JoinColumn(name = "orcamento_id"),
    inverseJoinColumns = @JoinColumn(name = "profissional_id")
    )
    private List<Profissional> profissionais = new ArrayList<>();

    public Orcamento() {
    }

    public Orcamento(
        LocalDate dataEvento,
        Integer duracaoEvento,
        String localEvento,
        String descricao,
        String status,
        Double valorTotal
    ) {
        this.dataEvento = dataEvento;
        this.duracaoEvento = duracaoEvento;
        this.localEvento = localEvento;
        this.descricao = descricao;
        this.status = status;
        this.valorTotal = valorTotal;
    }

    // GETTERS & SETTERS

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Date getDataEvento() { return dataEvento; }
    public void setDataEvento(Date dataEvento) { this.dataEvento = dataEvento; }

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

    public List<Servico> getServicos() { return servicos; }
    public void setServicos(List<Servico> servicos) { this.servicos = servicos;}

    public List<Equipamento> getEquipamentos() { return equipamentos; }
    public void setEquipamentos(List<Equipamento> equipamentos) { this.equipamentos = equipamentos;}

    public List<Profissional> getProfissionais() { return profissionais; }
    public void setProfissionais(List<Profissional> profissionais) { this.profissionais = profissionais; }
}
