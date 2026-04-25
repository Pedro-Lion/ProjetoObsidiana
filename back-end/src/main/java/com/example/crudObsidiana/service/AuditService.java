package com.example.crudObsidiana.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
public class AuditService {

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");
    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY");

    public static void logAcao(String usuario, String acao, String recurso, boolean sucesso){
        auditLogger.info("AUDIT|{}|{}|{}|{}|{}",
                LocalDateTime.now(),
                usuario != null ? usuario : "ANONIMO",
                acao,
                recurso,
                sucesso ? "SUCESSO" : "FALHA"
                );
    }

    public static void logAcessoNegado(String usuario, String recurso,String ip, String motivo){
        securityLogger.warn("SEGURANCA|{}|{}|ACESSO_NEGADO|{}|IP:{}|MOTIVO:{}",
                LocalDateTime.now(),
                usuario != null ? usuario : "ANONIMO",
                recurso,
                ip,
                motivo);
    }

    public static void logLogin(String email, boolean sucesso, String ip, String userAgent) {
        securityLogger.info("LOGIN|{}|{}|{}|IP:{}|AGENTE:{}",
                LocalDateTime.now(),
                email,
                sucesso ? "SUCESSO" : "FALHA",
                ip,
                userAgent
        );
    }

    public static void logAlteracaoDados(String usuario, String entidade, Long id, String campos) {
        auditLogger.info("ALTERACAO|{}|{}|{}|ID:{}|CAMPOS:{}",
                LocalDateTime.now(),
                usuario,
                entidade,
                id,
                campos
        );
    }
}




