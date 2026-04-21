package com.example.crudObsidiana.infrastructure.jpa.adapter;

import com.example.crudObsidiana.domain.entities.UsoEquipamento;
import com.example.crudObsidiana.domain.ports.UsoEquipamentoRepositoryPort;
import com.example.crudObsidiana.infrastructure.jpa.mapper.UsoEquipamentoMapper;
import com.example.crudObsidiana.infrastructure.jpa.entity.UsoEquipamentoJpaEntity;
import com.example.crudObsidiana.infrastructure.jpa.repository.EquipamentoJpaRepository;
import com.example.crudObsidiana.infrastructure.jpa.repository.OrcamentoJpaRepository;
import com.example.crudObsidiana.infrastructure.jpa.repository.ServicoJpaRepository;
import com.example.crudObsidiana.infrastructure.jpa.repository.UsoEquipamentoJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UsoEquipamentoRepositoryAdapter implements UsoEquipamentoRepositoryPort {

    @Autowired private UsoEquipamentoJpaRepository jpaRepository;
    @Autowired private UsoEquipamentoMapper mapper;
    @Autowired private EquipamentoJpaRepository equipamentoJpaRepository;
    @Autowired private OrcamentoJpaRepository orcamentoJpaRepository;
    @Autowired private ServicoJpaRepository servicoJpaRepository;

    @Override
    public UsoEquipamento save(UsoEquipamento uso) {
        UsoEquipamentoJpaEntity jpa = mapper.toJpaEntity(uso);

        if (uso.getEquipamento() != null && uso.getEquipamento().getId() != null) {
            jpa.setEquipamento(equipamentoJpaRepository.findById(uso.getEquipamento().getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Equipamento não encontrado: " + uso.getEquipamento().getId())));
        }
        if (uso.getOrcamento() != null && uso.getOrcamento().getId() != null) {
            jpa.setOrcamento(orcamentoJpaRepository.findById(uso.getOrcamento().getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Orçamento não encontrado: " + uso.getOrcamento().getId())));
        }
        if (uso.getServico() != null && uso.getServico().getId() != null) {
            jpa.setServico(servicoJpaRepository.findById(uso.getServico().getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Serviço não encontrado: " + uso.getServico().getId())));
        }

        return mapper.toDomain(jpaRepository.save(jpa));
    }

    @Override
    public List<UsoEquipamento> saveAll(List<UsoEquipamento> usos) {
        return usos.stream().map(this::save).collect(Collectors.toList());
    }

    @Override
    public Optional<UsoEquipamento> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<UsoEquipamento> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) { jpaRepository.deleteById(id); }

    @Override
    public boolean existsById(Long id) { return jpaRepository.existsById(id); }

    @Override
    public List<UsoEquipamento> findByOrcamentoId(Long idOrcamento) {
        return jpaRepository.findByOrcamento_Id(idOrcamento)
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<UsoEquipamento> findByServicoId(Long idServico) {
        return jpaRepository.findByServico_Id(idServico)
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<UsoEquipamento> findByEquipamentoId(Long idEquipamento) {
        return jpaRepository.findByEquipamento_Id(idEquipamento)
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteByOrcamentoId(Long orcamentoId) {
        jpaRepository.deleteByOrcamentoId(orcamentoId);
    }
}