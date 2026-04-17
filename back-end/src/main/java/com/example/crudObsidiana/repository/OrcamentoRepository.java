package com.example.crudObsidiana.repository;

import com.example.crudObsidiana.model.Orcamento;
import org.hibernate.annotations.processing.SQL;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {
    public Optional<Orcamento> findByIdCalendar(String idCalendar);

    @SQL("SELECT COUNT(status) FROM orcamento where status = ?;")
    Integer countByStatus(String status);
}
