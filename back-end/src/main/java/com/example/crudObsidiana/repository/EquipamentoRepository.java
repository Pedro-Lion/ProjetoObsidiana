package com.example.crudObsidiana.repository;

import com.example.crudObsidiana.model.Equipamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {
    List<Equipamento> findByOrcamentoId(Long orcamentoId);
}
