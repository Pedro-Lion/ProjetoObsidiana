package com.example.crudObsidiana.dto;

// DTO usado no PUT /api/usuario/{id} para atualizar dados do perfil do usuário.
// Campos opcionais — apenas os que vierem preenchidos serão atualizados.
// - nome:  novo nome de exibição
// - senha: nova senha em texto puro (será codificada antes de salvar)
public record UpdateUsuarioRequestDTO(String nome, String senha) {}
