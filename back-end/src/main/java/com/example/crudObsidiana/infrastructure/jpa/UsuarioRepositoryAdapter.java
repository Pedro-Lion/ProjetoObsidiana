package com.example.crudObsidiana.infrastructure.jpa;

import com.example.crudObsidiana.domain.entities.Usuario;
import com.example.crudObsidiana.domain.ports.UsuarioRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    @Autowired private UsuarioJpaRepository jpaRepository;
    @Autowired private UsuarioMapper        mapper;

    @Override
    public Usuario save(Usuario usuario) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(usuario)));
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Usuario> findAll() {
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
    public Optional<Usuario> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(mapper::toDomain);
    }
}