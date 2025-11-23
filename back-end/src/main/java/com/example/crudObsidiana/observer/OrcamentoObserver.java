package com.example.crudObsidiana.observer;

import com.example.crudObsidiana.model.Orcamento;

public interface OrcamentoObserver {

    void onOrcamentoUpdated(Orcamento orcamento,
                            String statusAnterior,
                            String novoStatus);
}
