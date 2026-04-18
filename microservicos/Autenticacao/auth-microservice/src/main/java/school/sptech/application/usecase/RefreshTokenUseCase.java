// application/usecase/RefreshTokenUseCase.java
package school.sptech.application.usecase;

import school.sptech.infraestructure.security.JwtTokenProvider;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenUseCase {

    private final JwtTokenProvider tokenProvider;

    public RefreshTokenUseCase(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    public record Result(String token) {}

    public Result execute(String tokenAntigo) {
        // Valida o token atual (mesmo expirado recentemente — veja nota abaixo)
        String subject = tokenProvider.validateTokenComTolerancia(tokenAntigo);
        if (subject == null) {
            throw new TokenInvalidoException();
        }
        // Gera um novo token para o mesmo usuário
        String novoToken = tokenProvider.generateToken(subject);
        return new Result(novoToken);
    }
}