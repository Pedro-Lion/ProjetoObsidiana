package com.example.crudObsidiana.dto;

// DTO para receber os dados de login do usuário: email e senha.
// DTO = "Eu preciso receber essas informações no body da requisição".

public record LoginRequestDTO (String email, String senha) {}
