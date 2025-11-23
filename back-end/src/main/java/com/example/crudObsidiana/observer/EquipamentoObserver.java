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
    public void onOrcamentoUpdated(Orcamento orcamento,
                                   String statusAnterior,
                                   String novoStatus) {

        boolean eraConfirmado = "Confirmado".equalsIgnoreCase(statusAnterior);
        boolean ehConfirmado  = "Confirmado".equalsIgnoreCase(novoStatus);

        // Se não houve transição de "confirmado" <-> "não confirmado", não faz nada
        if (eraConfirmado == ehConfirmado) {
            System.out.println("\n[Observer] Status mudou, mas não afetou estoque (sem transição de confirmação).");
            return;
        }

        System.out.println("\n[Observer] Orçamento atualizado: " + orcamento.getId());
        System.out.printf("Status: '%s' ➜ '%s'%n", statusAnterior, novoStatus);

        // Busca todos os usos vinculados a esse orçamento
        List<UsoEquipamento> usos = usoEquipamentoRepository.findByOrcamento_Id(orcamento.getId());

        if (usos == null || usos.isEmpty()) {
            System.out.println("Nenhum uso de equipamento associado a este orçamento.");
            return;
        }

        // Define se vamos reservar (reduzir) ou devolver (aumentar)
        boolean reservar = !eraConfirmado && ehConfirmado;   // virou confirmado
        boolean devolver = eraConfirmado && !ehConfirmado;   // deixou de ser confirmado

        for (UsoEquipamento uso : usos) {
            Long idEquipamento = uso.getEquipamento().getId();
            Equipamento eq = equipamentoRepository.findById(idEquipamento)
                    .orElseThrow(() -> new RuntimeException(
                            "Equipamento não encontrado (ID: " + idEquipamento + ")"));

            int antes = eq.getQuantidadeDisponivel();
            int quantidadeUsada = uso.getQuantidadeUsada();

            if (reservar) {
                eq.reduzirQuantidade(quantidadeUsada);
                System.out.printf(" - RESERVA %s: disp. %d → %d (usou %d)%n",
                        eq.getNome(), antes, eq.getQuantidadeDisponivel(), quantidadeUsada);
            } else if (devolver) {
                eq.devolverQuantidade(quantidadeUsada);
                System.out.printf(" - DEVOLVE %s: disp. %d → %d (devolveu %d)%n",
                        eq.getNome(), antes, eq.getQuantidadeDisponivel(), quantidadeUsada);
            }

            equipamentoRepository.save(eq);
        }

        System.out.println("✅ Estoque atualizado com sucesso.\n");
    }
}
