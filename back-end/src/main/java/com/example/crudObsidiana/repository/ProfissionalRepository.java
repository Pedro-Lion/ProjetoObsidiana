package com.example.crudObsidiana.repository;

import com.example.crudObsidiana.model.Profissional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfissionalRepository extends JpaRepository<Profissional, Long> {

    Page<Profissional> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

}