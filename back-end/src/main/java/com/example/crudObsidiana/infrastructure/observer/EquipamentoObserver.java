package com.example.crudObsidiana.infrastructure.observer;

import com.example.crudObsidiana.domain.entities.Equipamento;
import com.example.crudObsidiana.domain.entities.Orcamento;
import com.example.crudObsidiana.domain.entities.UsoEquipamento;
import com.example.crudObsidiana.domain.ports.EquipamentoRepositoryPort;
import com.example.crudObsidiana.domain.ports.UsoEquipamentoRepositoryPort;
import com.example.crudObsidiana.domain.use_cases.OrcamentoObserver;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Implementação concreta do OrcamentoObserver.
 * Atualiza estoque quando orçamento entra/sai do status "Confirmado".
 * Usa Ports — nunca JpaRepository diretamente.
 */
@Component
public class EquipamentoObserver implements OrcamentoObserver {

    private final EquipamentoRepositoryPort    equipamentoRepository;
    private final UsoEquipamentoRepositoryPort usoEquipamentoRepository;

    @Autowired
    public EquipamentoObserver(EquipamentoRepositoryPort equipamentoRepository,
                               UsoEquipamentoRepositoryPort usoEquipamentoRepository) {
        this.equipamentoRepository    = equipamentoRepository;
        this.usoEquipamentoRepository = usoEquipamentoRepository;
    }

    @Override
    @Transactional
    public void onOrcamentoUpdated(Orcamento orcamento,
                                   String statusAnterior,
                                   String novoStatus) {

        boolean eraConfirmado = "Confirmado".equalsIgnoreCase(statusAnterior != null ? statusAnterior : "");
        boolean ehConfirmado  = "Confirmado".equalsIgnoreCase(novoStatus     != null ? novoStatus     : "");

        if (eraConfirmado == ehConfirmado) return; // sem transição relevante

        List<UsoEquipamento> usos = usoEquipamentoRepository.findByOrcamentoId(orcamento.getId());
        if (usos == null || usos.isEmpty()) return;

        boolean reservar = !eraConfirmado && ehConfirmado;
        boolean devolver =  eraConfirmado && !ehConfirmado;

        for (UsoEquipamento uso : usos) {
            Long idEq = uso.getEquipamento().getId();
            Equipamento eq = equipamentoRepository.findById(idEq)
                    .orElseThrow(() -> new RuntimeException("Equipamento não encontrado: " + idEq));

            if (reservar) eq.reduzirQuantidade(uso.getQuantidadeUsada());
            else if (devolver) eq.devolverQuantidade(uso.getQuantidadeUsada());

            equipamentoRepository.save(eq);
        }
    }
}