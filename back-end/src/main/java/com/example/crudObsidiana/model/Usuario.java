package com.example.crudObsidiana.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Usuario {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Schema(description = "ID do Usuário", example = "1")
  private Long id;

  @Schema(description = "Nome do usuário", example = "José da Silva ADM")
  private String nome;

  @Schema(description = "E-mail do Usuário", example = "js.multimidia@email.com")
  private String email;

  @Schema(description = "Senha de acesso", example = "123AcessarGestao@")
  private String senha;

  @Schema(description = "Nome do arquivo de imagem", example = "fotoCamera")
  private String nomeArquivoImagem;

  @Schema(description = "Tipo da imagem", example = "jpg")
  private String tipoImagem;

  @Schema(description = "Caminho da imagem", example = "src/uploads/fotoCamera.jpg")
  private String caminhoImagem;

//  CONSTRUCTORS
  public Usuario() {
  }
  public Usuario(Long id, String nome, String email, String senha) {
    this.id = id;
    this.nome = nome;
    this.email = email;
    this.senha = senha;
  }
  public Usuario(Long id, String nome, String email, String senha, String nomeArquivoImagem, String tipoImagem, String caminhoImagem) {
    this.id = id;
    this.nome = nome;
    this.email = email;
    this.senha = senha;
    this.nomeArquivoImagem = nomeArquivoImagem;
    this.tipoImagem = tipoImagem;
    this.caminhoImagem = caminhoImagem;
  }

//  GETTERS & SETTERS
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
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  public String getSenha() {
    return senha;
  }
  public void setSenha(String senha) {
    this.senha = senha;
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


} //FIM CLASSE



