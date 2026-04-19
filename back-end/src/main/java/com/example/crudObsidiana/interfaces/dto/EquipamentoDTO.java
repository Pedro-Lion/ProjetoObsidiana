package com.example.crudObsidiana.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para criação de equipamento multimídia")
public class EquipamentoDTO {

    @Schema(description = "Nome do equipamento", example = "Câmera Canon")
    private String nome;

    @Schema(description = "Quantidade disponível", example = "5")
    private Integer quantidadeTotal;

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

    public String  getNome()                          { return nome; }
    public void    setNome(String nome)               { this.nome = nome; }
    public Integer getQuantidadeTotal()               { return quantidadeTotal; }
    public void    setQuantidadeTotal(Integer q)      { this.quantidadeTotal = q; }
    public String  getCategoria()                     { return categoria; }
    public void    setCategoria(String categoria)     { this.categoria = categoria; }
    public String  getMarca()                         { return marca; }
    public void    setMarca(String marca)             { this.marca = marca; }
    public String  getNumeroSerie()                   { return numeroSerie; }
    public void    setNumeroSerie(String n)           { this.numeroSerie = n; }
    public String  getModelo()                        { return modelo; }
    public void    setModelo(String modelo)           { this.modelo = modelo; }
    public Double  getValorPorHora()                  { return valorPorHora; }
    public void    setValorPorHora(Double v)          { this.valorPorHora = v; }
}