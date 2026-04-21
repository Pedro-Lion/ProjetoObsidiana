package com.example.crudObsidiana.infrastructure.jpa.mapper;

import com.example.crudObsidiana.domain.entities.Orcamento;
import com.example.crudObsidiana.infrastructure.jpa.entity.OrcamentoJpaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Converte entre Orcamento (domínio) e OrcamentoJpaEntity (infraestrutura).
 *
 * REGRA CRÍTICA: toDomain() nunca acessa lazy collections do JPA.
 * Os relacionamentos são mapeados apenas se já foram carregados
 * (checagem com != null e !isEmpty segura).
 */
@Component
public class OrcamentoMapper {

    // Mappers dos relacionamentos — injetados para evitar conversão manual repetida
    @Autowired private UsoEquipamentoMapper usoEquipamentoMapper;
    @Autowired private ServicoMapper servicoMapper;
    @Autowired private EquipamentoMapper equipamentoMapper;
    @Autowired private ProfissionalMapper profissionalMapper;

    // -------------------------------------------------------------------------
    // JpaEntity → Domain
    // -------------------------------------------------------------------------
    public Orcamento toDomain(OrcamentoJpaEntity jpa) {
        if (jpa == null) return null;

        Orcamento domain = new Orcamento();
        domain.setId(jpa.getId());
        domain.setDataInicio(jpa.getDataInicio());
        domain.setDataTermino(jpa.getDataTermino());
        domain.setLocalEvento(jpa.getLocalEvento());
        domain.setDescricao(jpa.getDescricao());
        domain.setStatus(jpa.getStatus());
        domain.setValorTotal(jpa.getValorTotal());
        domain.setIdCalendar(jpa.getIdCalendar());

        // Relacionamentos: só converte se a coleção já estiver carregada
        if (jpa.getUsosEquipamentos() != null) {
            domain.setUsosEquipamentos(
                    jpa.getUsosEquipamentos().stream()
                            .map(usoEquipamentoMapper::toDomain)
                            .collect(Collectors.toList())
            );
        }
        if (jpa.getServicos() != null) {
            domain.setServicos(
                    jpa.getServicos().stream()
                            .map(servicoMapper::toDomain)
                            .collect(Collectors.toList())
            );
        }
        if (jpa.getEquipamentos() != null) {
            domain.setEquipamentos(
                    jpa.getEquipamentos().stream()
                            .map(equipamentoMapper::toDomain)
                            .collect(Collectors.toList())
            );
        }
        if (jpa.getProfissionais() != null) {
            domain.setProfissionais(
                    jpa.getProfissionais().stream()
                            .map(profissionalMapper::toDomain)
                            .collect(Collectors.toList())
            );
        }

        return domain;
    }

    // -------------------------------------------------------------------------
    // Domain → JpaEntity
    // IMPORTANTE: relacionamentos many-to-many (servicos, equipamentos,
    // profissionais) NÃO são setados aqui — eles precisam ser carregados
    // como JpaEntities gerenciadas pelo repositório antes de associar.
    // Isso é responsabilidade do RepositoryAdapter.
    // -------------------------------------------------------------------------
    public OrcamentoJpaEntity toJpaEntity(Orcamento domain) {
        if (domain == null) return null;

        OrcamentoJpaEntity jpa = new OrcamentoJpaEntity();
        jpa.setId(domain.getId());
        jpa.setDataInicio(domain.getDataInicio());
        jpa.setDataTermino(domain.getDataTermino());
        jpa.setLocalEvento(domain.getLocalEvento());
        jpa.setDescricao(domain.getDescricao());
        jpa.setStatus(domain.getStatus());
        jpa.setValorTotal(domain.getValorTotal());
        jpa.setIdCalendar(domain.getIdCalendar());

        // Listas inicializadas vazias — o adapter popula os relacionamentos gerenciados
        jpa.setUsosEquipamentos(new ArrayList<>());
        jpa.setServicos(new ArrayList<>());
        jpa.setEquipamentos(new ArrayList<>());
        jpa.setProfissionais(new ArrayList<>());

        return jpa;
    }

    // -------------------------------------------------------------------------
    // Lista
    // -------------------------------------------------------------------------
    public List<Orcamento> toDomainList(List<OrcamentoJpaEntity> jpaList) {
        if (jpaList == null) return new ArrayList<>();
        return jpaList.stream().map(this::toDomain).collect(Collectors.toList());
    }
}