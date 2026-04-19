package com.example.crudObsidiana.domain.use_cases;

import com.example.crudObsidiana.domain.entities.Profissional;
import com.example.crudObsidiana.domain.ports.ProfissionalRepositoryPort;
import com.example.crudObsidiana.interfaces.dto.ProfissionalDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Use Case de Profissional — migração do ProfissionalService original.
 */
@Service
public class ProfissionalUseCase {

    private final ProfissionalRepositoryPort profissionalRepository;

    @Autowired
    public ProfissionalUseCase(ProfissionalRepositoryPort profissionalRepository) {
        this.profissionalRepository = profissionalRepository;
    }

    public Profissional criarProfissional(ProfissionalDTO dto) {
        Profissional novoProfissional = new Profissional(
                dto.getNome(), dto.getDisponibilidade(), dto.getContato()
        );
        return profissionalRepository.save(novoProfissional);
    }
}