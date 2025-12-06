package com.example.crudObsidiana.service;

import com.example.crudObsidiana.dto.EquipamentoDTO;
import com.example.crudObsidiana.model.Equipamento;
import com.example.crudObsidiana.repository.EquipamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EquipamentoService {

    @Autowired
    private EquipamentoRepository equipamentoRepository;

    public Equipamento criarEquipamento(EquipamentoDTO dto) {
        Equipamento equipamento = new Equipamento();
        equipamento.setNome(dto.getNome());
        equipamento.setQuantidadeTotal(dto.getQuantidadeTotal());
        equipamento.setCategoria(dto.getCategoria());
        equipamento.setMarca(dto.getMarca());
        equipamento.setNumeroSerie(dto.getNumeroSerie());
        equipamento.setModelo(dto.getModelo());
        equipamento.setValorPorHora(dto.getValorPorHora());
        equipamento.setQuantidadeDisponivel(dto.getQuantidadeTotal());

        return equipamentoRepository.save(equipamento);
    }
}