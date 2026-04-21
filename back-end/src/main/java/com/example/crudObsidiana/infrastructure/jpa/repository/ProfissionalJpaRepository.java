package com.example.crudObsidiana.infrastructure.jpa.repository;

import com.example.crudObsidiana.infrastructure.jpa.entity.ProfissionalJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProfissionalJpaRepository extends JpaRepository<ProfissionalJpaEntity, Long> {
    Page<ProfissionalJpaEntity> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    // Busca em todos os campos relevantes do profissional (case-insensitive)
    // Permite pesquisar por nome, disponibilidade, contato e categoria
    @Query(
            value = """
            SELECT * FROM profissional p
            WHERE LOWER(COALESCE(p.nome,            '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(p.disponibilidade, '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(p.contato,         '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(p.categoria,       '')) LIKE LOWER(CONCAT('%', :busca, '%'))
            """,
            countQuery = """
            SELECT COUNT(*) FROM profissional p
            WHERE LOWER(COALESCE(p.nome,            '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(p.disponibilidade, '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(p.contato,         '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(p.categoria,       '')) LIKE LOWER(CONCAT('%', :busca, '%'))
            """,
            nativeQuery = true
    )
    Page<ProfissionalJpaEntity> findByBusca(@Param("busca") String busca, Pageable pageable);
}