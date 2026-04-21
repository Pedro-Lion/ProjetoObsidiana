package com.example.crudObsidiana.infrastructure.security;

import com.example.crudObsidiana.domain.entities.Usuario;
import com.example.crudObsidiana.domain.ports.UsuarioRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Busca usuário pelo e-mail para autenticação no Spring Security.
 *
 * Mudanças em relação ao original:
 *  - Import de Usuario: domain.entities.Usuario
 *  - Injeção de UsuarioRepositoryPort (não UsuarioRepository JPA)
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepositoryPort usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(),
                usuario.getSenha(),
                new ArrayList<>()
        );
    }
}