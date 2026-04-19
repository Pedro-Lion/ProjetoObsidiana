package com.example.crudObsidiana.domain.ports;

import com.example.crudObsidiana.domain.entities.Equipamento;

import java.util.List;
import java.util.Optional;

public interface EquipamentoRepositoryPort {

    Equipamento save(Equipamento equipamento);
    Optional<Equipamento> findById(Long id);
    List<Equipamento> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    List<Equipamento> saveAll(List<Equipamento> equipamentos);
    List<Equipamento> findAllById(List<Long> ids);              // ← adicionado
    List<Equipamento> findEquipamentosByOrcamentoId(Long orcamentoId);
}