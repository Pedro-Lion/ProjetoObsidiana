package com.example.crudObsidiana.infrastructure.jpa.repository;

import com.example.crudObsidiana.infrastructure.jpa.entity.EquipamentoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Spring Data JPA repository — trabalha exclusivamente com EquipamentoJpaEntity.
 */
public interface EquipamentoJpaRepository extends JpaRepository<EquipamentoJpaEntity, Long> {

    @Query("SELECT e FROM EquipamentoJpaEntity e JOIN e.orcamentos o WHERE o.id = :orcamentoId")
    List<EquipamentoJpaEntity> findEquipamentosByOrcamentoId(@Param("orcamentoId") Long orcamentoId);

    Page<EquipamentoJpaEntity> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    // Busca em todos os campos relevantes do equipamento (case-insensitive)
    // Permite pesquisar por nome, categoria, marca, modelo, número de série e valor
    @Query(
            value = """
            SELECT * FROM equipamento e
            WHERE LOWER(COALESCE(e.nome,         '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(e.categoria,    '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(e.marca,        '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(e.modelo,       '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(e.numero_serie, '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR CAST(COALESCE(e.valor_por_hora, 0) AS VARCHAR) LIKE CONCAT('%', :busca, '%')
            """,
            countQuery = """
            SELECT COUNT(*) FROM equipamento e
            WHERE LOWER(COALESCE(e.nome,         '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(e.categoria,    '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(e.marca,        '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(e.modelo,       '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(e.numero_serie, '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR CAST(COALESCE(e.valor_por_hora, 0) AS VARCHAR) LIKE CONCAT('%', :busca, '%')
            """,
            nativeQuery = true
    )
    Page<EquipamentoJpaEntity> findByBusca(@Param("busca") String busca, Pageable pageable);
}