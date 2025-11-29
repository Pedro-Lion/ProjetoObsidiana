package com.example.crudObsidiana.service;

import com.example.crudObsidiana.dto.OrcamentoDTO;
import com.example.crudObsidiana.model.Orcamento;
import com.example.crudObsidiana.repository.OrcamentoRepository;
import org.springframework.stereotype.Service;

@Service
public class OrcamentoService {

    private final OrcamentoRepository orcamentoRepository;

    public OrcamentoService(OrcamentoRepository orcamentoRepository) {
        this.orcamentoRepository = orcamentoRepository;
    }

    public void excluirOrcamento(Long id) {
        orcamentoRepository.deleteById(id);
    }

    public Orcamento atualizarOrcamento(Long id, OrcamentoDTO dto) {
        Orcamento existente = orcamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));

        existente.setDescricao(dto.getDescricao());
        existente.setDataEvento(dto.getDataEvento());
        existente.setDuracaoEvento(dto.getDuracaoEvento());
        existente.setLocalEvento(dto.getLocalEvento());
        existente.setValorTotal(dto.getValorTotal());
        existente.setStatus(dto.getStatus());

        return orcamentoRepository.save(existente);
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
}
