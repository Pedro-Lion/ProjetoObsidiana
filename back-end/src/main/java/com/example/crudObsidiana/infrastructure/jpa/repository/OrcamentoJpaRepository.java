package com.example.crudObsidiana.infrastructure.jpa.repository;

import com.example.crudObsidiana.infrastructure.jpa.entity.OrcamentoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA repository — trabalha exclusivamente com OrcamentoJpaEntity.
 * Nunca é injetado fora da camada infrastructure.
 */
public interface OrcamentoJpaRepository extends JpaRepository<OrcamentoJpaEntity, Long> {
    Optional<OrcamentoJpaEntity> findByIdCalendar(String idCalendar);
    Integer countByStatus(String status);
}