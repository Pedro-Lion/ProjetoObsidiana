package com.example.crudObsidiana.service;

import com.example.crudObsidiana.dto.ProfissionalDTO;
import com.example.crudObsidiana.model.Profissional;
import com.example.crudObsidiana.repository.ProfissionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfissionalService {

    @Autowired
    private ProfissionalRepository profissionalRepository;

    public Profissional criarProfissional(ProfissionalDTO dto) {
        Profissional novoProfissional = new Profissional(
            dto.getNome(), dto.getDisponibilidade(), dto.getContato()
        );

        return profissionalRepository.save(novoProfissional);
    }
}
