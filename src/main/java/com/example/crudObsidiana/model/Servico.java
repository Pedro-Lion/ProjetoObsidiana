package com.example.crudObsidiana.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Schema(description = "Modelo de Serviço")
@Entity
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID do serviço", example = "1")
    private Long id;

    @Schema(description = "Nome do serviço", example = "Instalação de impressora")
    private String nome;

    @Schema(description = "Descrição detalhada do serviço", example = "Configuração e instalação de impressora multifuncional")
    private String descricao;

    @Schema(description = "Quantidade de horas estimadas", example = "3")
    private int horas;

    @Schema(description = "Valor total do serviço em reais", example = "250")
    private int valor;

    public Servico() {}

    public Servico(Long id, String nome, int horas, int valor) {
        this.id = id;
        this.nome = nome;
        this.horas = horas;
        this.valor = valor;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public int getHoras() { return horas; }
    public void setHoras(int horas) { this.horas = horas; }

    public int getValor() { return valor; }
    public void setValor(int valor) { this.valor = valor; }
}