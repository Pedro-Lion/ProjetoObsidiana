package com.example.crudObsidiana.infrastructure.jpa.mapper;

import com.example.crudObsidiana.domain.entities.UsoEquipamento;
import com.example.crudObsidiana.infrastructure.jpa.entity.UsoEquipamentoJpaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper de UsoEquipamento.
 *
 * ATENÇÃO — referência circular:
 *   UsoEquipamento → Orcamento → List<UsoEquipamento> → UsoEquipamento → ...
 *
 * Para evitar StackOverflowError, o toDomain() mapeia Equipamento
 * normalmente, mas mapeia Orcamento e Servico SOMENTE como objetos
 * com ID (sem propagar recursivamente os filhos deles).
 */
@Component
public class UsoEquipamentoMapper {

    @Autowired private EquipamentoMapper equipamentoMapper;

    // -------------------------------------------------------------------------
    // JpaEntity → Domain
    // -------------------------------------------------------------------------
    public UsoEquipamento toDomain(UsoEquipamentoJpaEntity jpa) {
        if (jpa == null) return null;

        UsoEquipamento domain = new UsoEquipamento();
        domain.setId(jpa.getId());
        domain.setQuantidadeUsada(jpa.getQuantidadeUsada());

        // Equipamento: mapeado completo (não gera ciclo)
        domain.setEquipamento(equipamentoMapper.toDomain(jpa.getEquipamento()));

        // Orcamento: apenas ID para quebrar o ciclo
        if (jpa.getOrcamento() != null) {
            com.example.crudObsidiana.domain.entities.Orcamento orcRef =
                    new com.example.crudObsidiana.domain.entities.Orcamento();
            orcRef.setId(jpa.getOrcamento().getId());
            domain.setOrcamento(orcRef);
        }

        // Servico: apenas ID para quebrar o ciclo
        if (jpa.getServico() != null) {
            com.example.crudObsidiana.domain.entities.Servico srvRef =
                    new com.example.crudObsidiana.domain.entities.Servico();
            srvRef.setId(jpa.getServico().getId());
            domain.setServico(srvRef);
        }

        return domain;
    }

    // -------------------------------------------------------------------------
    // Domain → JpaEntity
    // O adapter é responsável por setar as JpaEntities gerenciadas
    // (orcamento, servico, equipamento) após buscar do repositório.
    // Aqui apenas criamos o shell com quantidade e id.
    // -------------------------------------------------------------------------
    public UsoEquipamentoJpaEntity toJpaEntity(UsoEquipamento domain) {
        if (domain == null) return null;

        UsoEquipamentoJpaEntity jpa = new UsoEquipamentoJpaEntity();
        jpa.setId(domain.getId());
        jpa.setQuantidadeUsada(domain.getQuantidadeUsada());
        // equipamento, orcamento, servico são setados pelo adapter com entidades gerenciadas

        return jpa;
    }

    // -------------------------------------------------------------------------
    // Lista
    // -------------------------------------------------------------------------
    public List<UsoEquipamento> toDomainList(List<UsoEquipamentoJpaEntity> jpaList) {
        if (jpaList == null) return new ArrayList<>();
        return jpaList.stream().map(this::toDomain).collect(Collectors.toList());
    }
}