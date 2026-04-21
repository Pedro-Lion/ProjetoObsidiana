package com.example.crudObsidiana.domain.ports;

import com.example.crudObsidiana.domain.entities.Servico;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServicoRepositoryPort {
    Servico save(Servico servico);
    Optional<Servico> findById(Long id);
    List<Servico> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    List<Servico> findAllById(List<Long> ids);

    Page<List<Servico>> findAll(Pageable pageable);
    Page<Servico> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<ServicoJpaEntity> findByBusca(String busca, Pageable pageable);
}