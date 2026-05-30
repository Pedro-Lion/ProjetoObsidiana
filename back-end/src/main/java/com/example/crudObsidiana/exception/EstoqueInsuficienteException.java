package com.example.crudObsidiana.exception;

import java.util.List;

public class EstoqueInsuficienteException extends RuntimeException {
    private final List<String> equipamentosEmConflito;

    public EstoqueInsuficienteException(List<String> lista) {
        super("Estoque insuficiente do(s) equipamento(s) selecionado(s)");
        this.equipamentosEmConflito = lista;
    }

    public EstoqueInsuficienteException(String message, List<String> lista) {
        super(message);
        this.equipamentosEmConflito = lista;
    }

    public List<String> getEquipamentosEmConflito() { return equipamentosEmConflito; }
}
