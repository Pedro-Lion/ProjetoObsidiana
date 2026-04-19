package com.example.crudObsidiana.infrastructure.jpa;

import com.example.crudObsidiana.domain.entities.Profissional;
import com.example.crudObsidiana.domain.ports.ProfissionalRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProfissionalRepositoryAdapter implements ProfissionalRepositoryPort {

    @Autowired private ProfissionalJpaRepository jpaRepository;
    @Autowired private ProfissionalMapper        mapper;

    @Override
    public Profissional save(Profissional profissional) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(profissional)));
    }

    @Override
    public Optional<Profissional> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Profissional> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public List<Profissional> findAllById(List<Long> ids) {
        return jpaRepository.findAllById(ids).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}