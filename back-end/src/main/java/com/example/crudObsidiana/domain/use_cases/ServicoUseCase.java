package com.example.crudObsidiana.domain.use_cases;

import com.example.crudObsidiana.domain.entities.Equipamento;
import com.example.crudObsidiana.domain.entities.Servico;
import com.example.crudObsidiana.domain.ports.EquipamentoRepositoryPort;
import com.example.crudObsidiana.domain.ports.ServicoRepositoryPort;
import com.example.crudObsidiana.interfaces.dto.ServicoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Use Case de Serviço — migração do ServicoService original.
 *
 * CORREÇÃO: o ServicoService original não chamava servicoRepository.save(),
 * então o serviço nunca persistia. Corrigido aqui.
 * O ServicoController original compensava chamando repository.save() diretamente —
 * o novo controller não precisa fazer isso.
 */
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

    public Servico criarServico(ServicoDTO dto) {
        Servico servico = new Servico();
        servico.setNome(dto.getNome());
        servico.setDescricao(dto.getDescricao());
        servico.setHoras(dto.getHoras());
        servico.setValorPorHora(dto.getValorPorHora());

        if (dto.getEquipamentos() != null && !dto.getEquipamentos().isEmpty()) {
            List<Equipamento> equipamentos =
                    equipamentoRepository.findAllById(dto.getEquipamentos());
            servico.setEquipamentos(equipamentos);
        }

        return servicoRepository.save(servico); // ✅ save que faltava no original
    }

    public Servico editarServico(Long id, ServicoDTO dto) {
        Servico servico = criarServico(dto);
        servico.setId(id);
        return servicoRepository.save(servico);
    }
}