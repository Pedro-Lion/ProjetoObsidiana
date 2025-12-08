package com.example.crudObsidiana.repository;

import com.example.crudObsidiana.model.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {
    public Optional<Orcamento> findByIdCalendar(String idCalendar);
}
