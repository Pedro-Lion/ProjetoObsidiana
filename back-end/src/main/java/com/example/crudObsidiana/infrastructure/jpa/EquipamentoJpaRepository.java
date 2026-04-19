package com.example.crudObsidiana.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository — trabalha exclusivamente com EquipamentoJpaEntity.
 */
public interface EquipamentoJpaRepository extends JpaRepository<EquipamentoJpaEntity, Long> {

    @Query("SELECT e FROM EquipamentoJpaEntity e JOIN e.orcamentos o WHERE o.id = :orcamentoId")
    List<EquipamentoJpaEntity> findEquipamentosByOrcamentoId(@Param("orcamentoId") Long orcamentoId);
}