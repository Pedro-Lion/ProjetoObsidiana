package school.sptech.infraestructure.security;

import org.springframework.stereotype.Component;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class JwtTokenProvider {

    // Secret ativo (rotacionado periodicamente)
    private volatile String currentSecret = generateNewSecret();

    // Secrets anteriores ainda aceitos (tokens em uso que ainda não expiraram)
    private final CopyOnWriteArrayList<String> previousSecrets = new CopyOnWriteArrayList<>();

    private static final int MAX_PREVIOUS_SECRETS = 3;
    private static final String ISSUER = "auth-microservice";

    public String generateToken(String subject) {
        Algorithm algorithm = Algorithm.HMAC256(currentSecret);
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(subject)
                .withExpiresAt(expirationDate())
                .sign(algorithm);
    }

    // Tenta validar com o secret atual e, se falhar, com os anteriores
    public String validateToken(String token) {
        // Tenta o secret atual primeiro
        String result = tryValidate(token, currentSecret);
        if (result != null) return result;

        // Tenta secrets anteriores (tokens emitidos antes da última rotação)
        for (String oldSecret : previousSecrets) {
            result = tryValidate(token, oldSecret);
            if (result != null) return result;
        }
        return null;
    }

    // Chamado pelo KeyRotationScheduler
    public synchronized void rotateSecret() {
        previousSecrets.add(0, currentSecret);
        if (previousSecrets.size() > MAX_PREVIOUS_SECRETS) {
            previousSecrets.remove(previousSecrets.size() - 1);
        }
        currentSecret = generateNewSecret();
    }

    private String tryValidate(String token, String secret) {
        try {
            return JWT.require(Algorithm.HMAC256(secret))
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    private Instant expirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

    private static String generateNewSecret() {
        // Gera 64 bytes aleatórios em hex
        byte[] bytes = new byte[64];
        new java.security.SecureRandom().nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public String validateTokenComTolerancia(String token) {
        // Tenta validar normalmente primeiro
        String subject = validateToken(token);
        if (subject != null) return subject;

        // Se falhou, tenta aceitar tokens expirados há menos de 30 minutos
        for (String secret : getAllSecrets()) {
            try {
                // Decodifica sem verificar expiração para checar se é estruturalmente válido
                com.auth0.jwt.interfaces.DecodedJWT decoded =
                        JWT.decode(token);

                // Verifica se expirou há menos de 30 minutos
                java.util.Date expiration = decoded.getExpiresAt();
                if (expiration != null) {
                    long minutosExpirado = (System.currentTimeMillis()
                            - expiration.getTime()) / 60000;
                    if (minutosExpirado <= 30) {
                        // Verifica assinatura manualmente
                        Algorithm algorithm = Algorithm.HMAC256(secret);
                        JWT.require(algorithm)
                                .withIssuer(ISSUER)
                                .acceptExpiresAt(30 * 60) // 30 min de tolerância
                                .build()
                                .verify(token);
                        return decoded.getSubject();
                    }
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    // Método auxiliar para iterar todos os secrets
    private java.util.List<String> getAllSecrets() {
        java.util.List<String> all = new java.util.ArrayList<>();
        all.add(currentSecret);
        all.addAll(previousSecrets);
        return all;
    }

}
