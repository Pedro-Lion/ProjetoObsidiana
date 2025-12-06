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
    private Integer quantidadeTotal;

    @Schema(description = "Quantidade disponível", example = "3")
    private Integer quantidadeDisponivel;

    @Schema(description = "Categoria do Equipamento", example = "Câmeras")
    private String categoria;

    @Schema(description = "Marca do Equipamento", example = "Sony")
    private String marca;

    @Schema(description = "Número de Série do Equipamento", example = "N00123")
    private String numeroSerie;

    @Schema(description = "Modelo do Equipamento", example = "C9-20mm DisplayHD")
    private String modelo;

    @Schema(description = "Valor unitário em reais", example = "1200")
    private Double valorPorHora;

    @ManyToMany(mappedBy = "equipamentos")
    private List<Servico> servicos = new ArrayList<>();

//    Metadados da imagem
    @Schema(description = "Nome do arquivo de imagem salvo", example = "1632938123456_camera.jpg")
    private String nomeArquivoImagem;

    @Schema(description = "Tipo MIME da imagem", example = "image/jpeg")
    private String tipoImagem;

    @Schema(description = "Caminho absoluto ou relativo da imagem no servidor", example = "upload/1632938123456_camera.jpg")
    private String caminhoImagem;


    //CONSTRUCTORS
    public Equipamento() {}
    public Equipamento(Long id, String nome, Integer quantidadeTotal, String categoria, String marca, String numeroSerie, String modelo, Double valorPorHora) {
        this.id = id;
        this.nome = nome;
        this.quantidadeTotal = quantidadeTotal;
        this.categoria = categoria;
        this.marca = marca;
        this.numeroSerie = numeroSerie;
        this.modelo = modelo;
        this.valorPorHora = valorPorHora;
    }
    public Equipamento(Long id, String nome, Integer quantidadeTotal, Integer quantidadeDisponivel, String categoria, String marca, String numeroSerie, String modelo, Double valorPorHora, List<Servico> servicos, String nomeArquivoImagem, String tipoImagem, String caminhoImagem) {
        this.id = id;
        this.nome = nome;
        this.quantidadeTotal = quantidadeTotal;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.categoria = categoria;
        this.marca = marca;
        this.numeroSerie = numeroSerie;
        this.modelo = modelo;
        this.valorPorHora = valorPorHora;
        this.servicos = servicos;
        this.nomeArquivoImagem = nomeArquivoImagem;
        this.tipoImagem = tipoImagem;
        this.caminhoImagem = caminhoImagem;
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
    public Integer getQuantidadeTotal() {
        return quantidadeTotal;
    }
    public void setQuantidadeTotal(Integer quantidadeTotal) {
        this.quantidadeTotal = quantidadeTotal;
        // Se a quantidade total for alterada, ajustar o disponível
        if (quantidadeDisponivel > quantidadeTotal) {
            quantidadeDisponivel = quantidadeTotal;
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
    public Double getValorPorHora() {
        return valorPorHora;
    }
    public void setValorPorHora(Double valorPorHora) {
        this.valorPorHora = valorPorHora;
    }
    public Integer getQuantidadeDisponivel() { return quantidadeDisponivel; }
    public void setQuantidadeDisponivel(Integer quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
        if (quantidadeDisponivel < 0) quantidadeDisponivel = 0;
        if (quantidadeDisponivel > quantidadeTotal) quantidadeDisponivel = quantidadeTotal;
    }
    public String getNomeArquivoImagem() {
        return nomeArquivoImagem;
    }
    public void setNomeArquivoImagem(String nomeArquivoImagem) {
        this.nomeArquivoImagem = nomeArquivoImagem;
    }
    public String getTipoImagem() {
        return tipoImagem;
    }
    public void setTipoImagem(String tipoImagem) {
        this.tipoImagem = tipoImagem;
    }
    public String getCaminhoImagem() {
        return caminhoImagem;
    }
    public void setCaminhoImagem(String caminhoImagem) {
        this.caminhoImagem = caminhoImagem;
    }

    //METODOS
    public void reduzirQuantidade(Integer quantidadeUsada) {
        if (quantidadeUsada < 0) return;
        if (quantidadeDisponivel >= quantidadeUsada) {
            quantidadeDisponivel -= quantidadeUsada;
        } else {
            quantidadeDisponivel = 0;
        }
    }

    public void devolverQuantidade(Integer quantidadeDevolvida) {
        if (quantidadeDevolvida < 0) return;
        quantidadeDisponivel += quantidadeDevolvida;
        if (quantidadeDisponivel > quantidadeTotal) {
            quantidadeDisponivel = quantidadeTotal;
        }
    }
}