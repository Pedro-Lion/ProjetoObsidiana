package school.sptech.infraestructure.security;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class KeyRotationScheduler {

    private static final Logger log = LoggerFactory.getLogger(KeyRotationScheduler.class);
    private final JwtTokenProvider tokenProvider;

    public KeyRotationScheduler(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    // Rotaciona a cada 24 horas — ajuste o cron conforme necessário
    // "0 0 */24 * * *" = a cada 24h
    // "0 0 2 * * *"    = todo dia às 02:00
    @Scheduled(cron = "0 0 2 * * *")
    public void rotateKey() {
        log.info("Rotacionando JWT secret...");
        tokenProvider.rotateSecret();
        log.info("JWT secret rotacionado com sucesso.");
    }
}