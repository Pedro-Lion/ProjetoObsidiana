package com.example.crudObsidiana.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import com.example.crudObsidiana.dto.UsoEquipamentoDTO;


@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Dados para criação de um novo orçamento")
public class OrcamentoDTO {

    private String idCalendar;

    private String descricao;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Data de início do evento", example = "2025-11-10T09:00:00")
    private Date dataInicio;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Data de término do evento", example = "2025-11-10T09:00:00")
    private Date dataTermino;

    @Schema(description = "Local onde o evento ocorrerá", example = "Estúdio Central")
    private String localEvento;
    private String status;
    private Double valorTotal;

    @Schema(description = "Id recebido da API Microsoft ao cadastrar o evento", example = "Anucvbweo214...")
    private String idCalendar;


    private List<Long> servicos;

    private List<Long> equipamentos;

    private List<Long> profissionais;

    private List<UsoEquipamentoDTO> usosEquipamentos;

    // Getters e Setters
    public String getIdCalendar() { return idCalendar; }
    public void setIdCalendar(String idCalendar) { this.idCalendar = idCalendar;}

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Date getDataInicio() { return dataInicio;}
    public void setDataInicio(Date dataInicio) { this.dataInicio = dataInicio; }

    public Date getDataTermino() { return dataTermino; }
    public void setDataTermino(Date dataTermino) { this.dataTermino = dataTermino; }

    public String getLocalEvento() { return localEvento; }
    public void setLocalEvento(String localEvento) { this.localEvento = localEvento; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }

    public String getIdCalendar() {return idCalendar;}
    public void setIdCalendar(String idCalendar) {this.idCalendar = idCalendar;}

    public List<Long> getServicos() { return servicos; }
    public void setServicos(List<Long> servicos) { this.servicos = servicos; }

    public List<Long> getEquipamentos() { return equipamentos; }
    public void setEquipamentos(List<Long> equipamentos) { this.equipamentos = equipamentos; }

    public List<Long> getProfissionais() { return profissionais; }
    public void setProfissionais(List<Long> profissionais) { this.profissionais = profissionais; }

    public List<UsoEquipamentoDTO> getUsosEquipamentos() {
        return usosEquipamentos;
    }
    public void setUsosEquipamentos(List<UsoEquipamentoDTO> usosEquipamentos) {
        this.usosEquipamentos = usosEquipamentos;
    }
}
