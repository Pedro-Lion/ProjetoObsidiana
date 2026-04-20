package com.example.crudObsidiana.infrastructure.jpa.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "Modelo de Equipamento")
@Entity
@Table(name = "equipamento")
public class EquipamentoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idEquipamento")
    private Long id;

    private String nome;
    private Integer quantidadeTotal;
    private Integer quantidadeDisponivel;
    private String categoria;
    private String marca;
    private String numeroSerie;
    private String modelo;
    private Double valorPorHora;

    @ManyToMany(mappedBy = "equipamentos")
    private List<ServicoJpaEntity> servicos = new ArrayList<>();

    @ManyToMany(mappedBy = "equipamentos")
    private List<OrcamentoJpaEntity> orcamentos = new ArrayList<>();

    private String nomeArquivoImagem;
    private String tipoImagem;
    private String caminhoImagem;

    public EquipamentoJpaEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getQuantidadeTotal() { return quantidadeTotal; }
    public void setQuantidadeTotal(Integer quantidadeTotal) { this.quantidadeTotal = quantidadeTotal; }
    public Integer getQuantidadeDisponivel() { return quantidadeDisponivel; }
    public void setQuantidadeDisponivel(Integer quantidadeDisponivel) { this.quantidadeDisponivel = quantidadeDisponivel; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public Double getValorPorHora() { return valorPorHora; }
    public void setValorPorHora(Double valorPorHora) { this.valorPorHora = valorPorHora; }
    public String getNomeArquivoImagem() { return nomeArquivoImagem; }
    public void setNomeArquivoImagem(String v) { this.nomeArquivoImagem = v; }
    public String getTipoImagem() { return tipoImagem; }
    public void setTipoImagem(String v) { this.tipoImagem = v; }
    public String getCaminhoImagem() { return caminhoImagem; }
    public void setCaminhoImagem(String v) { this.caminhoImagem = v; }
    public List<ServicoJpaEntity> getServicos() { return servicos; }
    public void setServicos(List<ServicoJpaEntity> servicos) { this.servicos = servicos; }
}