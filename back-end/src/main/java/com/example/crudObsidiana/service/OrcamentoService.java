package com.example.crudObsidiana.service;

import com.example.crudObsidiana.model.Orcamento;
import com.example.crudObsidiana.observer.OrcamentoObserver;
import com.example.crudObsidiana.repository.OrcamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrcamentoService {

    @Autowired
    private OrcamentoRepository orcamentoRepository;

    // O Spring injeta automaticamente todos os beans que implementam OrcamentoObserver
    @Autowired
    private List<OrcamentoObserver> observers;

    public Orcamento atualizarOrcamento(Orcamento orcamento) {
        Orcamento atualizado = orcamentoRepository.save(orcamento);
        notificarObservers(atualizado);
        return atualizado;
    }

    private void notificarObservers(Orcamento orcamento) {
        for (OrcamentoObserver observer : observers) {
            observer.onOrcamentoUpdated(orcamento);
        }
    }
}
