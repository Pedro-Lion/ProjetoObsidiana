package com.example.crudObsidiana.domain.entities;

public class Equipamento {

    private Long id;
    private String nome;
    private Integer quantidadeTotal;
    private Integer quantidadeDisponivel;
    private String categoria;
    private String marca;
    private String numeroSerie;
    private String modelo;
    private Double valorPorHora;
    private String nomeArquivoImagem;
    private String tipoImagem;
    private String caminhoImagem;


    //CONSTRUCTORS
    public Equipamento() {}

    public Equipamento(Long id, String nome, Integer quantidadeTotal,
                       String categoria, String marca, String numeroSerie,
                       String modelo, Double valorPorHora) {
        this.id = id;
        this.nome = nome;
        this.quantidadeTotal = quantidadeTotal;
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
    public Integer getQuantidadeTotal() {
        return quantidadeTotal;
    }

    //LÓGICA
    public void setQuantidadeTotal(Integer novaQuantidadeTotal) {
        int novoTotal = (novaQuantidadeTotal == null ? 0 : Math.max(0, novaQuantidadeTotal));
        Integer atualDisponivel = this.quantidadeDisponivel;
        Integer antigoTotal = (this.quantidadeTotal == null ? 0 : this.quantidadeTotal);
        int delta = novoTotal - antigoTotal;

        if (atualDisponivel == null) {
            this.quantidadeDisponivel = novoTotal;
        } else {
            int novaDisponivel = atualDisponivel + delta;
            if (novaDisponivel < 0) novaDisponivel = 0;
            if (novaDisponivel > novoTotal) novaDisponivel = novoTotal;
            this.quantidadeDisponivel = novaDisponivel;
        }
        this.quantidadeTotal = novoTotal;
    }

    public Integer getQuantidadeDisponivel() { return quantidadeDisponivel; }

    public void setQuantidadeDisponivel(Integer quantidadeDisponivel) {
        int total = (this.quantidadeTotal == null) ? 0 : this.quantidadeTotal;
        this.quantidadeDisponivel = (quantidadeDisponivel == null) ? 0
                : Math.max(0, Math.min(quantidadeDisponivel, total));
    }

    public String getNomeArquivoImagem() { return nomeArquivoImagem; }
    public void setNomeArquivoImagem(String nomeArquivoImagem) { this.nomeArquivoImagem = nomeArquivoImagem; }

    public String getTipoImagem() { return tipoImagem; }
    public void setTipoImagem(String tipoImagem) { this.tipoImagem = tipoImagem; }

    public String getCaminhoImagem() { return caminhoImagem; }
    public void setCaminhoImagem(String caminhoImagem) { this.caminhoImagem = caminhoImagem; }

    //MÉTODOS
    public void reduzirQuantidade(Integer quantidadeRecebida) {
        if (this.quantidadeDisponivel == null) this.quantidadeDisponivel = 0;
        if (quantidadeRecebida == null) quantidadeRecebida = 0;
        this.quantidadeDisponivel = Math.max(0, this.quantidadeDisponivel - quantidadeRecebida);
    }

    public void devolverQuantidade(Integer quantidadeRecebida) {
        if (this.quantidadeDisponivel == null) this.quantidadeDisponivel = 0;
        if (quantidadeRecebida == null) quantidadeRecebida = 0;
        this.quantidadeDisponivel = Math.max(0, this.quantidadeDisponivel + quantidadeRecebida);
    }
}