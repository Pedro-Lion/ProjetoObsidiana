package com.example.crudObsidiana.domain.ports;

import com.example.crudObsidiana.domain.entities.Orcamento;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrcamentoRepositoryPort {
    Orcamento save(Orcamento orcamento);
    Optional<Orcamento> findById(Long id);
    List<Orcamento> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);

    Page<List<Orcamento>> findAll(Pageable pageable);
    Optional<Orcamento> findByIdCalendar(String idCalendar);
    Integer countByStatus(String status);
    Page<Orcamento> findByBusca(String busca, Pageable pageable);
}