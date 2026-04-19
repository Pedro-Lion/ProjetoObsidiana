package com.example.crudObsidiana.domain.use_cases;

import com.example.crudObsidiana.domain.entities.Equipamento;
import com.example.crudObsidiana.domain.entities.Servico;
import com.example.crudObsidiana.domain.ports.EquipamentoRepositoryPort;
import com.example.crudObsidiana.domain.ports.ServicoRepositoryPort;
import com.example.crudObsidiana.interfaces.dto.ServicoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServicoUseCase {

    private final ServicoRepositoryPort     servicoRepository;
    private final EquipamentoRepositoryPort equipamentoRepository;

    @Autowired
    public ServicoUseCase(ServicoRepositoryPort servicoRepository,
                          EquipamentoRepositoryPort equipamentoRepository) {
        this.servicoRepository     = servicoRepository;
        this.equipamentoRepository = equipamentoRepository;
    }

    // =========================================================================
    // CRIAR
    // =========================================================================
    public Servico criarServico(ServicoDTO dto) {
        Servico servico = new Servico();
        servico.setNome(dto.getNome());
        servico.setDescricao(dto.getDescricao());
        servico.setHoras(dto.getHoras());
        servico.setValorPorHora(dto.getValorPorHora());

        if (dto.getEquipamentos() != null && !dto.getEquipamentos().isEmpty()) {
            servico.setEquipamentos(equipamentoRepository.findAllById(dto.getEquipamentos()));
        }

        return servicoRepository.save(servico);
    }

    // =========================================================================
    // EDITAR — busca o existente e atualiza os campos (sem duplicar)
    // =========================================================================
    public Servico editarServico(Long id, ServicoDTO dto) {

        // Busca o serviço existente no banco
        Servico existente = servicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Serviço não encontrado: " + id));

        // Atualiza apenas os campos enviados
        existente.setNome(dto.getNome());
        existente.setDescricao(dto.getDescricao());
        existente.setHoras(dto.getHoras());
        existente.setValorPorHora(dto.getValorPorHora());

        if (dto.getEquipamentos() != null && !dto.getEquipamentos().isEmpty()) {
            existente.setEquipamentos(equipamentoRepository.findAllById(dto.getEquipamentos()));
        } else {
            existente.setEquipamentos(new ArrayList<>());
        }

        return servicoRepository.save(existente);
    }
}