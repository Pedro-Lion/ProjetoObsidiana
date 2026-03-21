package com.example.crudObsidiana.dto;

// DTO para receber os dados de login do usuário: email e senha.
// DTO = "Eu preciso receber essas informações no body da requisição".

// OWASP A05:2025 - Injection
// Desc: Validação de entrada

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO (

        @NotBlank(message = "Email é obrigatório!")
        @Email(message = "Email inválido")
        @Size(max = 45, message = "Email muito longo")
        String email,

        @NotBlank(message = "Senha é obrigatória!")
        @Size(min = 6, max = 45, message = "Senha deve ter de 6 a 45 caracteres" )
        String senha
) {}
