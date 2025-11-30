package com.example.crudObsidiana.service;

import com.example.crudObsidiana.dto.OrcamentoDTO;
import com.example.crudObsidiana.model.Equipamento;
import com.example.crudObsidiana.model.Orcamento;
import com.example.crudObsidiana.model.Profissional;
import com.example.crudObsidiana.model.Servico;
import com.example.crudObsidiana.repository.EquipamentoRepository;
import com.example.crudObsidiana.repository.OrcamentoRepository;
import com.example.crudObsidiana.repository.ProfissionalRepository;
import com.example.crudObsidiana.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrcamentoService {

    @Autowired
    private OrcamentoRepository orcamentoRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private EquipamentoRepository equipamentoRepository;

    @Autowired
    private ProfissionalRepository profissionalRepository;

    public Orcamento criarOrcamento(OrcamentoDTO dto) {
        Orcamento novoOrcamento = new Orcamento(
            dto.getDataEvento(),
            dto.getDuracaoEvento(),
            dto.getLocalEvento(),
            dto.getDescricao(),
            dto.getStatus(),
            dto.getValorTotal()
        );

        List<Servico> servicos = servicoRepository.findAllById(dto.getServicos());
        novoOrcamento.setServicos(servicos);

        List<Equipamento> equipamentos = equipamentoRepository.findAllById(dto.getEquipamentos());
        novoOrcamento.setEquipamentos(equipamentos);

        List<Profissional> profissionais = profissionalRepository.findAllById(dto.getProfissionais());
        novoOrcamento.setProfissionais(profissionais);

        return orcamentoRepository.save(novoOrcamento);
    }
}
