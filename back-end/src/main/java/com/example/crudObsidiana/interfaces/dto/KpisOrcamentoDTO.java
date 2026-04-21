package com.example.crudObsidiana.interfaces.dto;

public record KpisOrcamentoDTO(
        Integer confirmados,
        Integer pendentes,
        Integer cancelados
) {}