package com.example.crudObsidiana.observer;

import com.example.crudObsidiana.model.Orcamento;
import com.example.crudObsidiana.model.Equipamento;
import com.example.crudObsidiana.repository.EquipamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EquipamentoObserver implements OrcamentoObserver {

    @Autowired
    private EquipamentoRepository equipamentoRepository;

    @Override
    public void onOrcamentoUpdated(Orcamento orcamento) {
        System.out.println("🔔 [Observer] Orçamento atualizado: " + orcamento.getIdOrcamento());
        System.out.println("Marcando equipamentos como indisponíveis...");

        // Caso ainda não exista relação direta, criaremos um exemplo simulado:
        if (orcamento.getEquipamentos() != null) {
            for (Equipamento eq : orcamento.getEquipamentos()) {
                eq.setDisponivel(false);
                equipamentoRepository.save(eq);
                System.out.println(" - Equipamento " + eq.getNome() + " atualizado para indisponível");
            }
        } else {
            // Simulação caso o orçamento ainda não tenha equipamentos associados
            System.out.println("Nenhuma lista de equipamentos associada — simulação de atualização completa!");
        }
    }
}

