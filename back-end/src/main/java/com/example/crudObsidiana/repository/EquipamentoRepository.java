package com.example.crudObsidiana.repository;

import com.example.crudObsidiana.model.Equipamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {
    @Query("SELECT e FROM Equipamento e JOIN e.orcamentos o WHERE o.id = :orcamentoId")
    List<Equipamento> findEquipamentosByOrcamentoId(@Param("orcamentoId") Long orcamentoId);

    Page<Equipamento> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

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
    Page<Equipamento> findByBusca(@Param("busca") String busca, Pageable pageable);
}
