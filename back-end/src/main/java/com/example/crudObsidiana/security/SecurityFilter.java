package com.example.crudObsidiana.security;

import com.example.crudObsidiana.model.Usuario;
import com.example.crudObsidiana.repository.UsuarioRepository;
import com.example.crudObsidiana.service.AuditService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

// Filtro que intercepta requisições, valida o token JWT e autentica o usuário no contexto de segurança da aplicação.

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;

    @Autowired
    UsuarioRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        var login = tokenService.validateToken(token);

        String path = request.getRequestURI();
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        // Pular endpoints públicos (mas logar mesmo assim?)
        if (path.equals("/usuario/login") || path.equals("/usuario/cadastrar")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (token == null) {
            AuditService.logAcessoNegado(null, path, ip, "Token não fornecido");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token não fornecido");
            return;
        }

        if (login != null){
            AuditService.logAcao(login, "ACESSAR", path, true);
            Usuario usuario = userRepository.findByEmail(login).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
            var authentication = new UsernamePasswordAuthenticationToken(usuario, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }else {
            AuditService.logAcessoNegado(null, path, ip, "Token inválido");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token inválido");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}
