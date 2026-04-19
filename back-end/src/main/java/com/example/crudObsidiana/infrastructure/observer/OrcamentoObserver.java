package com.example.crudObsidiana.infrastructure.observer;

import com.example.crudObsidiana.model.Orcamento;

// Observer para mudanças em Orcamentos
public interface OrcamentoObserver {

    /**
     * Disparado sempre que o status de um orçamento é atualizado.
     *
     * @param orcamento      Orçamento que teve o status alterado.
     * @param statusAnterior Status antes da alteração (pode ser null em criações antigas).
     * @param novoStatus     Novo status aplicado ao orçamento.
     */
    void onOrcamentoUpdated(Orcamento orcamento,
                            String statusAnterior,
                            String novoStatus);
}//FIM INTERFACE