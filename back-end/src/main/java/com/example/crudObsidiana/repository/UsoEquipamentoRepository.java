package com.example.crudObsidiana.repository;

import com.example.crudObsidiana.dto.EquipamentoSobrepostoDTO;
import com.example.crudObsidiana.model.UsoEquipamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UsoEquipamentoRepository extends JpaRepository<UsoEquipamento, Long> {

    List<UsoEquipamento> findByOrcamento_Id(Long idOrcamento);
    List<UsoEquipamento> findByServico_Id(Long idServico);
    List<UsoEquipamento> findByEquipamento_Id(Long idEquipamento);

    // retorna uma lista de equipamentos de orçamentos confirmados de um período específico
    @NativeQuery("""
        SELECT
        u.fk_equipamento AS id_equipamento,
        e.quantidade_total,
        SUM(u.quantidade_usada) AS quantidade_usada,
        GROUP_CONCAT(o.descricao) AS orcamentos
        FROM uso_equipamento u
        JOIN equipamento e ON e.id = u.fk_equipamento
        JOIN orcamento o ON o.id = u.fk_orcamento
        WHERE
        o.data_termino > ?1 AND o.data_inicio < ?2
        AND o.status = 'Confirmado' AND u.fk_equipamento IN (?3)
        GROUP BY u.fk_equipamento;
    """)
    List<EquipamentoSobrepostoDTO> findSobrepostos(Date dataInicio, Date dataTermino, long[] idsEquipamentos);

//    METODO EM CASO DE ALTERACAO DE ORCAMENTO JÁ CRIADO
    @Modifying
    @Transactional
    @Query("delete from UsoEquipamento u where u.orcamento.id = :orcId")
    void deleteByOrcamentoId(@Param("orcId") Long orcId);

}
