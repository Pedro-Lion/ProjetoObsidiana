package school.sptech.infraestructure.persistence;

import org.springframework.stereotype.Repository;
import school.sptech.domain.model.Usuario;
import school.sptech.domain.port.UserRepository;

import java.util.Optional;

@Repository
public class JpaUserRepository implements UserRepository {

    private final SpringDataUsuarioRepository springRepo;

    public JpaUserRepository(SpringDataUsuarioRepository springRepo) {
        this.springRepo = springRepo;
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return springRepo.findByEmail(email)
                .map(e -> new Usuario(e.getId(), e.getNome(), e.getEmail(), e.getSenha()));
    }
}
