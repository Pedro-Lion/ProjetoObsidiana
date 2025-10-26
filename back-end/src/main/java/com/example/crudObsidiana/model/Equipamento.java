package com.example.crudObsidiana.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


@Schema(description = "Modelo de Equipamento")
@Entity
public class Equipamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idEquipamento")
    @Schema(description = "ID do equipamento", example = "1")
    private Long id;

    @Schema(description = "Nome do equipamento", example = "Câmera Canon")
    private String nome;

    @Schema(description = "Quantidade total", example = "5")
    private int quantidade;

    @Schema(description = "Quantidade disponível", example = "3")
    private int quantidadeDisponivel;

    @Schema(description = "Categoria do Equipamento", example = "Câmeras")
    private String categoria;

    @Schema(description = "Marca do Equipamento", example = "Sony")
    private String marca;

    @Schema(description = "Número de Série do Equipamento", example = "N00123")
    private String numeroSerie;

    @Schema(description = "Modelo do Equipamento", example = "C9-20mm DisplayHD")
    private String modelo;

    @Schema(description = "Valor unitário em reais", example = "1200")
    private int valorPorHora;

    @ManyToMany(mappedBy = "equipamentos")
    private List<Servico> servicos = new ArrayList<>();


    //CONSTRUCTORS
    public Equipamento() {}
    public Equipamento(Long id, String nome, int quantidade, String categoria, String marca, String numeroSerie, String modelo, int valorPorHora) {
        this.id = id;
        this.nome = nome;
        this.quantidade = quantidade;
        this.categoria = categoria;
        this.marca = marca;
        this.numeroSerie = numeroSerie;
        this.modelo = modelo;
        this.valorPorHora = valorPorHora;
    }

    //GETTERS & SETTERS
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public int getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
        // Se a quantidade total for alterada, ajustar o disponível
        if (quantidadeDisponivel > quantidade) {
            quantidadeDisponivel = quantidade;
        }
    }
    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    public String getMarca() {
        return marca;
    }
    public void setMarca(String marca) {
        this.marca = marca;
    }
    public String getNumeroSerie() {
        return numeroSerie;
    }
    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }
    public String getModelo() {
        return modelo;
    }
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }
    public int getValorPorHora() {
        return valorPorHora;
    }
    public void setValorPorHora(int valorPorHora) {
        this.valorPorHora = valorPorHora;
    }
    public int getQuantidadeDisponivel() { return quantidadeDisponivel; }
    public void setQuantidadeDisponivel(int quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
        if (quantidadeDisponivel < 0) quantidadeDisponivel = 0;
        if (quantidadeDisponivel > quantidade) quantidadeDisponivel = quantidade;
    }

    //METODOS
    public void reduzirQuantidade(int quantidadeUsada) {
        if (quantidadeUsada < 0) return;
        if (quantidadeDisponivel >= quantidadeUsada) {
            quantidadeDisponivel -= quantidadeUsada;
        } else {
            quantidadeDisponivel = 0;
        }
    }

    public void devolverQuantidade(int quantidadeDevolvida) {
        if (quantidadeDevolvida < 0) return;
        quantidadeDisponivel += quantidadeDevolvida;
        if (quantidadeDisponivel > quantidade) {
            quantidadeDisponivel = quantidade;
        }
    }
}