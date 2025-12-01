package com.example.crudObsidiana.observer;

import com.example.crudObsidiana.model.Orcamento;

public interface OrcamentoSubject {

    void registerObserver(OrcamentoObserver observer);

    void removeObserver(OrcamentoObserver observer);

    void notifyObservers(Orcamento orcamento,
                         String statusAnterior,
                         String novoStatus);
}
