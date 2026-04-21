package com.example.crudObsidiana.domain.ports;

import com.example.crudObsidiana.domain.entities.UsoEquipamento;

import java.util.List;
import java.util.Optional;

public interface UsoEquipamentoRepositoryPort {

    UsoEquipamento save(UsoEquipamento usoEquipamento);
    Optional<UsoEquipamento> findById(Long id);
    void deleteById(Long id);
    boolean existsById(Long id);
    List<UsoEquipamento> findAll();                          // ← adicionado
    List<UsoEquipamento> saveAll(List<UsoEquipamento> usos);
    List<UsoEquipamento> findByOrcamentoId(Long idOrcamento);
    List<UsoEquipamento> findByServicoId(Long idServico);
    List<UsoEquipamento> findByEquipamentoId(Long idEquipamento);
    void deleteByOrcamentoId(Long orcamentoId);
}