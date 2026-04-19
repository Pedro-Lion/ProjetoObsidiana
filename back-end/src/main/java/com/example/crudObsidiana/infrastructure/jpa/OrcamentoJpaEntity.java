package com.example.crudObsidiana.infrastructure.jpa;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entidade JPA — espelho do banco de dados.
 * Todas as anotações de persistência e relacionamentos ficam aqui.
 * O domínio NUNCA importa esta classe.
 */
@Schema(description = "Modelo de orçamento")
@Entity
@Table(name = "orcamentos")
public class OrcamentoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idOrcamento")
    private Long id;

    private Date dataInicio;

    private Date dataTermino;

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

    @Schema(description = "Valor recebido da API Outlook", example = "AybcaefSYc75....")
    @Column(name = "id_calendario")
    private String idCalendar;

    @OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<UsoEquipamentoJpaEntity> usosEquipamentos = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "orcamento_servicos",
            joinColumns = @JoinColumn(name = "orcamento_id"),
            inverseJoinColumns = @JoinColumn(name = "servico_id")
    )
    private List<ServicoJpaEntity> servicos = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "orcamento_equipamentos",
            joinColumns = @JoinColumn(name = "orcamento_id"),
            inverseJoinColumns = @JoinColumn(name = "equipamento_id")
    )
    private List<EquipamentoJpaEntity> equipamentos = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "orcamento_profissionais",
            joinColumns = @JoinColumn(name = "orcamento_id"),
            inverseJoinColumns = @JoinColumn(name = "profissional_id")
    )
    private List<ProfissionalJpaEntity> profissionais = new ArrayList<>();

    // -------------------------------------------------------------------------
    // CONSTRUCTORS
    // -------------------------------------------------------------------------
    public OrcamentoJpaEntity() {}

    public OrcamentoJpaEntity(Date dataInicio, Date dataTermino, String localEvento,
                              String descricao, String status, Double valorTotal,
                              String idCalendar) {
        this.dataInicio  = dataInicio;
        this.dataTermino = dataTermino;
        this.localEvento = localEvento;
        this.descricao   = descricao;
        this.status      = status;
        this.valorTotal  = valorTotal;
        this.idCalendar  = idCalendar;
    }

    // -------------------------------------------------------------------------
    // GETTERS & SETTERS — sem lógica de negócio, apenas acesso direto
    // -------------------------------------------------------------------------
    public Long getId()                              { return id; }
    public void setId(Long id)                      { this.id = id; }

    public Date getDataInicio()                      { return dataInicio; }
    public void setDataInicio(Date dataInicio)       { this.dataInicio = dataInicio; }

    public Date getDataTermino()                     { return dataTermino; }
    public void setDataTermino(Date dataTermino)     { this.dataTermino = dataTermino; }

    public String getLocalEvento()                   { return localEvento; }
    public void setLocalEvento(String localEvento)   { this.localEvento = localEvento; }

    public String getDescricao()                     { return descricao; }
    public void setDescricao(String descricao)       { this.descricao = descricao; }

    public String getStatus()                        { return status; }
    public void setStatus(String status)             { this.status = status; }

    public Double getValorTotal()                    { return valorTotal; }
    public void setValorTotal(Double valorTotal)     { this.valorTotal = valorTotal; }

    public String getIdCalendar()                    { return idCalendar; }
    public void setIdCalendar(String idCalendar)     { this.idCalendar = idCalendar; }

    public List<UsoEquipamentoJpaEntity> getUsosEquipamentos()                               { return usosEquipamentos; }
    public void setUsosEquipamentos(List<UsoEquipamentoJpaEntity> usosEquipamentos)          { this.usosEquipamentos = usosEquipamentos; }

    public List<ServicoJpaEntity> getServicos()                      { return servicos; }
    public void setServicos(List<ServicoJpaEntity> servicos)         { this.servicos = servicos; }

    public List<EquipamentoJpaEntity> getEquipamentos()                      { return equipamentos; }
    public void setEquipamentos(List<EquipamentoJpaEntity> equipamentos)     { this.equipamentos = equipamentos; }

    public List<ProfissionalJpaEntity> getProfissionais()                        { return profissionais; }
    public void setProfissionais(List<ProfissionalJpaEntity> profissionais)      { this.profissionais = profissionais; }
}