package com.example.crudObsidiana.domain.ports;

import com.example.crudObsidiana.domain.entities.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Port de persistência para Usuario.
 *
 * Métodos mapeados dos originais:
 *  - JpaRepository padrão: save, findById, findAll, deleteById, existsById
 *  - Customizados: findByEmail
 *
 * ATENÇÃO: O CustomUserDetailsService (infrastructure/security) ainda
 * pode continuar usando o UsuarioJpaRepository diretamente via Spring Security,
 * pois ele é um detalhe de infraestrutura — não precisa passar pela port.
 * Mas se preferir uniformidade, pode usar esta port lá também.
 */
public interface UsuarioRepositoryPort {

    // --- CRUD base ---
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(Long id);
    List<Usuario> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);

    // --- Queries customizadas (vindas do UsuarioRepository original) ---

    /**
     * Busca um usuário pelo e-mail.
     * Usado pelo CustomUserDetailsService para autenticação JWT.
     */
    Optional<Usuario> findByEmail(String email);
}