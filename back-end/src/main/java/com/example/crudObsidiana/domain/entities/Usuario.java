package com.example.crudObsidiana.domain.entities;

/**
 * Entidade de domínio — zero dependências de framework.
 * Não implementa UserDetails aqui; isso é responsabilidade
 * do CustomUserDetailsService na camada de infrastructure/security.
 */
public class Usuario {

    private Long id;
    private String nome;
    private String email;
    private String senha;
    private String nomeArquivoImagem;
    private String tipoImagem;
    private String caminhoImagem;

    // -------------------------------------------------------------------------
    // CONSTRUCTORS
    // -------------------------------------------------------------------------
    public Usuario() {}

    public Usuario(Long id, String nome, String email, String senha) {
        this.id    = id;
        this.nome  = nome;
        this.email = email;
        this.senha = senha;
    }

    public Usuario(Long id, String nome, String email, String senha,
                   String nomeArquivoImagem, String tipoImagem, String caminhoImagem) {
        this.id                 = id;
        this.nome               = nome;
        this.email              = email;
        this.senha              = senha;
        this.nomeArquivoImagem  = nomeArquivoImagem;
        this.tipoImagem         = tipoImagem;
        this.caminhoImagem      = caminhoImagem;
    }

    // -------------------------------------------------------------------------
    // GETTERS & SETTERS
    // -------------------------------------------------------------------------
    public Long getId()                              { return id; }
    public void setId(Long id)                      { this.id = id; }

    public String getNome()                          { return nome; }
    public void setNome(String nome)                 { this.nome = nome; }

    public String getEmail()                         { return email; }
    public void setEmail(String email)               { this.email = email; }

    public String getSenha()                         { return senha; }
    public void setSenha(String senha)               { this.senha = senha; }

    public String getNomeArquivoImagem()             { return nomeArquivoImagem; }
    public void setNomeArquivoImagem(String v)       { this.nomeArquivoImagem = v; }

    public String getTipoImagem()                    { return tipoImagem; }
    public void setTipoImagem(String v)              { this.tipoImagem = v; }

    public String getCaminhoImagem()                 { return caminhoImagem; }
    public void setCaminhoImagem(String v)           { this.caminhoImagem = v; }
}