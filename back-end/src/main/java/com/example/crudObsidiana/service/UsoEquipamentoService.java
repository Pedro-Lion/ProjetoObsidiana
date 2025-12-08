package com.example.crudObsidiana.service;

import com.example.crudObsidiana.dto.UsoEquipamentoDTO;
import com.example.crudObsidiana.model.Equipamento;
import com.example.crudObsidiana.model.Orcamento;
import com.example.crudObsidiana.model.Servico;
import com.example.crudObsidiana.model.UsoEquipamento;
import com.example.crudObsidiana.repository.EquipamentoRepository;
import com.example.crudObsidiana.repository.OrcamentoRepository;
import com.example.crudObsidiana.repository.ServicoRepository;
import com.example.crudObsidiana.repository.UsoEquipamentoRepository;
import org.springframework.stereotype.Service;

@Service
public class UsoEquipamentoService {

    private final UsoEquipamentoRepository usoEquipamentoRepository;
    private final EquipamentoRepository equipamentoRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final ServicoRepository servicoRepository;

    public UsoEquipamentoService(UsoEquipamentoRepository usoEquipamentoRepository,
                                 EquipamentoRepository equipamentoRepository,
                                 OrcamentoRepository orcamentoRepository,
                                 ServicoRepository servicoRepository) {
        this.usoEquipamentoRepository = usoEquipamentoRepository;
        this.equipamentoRepository = equipamentoRepository;
        this.orcamentoRepository = orcamentoRepository;
        this.servicoRepository = servicoRepository;
    }

    public UsoEquipamento registrarUso(UsoEquipamentoDTO dto) {
        Equipamento equipamento = equipamentoRepository.findById(dto.getIdEquipamento())
                .orElseThrow(() -> new RuntimeException("Equipamento não encontrado (ID: " + dto.getIdEquipamento() + ")"));

        UsoEquipamento uso = new UsoEquipamento();
        uso.setEquipamento(equipamento);
        uso.setQuantidadeUsada(dto.getQuantidadeUsada());

        if (dto.getIdOrcamento() != null) {
            Orcamento orcamento = orcamentoRepository.findById(dto.getIdOrcamento())
                    .orElseThrow(() -> new RuntimeException("Orçamento não encontrado (ID: " + dto.getIdOrcamento() + ")"));
            uso.setOrcamento(orcamento);
        } else if (dto.getIdServico() != null) {
            Servico servico = servicoRepository.findById(dto.getIdServico())
                    .orElseThrow(() -> new RuntimeException("Serviço não encontrado (ID: " + dto.getIdServico() + ")"));
            uso.setServico(servico);
        } else {
            throw new RuntimeException("É necessário informar o ID de um orçamento ou serviço.");
        }

        // NÃO MEXER NO ESTOQUE AQUI (o Observer já está fazendo isso)

        return usoEquipamentoRepository.save(uso);
    }
}
