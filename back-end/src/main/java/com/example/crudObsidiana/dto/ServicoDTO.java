package com.example.crudObsidiana.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO para criação de serviço com equipamentos")
public class ServicoDTO {

    @Schema(description = "Nome do serviço", example = "Fotografia de Casamento")
    private String nome;

    @Schema(description = "Descrição do serviço", example = "Cobertura completa do evento")
    private String descricao;

    @Schema(description = "Horas estimadas", example = "5")
    private int horas;

    @Schema(description = "Valor por hora", example = "200")
    private int valorPorHora;

    @Schema(description = "Lista de IDs dos equipamentos usados", example = "[1, 2, 3]")
    private List<Long> equipamentos;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getHoras() {
        return horas;
    }

    public void setHoras(int horas) {
        this.horas = horas;
    }

    public int getValorPorHora() {
        return valorPorHora;
    }

    public void setValorPorHora(int valorPorHora) {
        this.valorPorHora = valorPorHora;
    }

    public List<Long> getEquipamentos() { return equipamentos; }

    public void setEquipamentosIds(List<Long> equipamentosIds) {
        this.equipamentos = equipamentos;
    }
}
