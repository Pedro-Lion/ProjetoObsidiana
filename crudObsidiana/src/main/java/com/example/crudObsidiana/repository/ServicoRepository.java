package com.example.crudObsidiana.repository;

import com.example.crudObsidiana.model.Equipamento;
import com.example.crudObsidiana.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicoRepository extends JpaRepository<Servico, Long> {

}
