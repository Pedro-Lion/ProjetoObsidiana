package com.example.crudObsidiana.domain.ports;

import com.example.crudObsidiana.domain.entities.Orcamento;

import java.util.List;
import java.util.Optional;

/**
 * Port de persistência para Orcamento.
 *
 * Interface pura — zero imports de JPA, Spring ou qualquer framework.
 * O domínio declara o que precisa; a infraestrutura (OrcamentoRepositoryAdapter)
 * decide como cumprir usando JPA, MongoDB, arquivo, ou qualquer outro mecanismo.
 *
 * Métodos mapeados dos originals:
 *  - JpaRepository padrão: save, findById, findAll, deleteById, existsById
 *  - Customizados: findByIdCalendar, countByStatus
 */
public interface OrcamentoRepositoryPort {

    // --- CRUD base ---
    Orcamento save(Orcamento orcamento);
    Optional<Orcamento> findById(Long id);
    List<Orcamento> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);

    // --- Queries customizadas (vindas do OrcamentoRepository original) ---

    /**
     * Busca um orçamento pelo ID gerado pela API do Outlook/Calendar.
     * Usado para evitar duplicação de eventos sincronizados.
     */
    Optional<Orcamento> findByIdCalendar(String idCalendar);

    /**
     * Conta orçamentos por status (ex: "Pendente", "Aprovado", "Concluído").
     * Usado nos KPIs do dashboard.
     */
    Integer countByStatus(String status);
}