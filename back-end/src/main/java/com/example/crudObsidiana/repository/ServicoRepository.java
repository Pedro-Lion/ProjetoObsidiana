package com.example.crudObsidiana.repository;

import com.example.crudObsidiana.model.Servico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServicoRepository extends JpaRepository<Servico, Long> {

    Page<Servico> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    // Busca em todos os campos relevantes do serviço e de seus equipamentos (case-insensitive).
    // LEFT JOIN garante que serviços sem equipamentos também aparecem quando o termo bate nos campos do serviço.
    // DISTINCT evita duplicatas quando múltiplos equipamentos do mesmo serviço casam com o termo.
    // Campos do serviço: nome, descrição, horas, valor por hora
    // Campos do equipamento: nome, categoria, marca, modelo
    @Query(
            value = """
            SELECT DISTINCT s.* FROM servico s
            LEFT JOIN servico_equipamento se ON s.id = se.servico_id
            LEFT JOIN equipamento e ON se.equipamento_id = e.id
            WHERE LOWER(COALESCE(s.nome,      '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(s.descricao, '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR CAST(s.horas AS VARCHAR)             LIKE CONCAT('%', :busca, '%')
               OR CAST(COALESCE(s.valor_por_hora, 0) AS VARCHAR) LIKE CONCAT('%', :busca, '%')
               OR LOWER(COALESCE(e.nome,      '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(e.categoria, '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(e.marca,     '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(e.modelo,    '')) LIKE LOWER(CONCAT('%', :busca, '%'))
            """,
            countQuery = """
            SELECT COUNT(DISTINCT s.id) FROM servico s
            LEFT JOIN servico_equipamento se ON s.id = se.servico_id
            LEFT JOIN equipamento e ON se.equipamento_id = e.id
            WHERE LOWER(COALESCE(s.nome,      '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(s.descricao, '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR CAST(s.horas AS VARCHAR)             LIKE CONCAT('%', :busca, '%')
               OR CAST(COALESCE(s.valor_por_hora, 0) AS VARCHAR) LIKE CONCAT('%', :busca, '%')
               OR LOWER(COALESCE(e.nome,      '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(e.categoria, '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(e.marca,     '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(e.modelo,    '')) LIKE LOWER(CONCAT('%', :busca, '%'))
            """,
            nativeQuery = true
    )
    Page<Servico> findByBusca(@Param("busca") String busca, Pageable pageable);

}