package com.example.crudObsidiana.security;

import com.example.crudObsidiana.model.Usuario;
import com.example.crudObsidiana.repository.UsuarioRepository;
import com.example.crudObsidiana.service.AuditService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

// Filtro que intercepta requisições, valida o token JWT e autentica o usuário no contexto de segurança da aplicação.

@Component
public class SecurityFilter extends OncePerRequestFilter {

    // Injete via @Value no application.properties
    @Value("${auth.microservice.url:http://localhost:8081}")
    private String authServiceUrl;

    private final RestTemplate restTemplate;
    private final UsuarioRepository userRepository;

    public SecurityFilter(UsuarioRepository userRepository) {
        this.userRepository = userRepository;
        this.restTemplate = new RestTemplate();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path  = request.getRequestURI();
        String ip    = request.getRemoteAddr();

        if (path.equals("/usuario/login") || path.equals("/usuario/cadastrar")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = recoverToken(request);
        if (token == null) {
            AuditService.logAcessoNegado(null, path, ip, "Token não fornecido");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token não fornecido");
            return;
        }

        // ← única mudança: delega a validação ao microserviço
        String login = validateWithAuthService(token);

        if (login != null) {
            AuditService.logAcao(login, "ACESSAR", path, true);
            Usuario usuario = userRepository.findByEmail(login)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            var authorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_USER"));
            var authentication = new UsernamePasswordAuthenticationToken(
                    usuario, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            AuditService.logAcessoNegado(null, path, ip, "Token inválido");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token inválido");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String validateWithAuthService(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<ValidateResponse> resp = restTemplate.exchange(
                    authServiceUrl + "/auth/validate",
                    HttpMethod.GET, entity, ValidateResponse.class);

            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null
                    && resp.getBody().valid()) {
                return resp.getBody().subject();
            }
        } catch (Exception e) {
            // serviço indisponível → nega acesso
        }
        return null;
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }

    record ValidateResponse(boolean valid, String subject) {}
}
