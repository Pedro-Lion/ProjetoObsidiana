package com.example.crudObsidiana.infrastructure.jpa.mapper;

import com.example.crudObsidiana.domain.entities.Servico;
import com.example.crudObsidiana.infrastructure.jpa.entity.ServicoJpaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ServicoMapper {

    @Autowired private EquipamentoMapper equipamentoMapper;

    // JpaEntity → Domain
    public Servico toDomain(ServicoJpaEntity jpa) {
        if (jpa == null) return null;

        Servico domain = new Servico();
        domain.setId(jpa.getId());
        domain.setNome(jpa.getNome());
        domain.setDescricao(jpa.getDescricao());
        domain.setHoras(jpa.getHoras());
        domain.setValorPorHora(jpa.getValorPorHora());

        // ✅ Carrega equipamentos — igual ao comportamento original com @ManyToMany EAGER
        if (jpa.getEquipamentos() != null) {
            domain.setEquipamentos(
                    jpa.getEquipamentos().stream()
                            .map(equipamentoMapper::toDomain)
                            .collect(Collectors.toList())
            );
        }

        return domain;
    }

    // Domain → JpaEntity
    public ServicoJpaEntity toJpaEntity(Servico domain) {
        if (domain == null) return null;

        ServicoJpaEntity jpa = new ServicoJpaEntity();
        jpa.setId(domain.getId());
        jpa.setNome(domain.getNome());
        jpa.setDescricao(domain.getDescricao());
        jpa.setHoras(domain.getHoras() != null ? domain.getHoras() : 0);
        jpa.setValorPorHora(domain.getValorPorHora());
        jpa.setEquipamentos(new ArrayList<>());

        return jpa;
    }

    // Lista
    public List<Servico> toDomainList(List<ServicoJpaEntity> jpaList) {
        if (jpaList == null) return new ArrayList<>();
        return jpaList.stream().map(this::toDomain).collect(Collectors.toList());
    }
}