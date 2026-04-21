package com.example.crudObsidiana.domain.use_cases;

import com.example.crudObsidiana.domain.entities.Equipamento;
import com.example.crudObsidiana.domain.ports.EquipamentoRepositoryPort;
import com.example.crudObsidiana.interfaces.dto.EquipamentoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Use Case de Equipamento — migração do EquipamentoService original.
 */
@Service
public class EquipamentoUseCase {

    private final EquipamentoRepositoryPort equipamentoRepository;

    @Autowired
    public EquipamentoUseCase(EquipamentoRepositoryPort equipamentoRepository) {
        this.equipamentoRepository = equipamentoRepository;
    }

    public Equipamento criarEquipamento(EquipamentoDTO dto) {
        Equipamento equipamento = new Equipamento();
        equipamento.setNome(dto.getNome());
        equipamento.setQuantidadeTotal(dto.getQuantidadeTotal()); // setter já ajusta disponível
        equipamento.setCategoria(dto.getCategoria());
        equipamento.setMarca(dto.getMarca());
        equipamento.setNumeroSerie(dto.getNumeroSerie());
        equipamento.setModelo(dto.getModelo());
        equipamento.setValorPorHora(dto.getValorPorHora());
        // garante disponível = total na criação (igual ao original)
        equipamento.setQuantidadeDisponivel(
                equipamento.getQuantidadeTotal() == null ? 0 : equipamento.getQuantidadeTotal()
        );
        return equipamentoRepository.save(equipamento);
    }
}