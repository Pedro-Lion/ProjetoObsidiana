package com.example.crudObsidiana.domain.use_cases;

import com.example.crudObsidiana.domain.entities.*;
import com.example.crudObsidiana.domain.ports.*;
import com.example.crudObsidiana.interfaces.dto.UsoEquipamentoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Use Case de UsoEquipamento — migração do UsoEquipamentoService original.
 * NÃO mexe em estoque diretamente — o EquipamentoObserver faz isso via notificação.
 */
@Service
public class UsoEquipamentoUseCase {

    private final UsoEquipamentoRepositoryPort usoEquipamentoRepository;
    private final EquipamentoRepositoryPort    equipamentoRepository;
    private final OrcamentoRepositoryPort      orcamentoRepository;
    private final ServicoRepositoryPort        servicoRepository;

    @Autowired
    public UsoEquipamentoUseCase(
            UsoEquipamentoRepositoryPort usoEquipamentoRepository,
            EquipamentoRepositoryPort equipamentoRepository,
            OrcamentoRepositoryPort orcamentoRepository,
            ServicoRepositoryPort servicoRepository) {
        this.usoEquipamentoRepository = usoEquipamentoRepository;
        this.equipamentoRepository    = equipamentoRepository;
        this.orcamentoRepository      = orcamentoRepository;
        this.servicoRepository        = servicoRepository;
    }

    public UsoEquipamento registrarUso(UsoEquipamentoDTO dto) {
        Equipamento equipamento = equipamentoRepository.findById(dto.getIdEquipamento())
                .orElseThrow(() -> new RuntimeException(
                        "Equipamento não encontrado (ID: " + dto.getIdEquipamento() + ")"));

        UsoEquipamento uso = new UsoEquipamento();
        uso.setEquipamento(equipamento);
        uso.setQuantidadeUsada(dto.getQuantidadeUsada());

        if (dto.getIdOrcamento() != null) {
            Orcamento orcamento = orcamentoRepository.findById(dto.getIdOrcamento())
                    .orElseThrow(() -> new RuntimeException(
                            "Orçamento não encontrado (ID: " + dto.getIdOrcamento() + ")"));
            uso.setOrcamento(orcamento);

        } else if (dto.getIdServico() != null) {
            Servico servico = servicoRepository.findById(dto.getIdServico())
                    .orElseThrow(() -> new RuntimeException(
                            "Serviço não encontrado (ID: " + dto.getIdServico() + ")"));
            uso.setServico(servico);

        } else {
            throw new RuntimeException("É necessário informar o ID de um orçamento ou serviço.");
        }

        uso.validarDonoUnico(); // regra de negócio do domínio
        return usoEquipamentoRepository.save(uso);
    }
}