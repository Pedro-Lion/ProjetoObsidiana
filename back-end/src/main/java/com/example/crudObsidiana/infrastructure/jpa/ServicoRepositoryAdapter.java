package com.example.crudObsidiana.infrastructure.jpa;

import com.example.crudObsidiana.domain.entities.Servico;
import com.example.crudObsidiana.domain.ports.ServicoRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter de Serviço — único ponto onde domínio ↔ JPA se tocam.
 *
 * CORREÇÃO PRINCIPAL no método save():
 * Buscamos as EquipamentoJpaEntity gerenciadas pelo JPA antes de salvar,
 * garantindo que o relacionamento ManyToMany seja persistido corretamente
 * na tabela servico_equipamento e retorne populado na resposta.
 */
@Component
public class ServicoRepositoryAdapter implements ServicoRepositoryPort {

    @Autowired private ServicoJpaRepository     jpaRepository;
    @Autowired private ServicoMapper            mapper;
    @Autowired private EquipamentoJpaRepository equipamentoJpaRepository;

    @Override
    public Servico save(Servico servico) {
        ServicoJpaEntity jpa = mapper.toJpaEntity(servico);

        // ── EQUIPAMENTOS ──────────────────────────────────────────────────────
        if (servico.getEquipamentos() != null && !servico.getEquipamentos().isEmpty()) {
            List<Long> ids = servico.getEquipamentos().stream()
                    .filter(e -> e.getId() != null)
                    .map(e -> e.getId())
                    .collect(Collectors.toList());
            jpa.setEquipamentos(equipamentoJpaRepository.findAllById(ids));
        } else {
            jpa.setEquipamentos(new ArrayList<>());
        }

        ServicoJpaEntity saved = jpaRepository.save(jpa);

        // Recarregar do banco após save para garantir equipamentos populados
        return jpaRepository.findById(saved.getId())
                .map(mapper::toDomain)
                .orElseGet(() -> mapper.toDomain(saved));
    }

    @Override
    public Optional<Servico> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Servico> findAll() {
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
    public List<Servico> findAllById(List<Long> ids) {
        return jpaRepository.findAllById(ids).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }
}