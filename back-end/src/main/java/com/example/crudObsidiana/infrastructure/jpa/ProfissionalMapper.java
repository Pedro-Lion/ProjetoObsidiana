package com.example.crudObsidiana.infrastructure.jpa;

import com.example.crudObsidiana.domain.entities.Profissional;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProfissionalMapper {

    // -------------------------------------------------------------------------
    // JpaEntity → Domain
    // -------------------------------------------------------------------------
    public Profissional toDomain(ProfissionalJpaEntity jpa) {
        if (jpa == null) return null;

        Profissional domain = new Profissional();
        domain.setId(jpa.getId());
        domain.setNome(jpa.getNome());
        domain.setDisponibilidade(jpa.getDisponibilidade());
        domain.setContato(jpa.getContato());
        // Orcamentos NÃO são mapeados aqui para evitar referência cíclica
        // (Orcamento → Profissional → Orcamento → ...)

        return domain;
    }

    // -------------------------------------------------------------------------
    // Domain → JpaEntity
    // -------------------------------------------------------------------------
    public ProfissionalJpaEntity toJpaEntity(Profissional domain) {
        if (domain == null) return null;

        ProfissionalJpaEntity jpa = new ProfissionalJpaEntity();
        jpa.setId(domain.getId());
        jpa.setNome(domain.getNome());
        jpa.setDisponibilidade(domain.getDisponibilidade());
        jpa.setContato(domain.getContato());
        jpa.setOrcamentos(new ArrayList<>());

        return jpa;
    }

    // -------------------------------------------------------------------------
    // Lista
    // -------------------------------------------------------------------------
    public List<Profissional> toDomainList(List<ProfissionalJpaEntity> jpaList) {
        if (jpaList == null) return new ArrayList<>();
        return jpaList.stream().map(this::toDomain).collect(Collectors.toList());
    }
}