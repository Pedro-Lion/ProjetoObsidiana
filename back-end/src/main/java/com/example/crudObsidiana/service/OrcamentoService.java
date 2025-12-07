package com.example.crudObsidiana.service;

import com.example.crudObsidiana.dto.KpisOrcamentoDTO;
import com.example.crudObsidiana.dto.OrcamentoDTO;
import com.example.crudObsidiana.model.Orcamento;
import com.example.crudObsidiana.repository.OrcamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrcamentoService {

    @Autowired
    private OrcamentoRepository orcamentoRepository;

    public OrcamentoService(OrcamentoRepository orcamentoRepository) {
        this.orcamentoRepository = orcamentoRepository;
    }

    public Orcamento criarOrcamento(OrcamentoDTO dto) {
        Orcamento orcamento = new Orcamento();
        orcamento.setDescricao(dto.getDescricao());
        orcamento.setDataEvento(dto.getDataEvento());
        orcamento.setDuracaoEvento(dto.getDuracaoEvento());
        orcamento.setLocalEvento(dto.getLocalEvento());
        orcamento.setValorTotal(dto.getValorTotal());
        orcamento.setStatus(dto.getStatus());

        return orcamentoRepository.save(orcamento);
    }

    public KpisOrcamentoDTO getKpis() {
        Integer aprovados = orcamentoRepository.countByStatus("Aprovado");
        Integer pendentes = orcamentoRepository.countByStatus("Pendente");
        Integer concluidos = orcamentoRepository.countByStatus("Concluido");

        return new KpisOrcamentoDTO(aprovados, pendentes, concluidos);
    }

}
