package com.example.crudObsidiana.repository;

import com.example.crudObsidiana.model.UsoEquipamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UsoEquipamentoRepository extends JpaRepository<UsoEquipamento, Long> {

    // ✅ Corrigido: o campo de ID em Orcamento é "id"
    List<UsoEquipamento> findByOrcamento_Id(Long idOrcamento);

    // (opcional) buscar por serviço
    List<UsoEquipamento> findByServico_Id(Long idServico);

    // (opcional) buscar por equipamento
    List<UsoEquipamento> findByEquipamento_Id(Long idEquipamento);
}
