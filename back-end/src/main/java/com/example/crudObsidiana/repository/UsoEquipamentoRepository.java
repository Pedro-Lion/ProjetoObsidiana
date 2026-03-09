package com.example.crudObsidiana.repository;

import com.example.crudObsidiana.model.UsoEquipamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
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

//    METODO EM CASO DE ALTERACAO DE ORCAMENTO JÁ CRIADO
    @Modifying
    @Transactional
    @Query("delete from UsoEquipamento u where u.orcamento.id = :orcId")
    void deleteByOrcamentoId(@Param("orcId") Long orcId);

}
