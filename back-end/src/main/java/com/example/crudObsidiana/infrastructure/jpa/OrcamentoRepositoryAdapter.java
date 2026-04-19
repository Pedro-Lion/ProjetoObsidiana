package com.example.crudObsidiana.infrastructure.jpa;

import com.example.crudObsidiana.domain.entities.Orcamento;
import com.example.crudObsidiana.domain.ports.OrcamentoRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter de Orçamento — único ponto onde domínio ↔ JPA se tocam.
 *
 * CORREÇÃO PRINCIPAL no método save():
 * Após criar a JpaEntity via mapper, buscamos as entidades gerenciadas
 * pelo JPA para cada relacionamento ManyToMany (servicos, equipamentos,
 * profissionais) e as associamos antes do save(). Sem isso, o Hibernate
 * não persiste os registros nas tabelas de junção e os campos voltam vazios.
 */
@Component
public class OrcamentoRepositoryAdapter implements OrcamentoRepositoryPort {

    @Autowired private OrcamentoJpaRepository    jpaRepository;
    @Autowired private OrcamentoMapper           mapper;

    // Repositórios auxiliares para buscar JpaEntities gerenciadas
    @Autowired private ServicoJpaRepository      servicoJpaRepository;
    @Autowired private EquipamentoJpaRepository  equipamentoJpaRepository;
    @Autowired private ProfissionalJpaRepository profissionalJpaRepository;

    @Override
    public Orcamento save(Orcamento orcamento) {
        OrcamentoJpaEntity jpa = mapper.toJpaEntity(orcamento);

        // ── SERVIÇOS ──────────────────────────────────────────────────────────
        if (orcamento.getServicos() != null && !orcamento.getServicos().isEmpty()) {
            List<Long> ids = orcamento.getServicos().stream()
                    .filter(s -> s.getId() != null)
                    .map(s -> s.getId())
                    .collect(Collectors.toList());
            jpa.setServicos(servicoJpaRepository.findAllById(ids));
        } else {
            jpa.setServicos(new ArrayList<>());
        }

        // ── EQUIPAMENTOS (many-to-many direto do orçamento) ───────────────────
        if (orcamento.getEquipamentos() != null && !orcamento.getEquipamentos().isEmpty()) {
            List<Long> ids = orcamento.getEquipamentos().stream()
                    .filter(e -> e.getId() != null)
                    .map(e -> e.getId())
                    .collect(Collectors.toList());
            jpa.setEquipamentos(equipamentoJpaRepository.findAllById(ids));
        } else {
            jpa.setEquipamentos(new ArrayList<>());
        }

        // ── PROFISSIONAIS ─────────────────────────────────────────────────────
        if (orcamento.getProfissionais() != null && !orcamento.getProfissionais().isEmpty()) {
            List<Long> ids = orcamento.getProfissionais().stream()
                    .filter(p -> p.getId() != null)
                    .map(p -> p.getId())
                    .collect(Collectors.toList());
            jpa.setProfissionais(profissionalJpaRepository.findAllById(ids));
        } else {
            jpa.setProfissionais(new ArrayList<>());
        }

        // usosEquipamentos é gerenciado pelo UsoEquipamentoRepositoryAdapter
        // separadamente — não setar aqui para evitar conflito com orphanRemoval
        jpa.setUsosEquipamentos(new ArrayList<>());

        OrcamentoJpaEntity saved = jpaRepository.save(jpa);

        // Após salvar, recarregar do banco para garantir todos os relacionamentos
        // populados no retorno (evita listas vazias por contexto JPA sujo)
        return jpaRepository.findById(saved.getId())
                .map(mapper::toDomain)
                .orElseGet(() -> mapper.toDomain(saved));
    }

    @Override
    public Optional<Orcamento> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Orcamento> findAll() {
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
    public Optional<Orcamento> findByIdCalendar(String idCalendar) {
        return jpaRepository.findByIdCalendar(idCalendar).map(mapper::toDomain);
    }

    @Override
    public Integer countByStatus(String status) {
        return jpaRepository.countByStatus(status);
    }
}