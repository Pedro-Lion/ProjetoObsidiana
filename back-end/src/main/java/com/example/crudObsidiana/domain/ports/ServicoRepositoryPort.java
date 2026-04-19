package com.example.crudObsidiana.domain.ports;

import com.example.crudObsidiana.domain.entities.Servico;

import java.util.List;
import java.util.Optional;

/**
 * Port de persistência para Servico.
 *
 * O repository original não tinha métodos customizados,
 * apenas o CRUD padrão do JpaRepository.
 */
public interface ServicoRepositoryPort {

    // --- CRUD base ---
    Servico save(Servico servico);
    Optional<Servico> findById(Long id);
    List<Servico> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);

    /**
     * Busca múltiplos serviços por lista de IDs.
     * Necessário no OrcamentoService ao associar serviços a um orçamento.
     */
    List<Servico> findAllById(List<Long> ids);
}