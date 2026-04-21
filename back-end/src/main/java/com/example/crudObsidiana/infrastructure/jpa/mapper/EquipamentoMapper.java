package com.example.crudObsidiana.infrastructure.jpa.mapper;

import com.example.crudObsidiana.domain.entities.Equipamento;
import com.example.crudObsidiana.infrastructure.jpa.entity.EquipamentoJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class EquipamentoMapper {

    public Equipamento toDomain(EquipamentoJpaEntity jpa) {
        if (jpa == null) return null;
        Equipamento eq = new Equipamento();
        eq.setId(jpa.getId());
        eq.setNome(jpa.getNome());
        eq.setQuantidadeTotal(jpa.getQuantidadeTotal());
        eq.setQuantidadeDisponivel(jpa.getQuantidadeDisponivel());
        eq.setCategoria(jpa.getCategoria());
        eq.setMarca(jpa.getMarca());
        eq.setNumeroSerie(jpa.getNumeroSerie());
        eq.setModelo(jpa.getModelo());
        eq.setValorPorHora(jpa.getValorPorHora());
        eq.setNomeArquivoImagem(jpa.getNomeArquivoImagem());
        eq.setTipoImagem(jpa.getTipoImagem());
        eq.setCaminhoImagem(jpa.getCaminhoImagem());
        return eq;
    }

    public EquipamentoJpaEntity toJpaEntity(Equipamento eq) {
        if (eq == null) return null;
        EquipamentoJpaEntity jpa = new EquipamentoJpaEntity();
        jpa.setId(eq.getId());
        jpa.setNome(eq.getNome());
        jpa.setQuantidadeTotal(eq.getQuantidadeTotal());
        jpa.setQuantidadeDisponivel(eq.getQuantidadeDisponivel());
        jpa.setCategoria(eq.getCategoria());
        jpa.setMarca(eq.getMarca());
        jpa.setNumeroSerie(eq.getNumeroSerie());
        jpa.setModelo(eq.getModelo());
        jpa.setValorPorHora(eq.getValorPorHora());
        jpa.setNomeArquivoImagem(eq.getNomeArquivoImagem());
        jpa.setTipoImagem(eq.getTipoImagem());
        jpa.setCaminhoImagem(eq.getCaminhoImagem());
        return jpa;
    }
}