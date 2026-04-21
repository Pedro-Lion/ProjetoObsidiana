package com.example.crudObsidiana.domain.ports;

import com.example.crudObsidiana.domain.entities.Profissional;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProfissionalRepositoryPort {
    Profissional save(Profissional profissional);
    Optional<Profissional> findById(Long id);
    List<Profissional> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    List<Profissional> findAllById(List<Long> ids);

    Page<List<Profissional>> findAll(Pageable pageable);
    Page<ProfissionalJpaEntity> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<ProfissionalJpaEntity> findByBusca(String busca, Pageable pageable);
}