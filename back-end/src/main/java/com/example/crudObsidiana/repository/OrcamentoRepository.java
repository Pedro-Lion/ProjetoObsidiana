package com.example.crudObsidiana.repository;

import com.example.crudObsidiana.model.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {
    String findByIdCalendar(String idCalendar);
    Integer countByStatus(String status);
}
