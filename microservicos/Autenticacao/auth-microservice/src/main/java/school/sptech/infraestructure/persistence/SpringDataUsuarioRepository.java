package school.sptech.infraestructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Repositório Spring Data interno da camada de infra
public interface SpringDataUsuarioRepository extends JpaRepository<UsuarioJpaEntity, Long> {
    Optional<UsuarioJpaEntity> findByEmail(String email);
}