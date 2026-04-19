package com.example.crudObsidiana.domain.ports;

import com.example.crudObsidiana.domain.entities.Profissional;

import java.util.List;
import java.util.Optional;

/**
 * Port de persistência para Profissional.
 *
 * O repository original não tinha métodos customizados,
 * apenas o CRUD padrão do JpaRepository.
 */
public interface ProfissionalRepositoryPort {

    // --- CRUD base ---
    Profissional save(Profissional profissional);
    Optional<Profissional> findById(Long id);
    List<Profissional> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);

    /**
     * Busca múltiplos profissionais por lista de IDs.
     * Necessário no OrcamentoService ao associar profissionais a um orçamento.
     */
    List<Profissional> findAllById(List<Long> ids);
}