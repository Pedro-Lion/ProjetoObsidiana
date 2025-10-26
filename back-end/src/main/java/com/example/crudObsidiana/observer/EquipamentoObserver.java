package com.example.crudObsidiana.observer;

import com.example.crudObsidiana.model.Orcamento;
import com.example.crudObsidiana.model.Equipamento;
import com.example.crudObsidiana.repository.EquipamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Observador responsável por reagir a alterações em orçamentos.
 * Quando um orçamento é atualizado, reduz a quantidade disponível dos equipamentos utilizados.
 */
@Component
public class EquipamentoObserver implements OrcamentoObserver {

    @Autowired
    private EquipamentoRepository equipamentoRepository;

    @Override
    public void onOrcamentoUpdated(Orcamento orcamento) {
        System.out.println("\n🔔 [Observer] Orçamento atualizado: " + orcamento.getId());
        System.out.println("Atualizando estoque dos equipamentos associados...");

        if (orcamento.getEquipamentos() == null || orcamento.getEquipamentos().isEmpty()) {
            System.out.println("Nenhum equipamento vinculado a este orçamento.");
            return;
        }

        for (Equipamento eq : orcamento.getEquipamentos()) {

            // Quantidade usada neste orçamento (por enquanto fixo — pode virar campo futuro)
            int quantidadeUsada = 1;

            int antes = eq.getQuantidadeDisponivel();

            // Chamando o metodo reduzirQuantidade da classe Equipamento
            eq.reduzirQuantidade(quantidadeUsada);

            // Nunca ultrapassa o total nem fica negativo
            if (eq.getQuantidadeDisponivel() > eq.getQuantidade()) {
                eq.setQuantidadeDisponivel(eq.getQuantidade());
            }
            if (eq.getQuantidadeDisponivel() < 0) {
                eq.setQuantidadeDisponivel(0);
            }

            equipamentoRepository.save(eq);

            int depois = eq.getQuantidadeDisponivel();

            System.out.printf(" - %s: disponível %d → %d (total: %d)%n",
                    eq.getNome(), antes, depois, eq.getQuantidade());
        }

        System.out.println("✅ Estoque atualizado com sucesso.\n");
    }
}
