package com.example.crudObsidiana.infrastructure.jpa.repository;

import com.example.crudObsidiana.infrastructure.jpa.entity.ServicoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicoJpaRepository extends JpaRepository<ServicoJpaEntity, Long> {
    // sem métodos customizados no original
}