package school.sptech.domain.port;

import school.sptech.domain.model.Usuario;

import java.util.Optional;

// Interface que o domínio conhece — a implementação fica na infra
public interface UserRepository {
    Optional<Usuario> findByEmail(String email);
}