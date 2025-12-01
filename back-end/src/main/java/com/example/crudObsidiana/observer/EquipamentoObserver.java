package com.example.crudObsidiana.observer;

import com.example.crudObsidiana.model.Equipamento;
import com.example.crudObsidiana.model.Orcamento;
import com.example.crudObsidiana.model.UsoEquipamento;
import com.example.crudObsidiana.repository.EquipamentoRepository;
import com.example.crudObsidiana.repository.UsoEquipamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

// Esse Observer atualiza o estoque de equipamentos sempre que um orçamento entra ou sai do status "Confirmado".
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

        // Normaliza para evitar NullPointer
        boolean eraConfirmado = "Confirmado".equalsIgnoreCase(
                statusAnterior != null ? statusAnterior : ""
        );
        boolean ehConfirmado = "Confirmado".equalsIgnoreCase(
                novoStatus != null ? novoStatus : ""
        );

        // Se não houve transição relativa a "Confirmado", não mexe em estoque
        if (eraConfirmado == ehConfirmado) {
            System.out.printf(
                    "\n[Observer] Orçamento %d teve status alterado ('%s' -> '%s'), " +
                            "mas não houve transição de/para 'Confirmado'. Estoque inalterado.%n",
                    orcamento.getId(), statusAnterior, novoStatus
            );
            return;
        }

        System.out.println("\n[Observer] Atualização de orçamento detectada para estoque.");
        System.out.printf("Orçamento ID: %d | Status: '%s' ➜ '%s'%n",
                orcamento.getId(), statusAnterior, novoStatus);

        // Busca todos os usos de equipamento vinculados a esse orçamento
        List<UsoEquipamento> usos = usoEquipamentoRepository.findByOrcamento_Id(orcamento.getId());

        if (usos == null || usos.isEmpty()) {
            System.out.println("Nenhum uso de equipamento associado a este orçamento.");
            return;
        }

        boolean reservar = !eraConfirmado && ehConfirmado;   // entrou em Confirmado
        boolean devolver = eraConfirmado && !ehConfirmado;   // saiu de Confirmado

        for (UsoEquipamento uso : usos) {
            Long idEquipamento = uso.getEquipamento().getId();

            Equipamento eq = equipamentoRepository.findById(idEquipamento)
                    .orElseThrow(() -> new RuntimeException(
                            "Equipamento não encontrado (ID: " + idEquipamento + ")"
                    ));

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

        System.out.println("Estoque de equipamentos atualizado com sucesso.\n");
    }

} // FIM CLASSE
