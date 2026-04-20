package com.example.crudObsidiana.infrastructure.jpa.mapper;

import com.example.crudObsidiana.domain.entities.Usuario;
import com.example.crudObsidiana.infrastructure.jpa.entity.UsuarioJpaEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UsuarioMapper {

    // -------------------------------------------------------------------------
    // JpaEntity → Domain
    // -------------------------------------------------------------------------
    public Usuario toDomain(UsuarioJpaEntity jpa) {
        if (jpa == null) return null;

        Usuario domain = new Usuario();
        domain.setId(jpa.getId());
        domain.setNome(jpa.getNome());
        domain.setEmail(jpa.getEmail());
        domain.setSenha(jpa.getSenha());
        domain.setNomeArquivoImagem(jpa.getNomeArquivoImagem());
        domain.setTipoImagem(jpa.getTipoImagem());
        domain.setCaminhoImagem(jpa.getCaminhoImagem());

        return domain;
    }

    // -------------------------------------------------------------------------
    // Domain → JpaEntity
    // -------------------------------------------------------------------------
    public UsuarioJpaEntity toJpaEntity(Usuario domain) {
        if (domain == null) return null;

        UsuarioJpaEntity jpa = new UsuarioJpaEntity();
        jpa.setId(domain.getId());
        jpa.setNome(domain.getNome());
        jpa.setEmail(domain.getEmail());
        jpa.setSenha(domain.getSenha());
        jpa.setNomeArquivoImagem(domain.getNomeArquivoImagem());
        jpa.setTipoImagem(domain.getTipoImagem());
        jpa.setCaminhoImagem(domain.getCaminhoImagem());

        return jpa;
    }

    // -------------------------------------------------------------------------
    // Lista
    // -------------------------------------------------------------------------
    public List<Usuario> toDomainList(List<UsuarioJpaEntity> jpaList) {
        if (jpaList == null) return new ArrayList<>();
        return jpaList.stream().map(this::toDomain).collect(Collectors.toList());
    }
}