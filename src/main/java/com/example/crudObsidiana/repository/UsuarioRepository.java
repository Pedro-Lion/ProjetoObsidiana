package com.example.crudObsidiana.repository;

import com.example.crudObsidiana.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
  public Usuario findByEmail(String email);
}
