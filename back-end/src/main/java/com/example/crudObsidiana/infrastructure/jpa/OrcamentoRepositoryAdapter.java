package com.example.crudObsidiana.infrastructure.jpa;

import com.example.crudObsidiana.domain.entities.Orcamento;
import com.example.crudObsidiana.domain.ports.OrcamentoRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter que implementa OrcamentoRepositoryPort usando Spring Data JPA.
 *
 * É o único ponto do sistema onde domínio ↔ JPA se tocam via mapeamento.
 * Os Use Cases não sabem que este adapter existe — eles só conhecem a Port.
 *
 * @Component (não @Repository) porque ele não é um repositório JPA,
 * é um adaptador que usa um repositório JPA internamente.
 */
@Component
public class OrcamentoRepositoryAdapter implements OrcamentoRepositoryPort {

    @Autowired private OrcamentoJpaRepository jpaRepository;
    @Autowired private OrcamentoMapper        mapper;

    @Override
    public Orcamento save(Orcamento orcamento) {
        OrcamentoJpaEntity jpaEntity = mapper.toJpaEntity(orcamento);
        OrcamentoJpaEntity saved    = jpaRepository.save(jpaEntity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Orcamento> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Orcamento> findAll() {
        return jpaRepository.findAll()
                .stream()
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
    public Optional<Orcamento> findByIdCalendar(String idCalendar) {
        return jpaRepository.findByIdCalendar(idCalendar)
                .map(mapper::toDomain);
    }

    @Override
    public Integer countByStatus(String status) {
        return jpaRepository.countByStatus(status);
    }
}