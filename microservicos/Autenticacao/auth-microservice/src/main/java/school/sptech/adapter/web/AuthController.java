package school.sptech.adapter.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.application.usecase.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final ValidateTokenUseCase validateTokenUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;

    public AuthController(LoginUseCase loginUseCase,
                          ValidateTokenUseCase validateTokenUseCase,
                          RefreshTokenUseCase refreshTokenUseCase) {
        this.loginUseCase = loginUseCase;
        this.validateTokenUseCase = validateTokenUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
    }

    // DTOs inline (ou mova para classes separadas)
    public record LoginRequest(String email, String senha) {}
    public record LoginResponse(String nome, String email, String token) {}
    public record ValidateResponse(boolean valid, String subject) {}

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest body) {
        try {
            var result = loginUseCase.execute(body.email(), body.senha());
            return ResponseEntity.ok(new LoginResponse(result.nome(), result.email(), result.token()));
        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SenhaInvalidaException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<ValidateResponse> validate(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        var result = validateTokenUseCase.execute(token);
        if (!result.valid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ValidateResponse(false, null));
        }
        return ResponseEntity.ok(new ValidateResponse(true, result.subject()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            var result = refreshTokenUseCase.execute(token);
            // Retorna novo token (nome/email podem ser buscados do subject se necessário)
            return ResponseEntity.ok(new LoginResponse(null, null, result.token()));
        } catch (TokenInvalidoException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}