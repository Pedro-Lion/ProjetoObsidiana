package com.example.crudObsidiana.infrastructure.jpa.repository;

import com.example.crudObsidiana.infrastructure.jpa.entity.ProfissionalJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfissionalJpaRepository extends JpaRepository<ProfissionalJpaEntity, Long> {
    // sem métodos customizados no original
}