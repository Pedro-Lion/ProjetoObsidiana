package com.example.crudObsidiana.observer;

import com.example.crudObsidiana.model.Equipamento;
import com.example.crudObsidiana.model.Orcamento;
import com.example.crudObsidiana.model.UsoEquipamento;
import com.example.crudObsidiana.repository.EquipamentoRepository;
import com.example.crudObsidiana.repository.UsoEquipamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EquipamentoObserver implements OrcamentoObserver {

    @Autowired
    private EquipamentoRepository equipamentoRepository;

    @Autowired
    private UsoEquipamentoRepository usoEquipamentoRepository;

    @Override
    public void onOrcamentoUpdated(Orcamento orcamento) {
        System.out.println("\n🔔 [Observer] Orçamento atualizado: " + orcamento.getId());
        System.out.println("Atualizando estoque com base em uso_equipamento...");

        // Busca todos os usos vinculados a esse orçamento
        List<UsoEquipamento> usos = usoEquipamentoRepository.findByOrcamento_Id(orcamento.getId());

        if (usos == null || usos.isEmpty()) {
            System.out.println("Nenhum uso de equipamento associado a este orçamento.");
            return;
        }

        // Processa cada uso encontrado
        for (UsoEquipamento uso : usos) {
            Long idEquipamento = uso.getEquipamento().getId();
            Equipamento eq = equipamentoRepository.findById(idEquipamento)
                    .orElseThrow(() -> new RuntimeException("Equipamento não encontrado (ID: " + idEquipamento + ")"));

            int antes = eq.getQuantidadeDisponivel();
            int quantidadeUsada = uso.getQuantidadeUsada();

            // Reduz o estoque respeitando limites
            int novaDisponivel = Math.max(0, antes - quantidadeUsada);
            eq.setQuantidadeDisponivel(novaDisponivel);

            equipamentoRepository.save(eq);

            System.out.printf(" - %s: disponível %d → %d (usou %d)%n",
                    eq.getNome(), antes, novaDisponivel, quantidadeUsada);
        }

        System.out.println("✅ Estoque atualizado com sucesso.\n");
    }
}
