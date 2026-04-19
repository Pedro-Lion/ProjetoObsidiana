package com.example.crudObsidiana.domain.use_cases;

import com.example.crudObsidiana.domain.entities.Orcamento;

/**
 * Interface Observer — contrato de domínio.
 * A implementação concreta (EquipamentoObserver) fica em infrastructure/observer.
 */
public interface OrcamentoObserver {
    void onOrcamentoUpdated(Orcamento orcamento, String statusAnterior, String novoStatus);
}