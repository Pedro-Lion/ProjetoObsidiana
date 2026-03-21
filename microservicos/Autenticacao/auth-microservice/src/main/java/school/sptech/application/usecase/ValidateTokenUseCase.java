package school.sptech.application.usecase;

import org.springframework.stereotype.Service;
import school.sptech.infraestructure.security.JwtTokenProvider;

@Service
public class ValidateTokenUseCase {
    private final JwtTokenProvider tokenProvider;

    public ValidateTokenUseCase(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    public record Result(boolean valid, String subject) {}

    public Result execute(String token) {
        String subject = tokenProvider.validateToken(token);
        return new Result(subject != null, subject);
    }
}
