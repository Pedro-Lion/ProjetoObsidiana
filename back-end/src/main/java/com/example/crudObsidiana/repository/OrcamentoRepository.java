package com.example.crudObsidiana.repository;

import com.example.crudObsidiana.model.Orcamento;
import org.hibernate.annotations.processing.SQL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {
    public Optional<Orcamento> findByIdCalendar(String idCalendar);

    @SQL("SELECT COUNT(status) FROM orcamento where status = ?;")
    Integer countByStatus(String status);

    // Busca em todos os campos relevantes do orçamento, seus serviços e equipamentos (case-insensitive).
    // LEFT JOIN garante que orçamentos sem serviços/equipamentos também aparecem quando o termo
    // bate nos campos do próprio orçamento.
    // DISTINCT evita duplicatas quando múltiplos serviços ou equipamentos do mesmo orçamento casam.
    // Campos do orçamento: local do evento, descrição, status, valor total, datas.
    // Campos do serviço: nome, descrição.
    // Campos do equipamento: nome, categoria, marca.
    @Query(
            value = """
            SELECT DISTINCT o.* FROM orcamento o
            LEFT JOIN orcamento_servicos    os  ON o.id = os.orcamento_id
            LEFT JOIN servico               s   ON os.servico_id = s.id
            LEFT JOIN orcamento_equipamentos oeq ON o.id = oeq.orcamento_id
            LEFT JOIN equipamento            e   ON oeq.equipamento_id = e.id
            WHERE LOWER(COALESCE(o.local_evento, ''))  LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(o.descricao,    ''))  LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(o.status,       ''))  LIKE LOWER(CONCAT('%', :busca, '%'))
               OR CAST(COALESCE(o.valor_total, 0) AS VARCHAR) LIKE CONCAT('%', :busca, '%')
               OR CAST(o.data_inicio  AS VARCHAR)             LIKE CONCAT('%', :busca, '%')
               OR CAST(o.data_termino AS VARCHAR)             LIKE CONCAT('%', :busca, '%')
               OR LOWER(COALESCE(s.nome,     ''))  LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(s.descricao,''))  LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(e.nome,     ''))  LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(e.categoria,''))  LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(e.marca,    ''))  LIKE LOWER(CONCAT('%', :busca, '%'))
            """,
            countQuery = """
            SELECT COUNT(DISTINCT o.id) FROM orcamento o
            LEFT JOIN orcamento_servicos    os  ON o.id = os.orcamento_id
            LEFT JOIN servico               s   ON os.servico_id = s.id
            LEFT JOIN orcamento_equipamentos oeq ON o.id = oeq.orcamento_id
            LEFT JOIN equipamento            e   ON oeq.equipamento_id = e.id
            WHERE LOWER(COALESCE(o.local_evento, ''))  LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(o.descricao,    ''))  LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(o.status,       ''))  LIKE LOWER(CONCAT('%', :busca, '%'))
               OR CAST(COALESCE(o.valor_total, 0) AS VARCHAR) LIKE CONCAT('%', :busca, '%')
               OR CAST(o.data_inicio  AS VARCHAR)             LIKE CONCAT('%', :busca, '%')
               OR CAST(o.data_termino AS VARCHAR)             LIKE CONCAT('%', :busca, '%')
               OR LOWER(COALESCE(s.nome,     ''))  LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(s.descricao,''))  LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(e.nome,     ''))  LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(e.categoria,''))  LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(e.marca,    ''))  LIKE LOWER(CONCAT('%', :busca, '%'))
            """,
            nativeQuery = true
    )
    Page<Orcamento> findByBusca(@Param("busca") String busca, Pageable pageable);
}