package com.example.crudObsidiana.infrastructure.jpa;

import com.example.crudObsidiana.domain.entities.Servico;
import com.example.crudObsidiana.domain.ports.ServicoRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ServicoRepositoryAdapter implements ServicoRepositoryPort {

    @Autowired private ServicoJpaRepository jpaRepository;
    @Autowired private ServicoMapper        mapper;

    @Override
    public Servico save(Servico servico) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(servico)));
    }

    @Override
    public Optional<Servico> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Servico> findAll() {
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
    public List<Servico> findAllById(List<Long> ids) {
        return jpaRepository.findAllById(ids).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}