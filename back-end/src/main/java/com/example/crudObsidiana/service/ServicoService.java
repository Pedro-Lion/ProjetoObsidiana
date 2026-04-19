package com.example.crudObsidiana.service;

import com.example.crudObsidiana.interfaces.dto.ServicoDTO;
import com.example.crudObsidiana.domain.entities.Equipamento;
import com.example.crudObsidiana.model.Servico;
import com.example.crudObsidiana.repository.EquipamentoRepository;
import com.example.crudObsidiana.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
    public class ServicoService {

        @Autowired
        private ServicoRepository servicoRepository;

        @Autowired
        private EquipamentoRepository equipamentoRepository;

    public ServicoService(ServicoRepository servicoRepository, EquipamentoRepository equipamentoRepository) {
        this.servicoRepository = servicoRepository;
        this.equipamentoRepository = equipamentoRepository;
    }

    public Servico criarServico(ServicoDTO dto) {
            Servico servico = new Servico();
            servico.setNome(dto.getNome());
            servico.setDescricao(dto.getDescricao());
            servico.setHoras(dto.getHoras());
            servico.setValorPorHora(dto.getValorPorHora());

            if (dto.getEquipamentos() != null && !dto.getEquipamentos().isEmpty()) {
                List<Equipamento> equipamentos = equipamentoRepository.findAllById(dto.getEquipamentos());
                servico.setEquipamentos(equipamentos);
            }

            return servico;
        }
    }


