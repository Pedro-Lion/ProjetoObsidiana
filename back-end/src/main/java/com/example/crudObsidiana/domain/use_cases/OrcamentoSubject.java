package com.example.crudObsidiana.domain.use_cases;

import com.example.crudObsidiana.domain.entities.Orcamento;

/**
 * Interface Subject do padrão Observer.
 */
public interface OrcamentoSubject {
    void registerObserver(OrcamentoObserver observer);
    void removeObserver(OrcamentoObserver observer);
    void notifyObservers(Orcamento orcamento, String statusAnterior, String novoStatus);
}