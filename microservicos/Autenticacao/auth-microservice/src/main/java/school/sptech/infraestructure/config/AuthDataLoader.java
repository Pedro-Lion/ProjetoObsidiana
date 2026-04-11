package school.sptech.infraestructure.config;

import org.springframework.beans.factory.annotation.Value;
import school.sptech.infraestructure.persistence.UsuarioJpaEntity;
import school.sptech.infraestructure.persistence.SpringDataUsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthDataLoader implements CommandLineRunner {

    // variável para determinar se está usando H2 ou MYSQL
    @Value("${spring.datasource.url}")
    private String dataBaseUrl;

    private final SpringDataUsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AuthDataLoader(SpringDataUsuarioRepository repository,
                          PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (dataBaseUrl.contains("mysql")) {
            return;
        }

        if (repository.count() == 0) {
            UsuarioJpaEntity admin = new UsuarioJpaEntity();
            admin.setNome("Administrador");
            admin.setEmail("admin@obsidiana.com");
            admin.setSenha(passwordEncoder.encode("123456"));
            repository.save(admin);
            System.out.println("✔ [Auth-MS] Usuário admin criado.");
        }
    }
}