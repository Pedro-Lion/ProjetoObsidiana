package com.example.crudObsidiana.repository;

import com.example.crudObsidiana.model.Equipamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {
    @Query("SELECT e FROM Equipamento e JOIN e.orcamentos o WHERE o.id = :orcamentoId")
    List<Equipamento> findEquipamentosByOrcamentoId(@Param("orcamentoId") Long orcamentoId);
}
