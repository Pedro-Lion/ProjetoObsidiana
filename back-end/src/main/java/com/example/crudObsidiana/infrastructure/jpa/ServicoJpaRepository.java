package com.example.crudObsidiana.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicoJpaRepository extends JpaRepository<ServicoJpaEntity, Long> {
    // sem métodos customizados no original
}