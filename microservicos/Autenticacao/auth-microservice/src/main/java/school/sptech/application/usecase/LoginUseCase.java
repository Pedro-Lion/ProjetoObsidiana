package school.sptech.application.usecase;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import school.sptech.domain.model.Usuario;
import school.sptech.domain.port.UserRepository;
import school.sptech.infraestructure.security.JwtTokenProvider;

@Service
public class LoginUseCase {
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public LoginUseCase(UserRepository userRepository,
                        JwtTokenProvider tokenProvider,
                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenProvider  = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public record Result(String nome, String email, String token) {}

    public Result execute(String email, String senha) {
        Usuario usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNaoEncontradoException());

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new SenhaInvalidaException();
        }

        String token = tokenProvider.generateToken(usuario.getEmail());
        return new Result(usuario.getNome(), usuario.getEmail(), token);
    }
}
