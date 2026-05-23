package com.example.crudObsidiana.exception;

import java.util.List;

public class ErroEstoqueInsuficiente extends ErroApi  {
    private final List<String> equipamentosEmConflito;

    public ErroEstoqueInsuficiente(String message, List<String> equipamentosEmConflito) {
        super(message);
        this.equipamentosEmConflito = equipamentosEmConflito;
    }

    public List<String> getEquipamentosEmConflito() { return equipamentosEmConflito; }
}