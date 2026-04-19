package com.example.crudObsidiana.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "DTO para criação de serviço com equipamentos")
public class ServicoDTO {

    @Schema(description = "Nome do serviço", example = "Fotografia de Casamento")
    private String nome;

    @Schema(description = "Descrição do serviço", example = "Cobertura completa do evento")
    private String descricao;

    @Schema(description = "Horas estimadas", example = "5")
    private Integer horas;  // Integer (não int primitivo) — compatível com domain entity Servico

    @Schema(description = "Valor por hora", example = "200")
    private Double valorPorHora;

    @Schema(description = "Lista de IDs dos equipamentos usados", example = "[1, 2, 3]")
    private List<Long> equipamentos;

    public String  getNome()                          { return nome; }
    public void    setNome(String nome)               { this.nome = nome; }
    public String  getDescricao()                     { return descricao; }
    public void    setDescricao(String descricao)     { this.descricao = descricao; }
    public Integer getHoras()                         { return horas; }
    public void    setHoras(Integer horas)            { this.horas = horas; }
    public Double  getValorPorHora()                  { return valorPorHora; }
    public void    setValorPorHora(Double v)          { this.valorPorHora = v; }
    public List<Long> getEquipamentos()               { return equipamentos; }
    public void    setEquipamentos(List<Long> e)      { this.equipamentos = e; }
}