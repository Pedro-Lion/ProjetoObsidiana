package com.example.crudObsidiana.domain.ports;

import com.example.crudObsidiana.domain.entities.Equipamento;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EquipamentoRepositoryPort {
    Equipamento save(Equipamento equipamento);
    Optional<Equipamento> findById(Long id);
    List<Equipamento> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    List<Equipamento> saveAll(List<Equipamento> equipamentos);
    List<Equipamento> findAllById(List<Long> ids);              // ← adicionado
    List<Equipamento> findEquipamentosByOrcamentoId(Long orcamentoId);

    Page<List<Equipamento>> findAll(Pageable pageable);
    Page<Equipamento> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<Equipamento> findByBusca(String busca, Pageable pageable);
}