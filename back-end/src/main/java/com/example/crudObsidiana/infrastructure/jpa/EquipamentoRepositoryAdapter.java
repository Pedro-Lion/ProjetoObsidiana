package com.example.crudObsidiana.infrastructure.jpa;

import com.example.crudObsidiana.domain.entities.Equipamento;
import com.example.crudObsidiana.domain.ports.EquipamentoRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class EquipamentoRepositoryAdapter implements EquipamentoRepositoryPort {

    @Autowired private EquipamentoJpaRepository jpaRepository;
    @Autowired private EquipamentoMapper        mapper;

    @Override
    public Equipamento save(Equipamento equipamento) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(equipamento)));
    }

    @Override
    public Optional<Equipamento> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Equipamento> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain).collect(Collectors.toList());
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
    public List<Equipamento> saveAll(List<Equipamento> equipamentos) {
        return equipamentos.stream()
                .map(e -> mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(e))))
                .collect(Collectors.toList());
    }

    @Override
    public List<Equipamento> findAllById(List<Long> ids) {
        return jpaRepository.findAllById(ids).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Equipamento> findEquipamentosByOrcamentoId(Long orcamentoId) {
        return jpaRepository.findEquipamentosByOrcamentoId(orcamentoId).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }
}