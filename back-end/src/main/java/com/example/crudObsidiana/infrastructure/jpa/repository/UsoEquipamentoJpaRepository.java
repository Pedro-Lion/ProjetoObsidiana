package com.example.crudObsidiana.infrastructure.jpa.repository;

import com.example.crudObsidiana.infrastructure.jpa.entity.UsoEquipamentoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UsoEquipamentoJpaRepository extends JpaRepository<UsoEquipamentoJpaEntity, Long> {

    List<UsoEquipamentoJpaEntity> findByOrcamento_Id(Long idOrcamento);
    List<UsoEquipamentoJpaEntity> findByServico_Id(Long idServico);
    List<UsoEquipamentoJpaEntity> findByEquipamento_Id(Long idEquipamento);

    @Modifying
    @Transactional
    @Query("delete from uso_equipamento u where u.orcamento.id = :orcId")
    void deleteByOrcamentoId(@Param("orcId") Long orcId);
}