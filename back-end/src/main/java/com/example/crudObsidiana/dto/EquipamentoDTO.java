package com.example.crudObsidiana.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para criação de equipamento multimídia")
public class EquipamentoDTO {

    @Schema(description = "Nome do equipamento", example = "Câmera Canon")
    private String nome;

    @Schema(description = "Quantidade disponível", example = "5")
    private int quantidade;

    @Schema(description = "Categoria do equipamento", example = "Câmeras")
    private String categoria;

    @Schema(description = "Marca do equipamento", example = "Sony")
    private String marca;

    @Schema(description = "Número de Série do equipamento", example = "N00123")
    private String numeroSerie;

    @Schema(description = "Modelo do equipamento", example = "C9-20mm DisplayHD")
    private String modelo;

    @Schema(description = "Valor unitário por hora", example = "1200")
    private Double valorPorHora;

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

    public double getValorPorHora() {
        return valorPorHora;
    }

    public void setValorPorHora(Double valorPorHora) {
        this.valorPorHora = valorPorHora;
    }
}