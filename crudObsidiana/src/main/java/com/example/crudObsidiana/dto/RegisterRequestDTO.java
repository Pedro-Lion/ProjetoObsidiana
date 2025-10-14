package com.example.crudObsidiana.dto;

// DTO para receber os dados de cadastro do usuário: nome, email e senha.
// DTO = "Eu preciso receber essas informações no body da requisição".

public record RegisterRequestDTO (String nome, String email, String senha) {}
