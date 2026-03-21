package com.example.crudObsidiana.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.crudObsidiana.model.Usuario;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Classe que gera e valida tokens JWT para autenticação de usuários na aplicação.

@Service
public class TokenService {
    // Recupera do application.properties
    @Value("${api.security.token.secret}")
    private String secret;

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    @PostConstruct
    public void init() {
        // Validação rigorosa da chave
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException(
                    "JWT Secret deve ter no mínimo 32 caracteres " +
                            "e ser configurada via variável de ambiente! " +
                            "Nunca coloque a chave no application.properties"
            );
        }
    }

    // Gera o Token
    public String generateToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("login-auth-api")
                    .withSubject(usuario.getEmail())
                    .withClaim("userId", usuario.getId())
                    .withClaim("userRole", "ROLE_USER")
                    .withIssuedAt(Instant.now())

                    .withExpiresAt(this.generateExpirationDate())
                    .sign(algorithm);

            logger.info("Token gerado para usuário: {}", usuario.getEmail());
            return token;

        } catch (JWTCreationException exception){
            logger.error("Erro ao gerar token: {}", exception.getMessage());
            throw new RuntimeException("Erro na autenticação");
        }
    }

    // Valida o token
    public String validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
//            String subject = JWT.require(algorithm)
            return JWT.require(algorithm)
                    .withIssuer("login-auth-api")
                    .build()
                    .verify(token)
                    .getSubject();

//            securityLogger.info("Token válido para usuário: {}", subject);
//                return subject;

        } catch (JWTVerificationException exception) {
//            securityLogger.warn("Token inválido: {} - Motivo: {}",
//                    token.substring(0,Math.min(20, token.length())) + "...",
//                    exception.getMessage());
            return null;
        }
    }

    // Duração do token (2 horas)
    private Instant generateExpirationDate(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
