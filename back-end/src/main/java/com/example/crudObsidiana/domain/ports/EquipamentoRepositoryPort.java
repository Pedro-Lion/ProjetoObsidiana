package com.example.crudObsidiana.domain.ports;

import com.example.crudObsidiana.domain.entities.Equipamento;

import java.util.List;
import java.util.Optional;

/**
 * Port de persistência para Equipamento.
 *
 * Métodos mapeados dos originais:
 *  - JpaRepository padrão: save, findById, findAll, deleteById, existsById, saveAll
 *  - Customizados: findEquipamentosByOrcamentoId
 */
public interface EquipamentoRepositoryPort {

    // --- CRUD base ---
    Equipamento save(Equipamento equipamento);
    Optional<Equipamento> findById(Long id);
    List<Equipamento> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);

    /**
     * Salva uma lista de equipamentos de uma vez.
     * Usado pelo SampleDataLoader e por operações em lote.
     */
    List<Equipamento> saveAll(List<Equipamento> equipamentos);

    // --- Queries customizadas (vindas do EquipamentoRepository original) ---

    /**
     * Retorna todos os equipamentos vinculados a um orçamento específico.
     * Equivalente ao @Query JPQL original:
     * "SELECT e FROM Equipamento e JOIN e.orcamentos o WHERE o.id = :orcamentoId"
     */
    List<Equipamento> findEquipamentosByOrcamentoId(Long orcamentoId);
}