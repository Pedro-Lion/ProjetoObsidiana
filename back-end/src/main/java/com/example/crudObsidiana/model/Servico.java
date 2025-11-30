package com.example.crudObsidiana.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import org.hibernate.mapping.Join;

import java.util.List;
import java.util.ArrayList;


@Schema(description = "Modelo de Serviço")
@Entity
public class Servico  {

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
    private int valorPorHora;

    @ManyToMany
    @JoinTable(
            name = "servico_equipamento",
            joinColumns = @JoinColumn(name = "servico_id"),
            inverseJoinColumns = @JoinColumn(name = "equipamento_id")
    )
    private List<Equipamento> equipamentos = new ArrayList<>();

    @ManyToMany(mappedBy = "servicos")
    private List<Orcamento> orcamentos = new ArrayList<>();

    public Servico() {}

    public Servico(Long id, String nome, int horas, int valor) {
        this.id = id;
        this.nome = nome;
        this.horas = horas;
        this.valorPorHora = valor;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public int getHoras() { return horas; }
    public void setHoras(int horas) { this.horas = horas; }

    public int getValorPorHora() { return valorPorHora; }
    public void setValorPorHora(int valorPorHora) { this.valorPorHora = valorPorHora; }

    public List<Equipamento> getEquipamentos() {
        return equipamentos;
    }
    public void setEquipamentos(List<Equipamento> equipamentos) {
        this.equipamentos = equipamentos;
    }
}
