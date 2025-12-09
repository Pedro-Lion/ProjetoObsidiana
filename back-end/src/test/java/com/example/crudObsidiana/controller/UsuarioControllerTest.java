package com.example.crudObsidiana.controller;

import com.example.crudObsidiana.dto.LoginRequestDTO;
import com.example.crudObsidiana.dto.RegisterRequestDTO;
import com.example.crudObsidiana.dto.ResponseDTO;
import com.example.crudObsidiana.model.Usuario;
import com.example.crudObsidiana.repository.UsuarioRepository;
import com.example.crudObsidiana.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class UsuarioControllerTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    private UsuarioController controller;

    @BeforeEach
    void setUp() {
        openMocks(this);
        controller = new UsuarioController(repository, passwordEncoder, tokenService);
    }

    // -----------------------------------------------------------------------
    // 1. Deve listar todos os usuários
    // -----------------------------------------------------------------------
    @Test
    void deveListarUsuarios() {
        Usuario u1 = new Usuario();
        Usuario u2 = new Usuario();

        when(repository.findAll()).thenReturn(List.of(u1, u2));

        List<Usuario> resultado = controller.listar();

        assertEquals(2, resultado.size());
        verify(repository, times(1)).findAll();
    }

    // -----------------------------------------------------------------------
    // 2. Deve cadastrar um usuário com sucesso
    // -----------------------------------------------------------------------
    @Test
    void deveCadastrarUsuario() {
        RegisterRequestDTO dto = new RegisterRequestDTO("Teste", "teste@email", "123");

        when(repository.findByEmail(dto.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.senha())).thenReturn("ENCODED");
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        ResponseEntity<ResponseDTO> response = controller.cadastrar(dto);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Teste", response.getBody().nome());
        assertEquals("teste@email", response.getBody().email());
    }

    // -----------------------------------------------------------------------
    // 3. Não deve cadastrar usuário com email já existente
    // -----------------------------------------------------------------------
    @Test
    void naoDeveCadastrarUsuarioExistente() {
        RegisterRequestDTO dto = new RegisterRequestDTO("Teste", "teste@email", "123");

        when(repository.findByEmail(dto.email())).thenReturn(Optional.of(new Usuario()));

        ResponseEntity<ResponseDTO> response = controller.cadastrar(dto);

        assertEquals(409, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(repository, never()).save(any());
    }

    // -----------------------------------------------------------------------
    // 4. Deve realizar login com sucesso
    // -----------------------------------------------------------------------
    @Test
    void deveLogarUsuario() {
        LoginRequestDTO login = new LoginRequestDTO("teste@email", "123");

        Usuario usuario = new Usuario();
        usuario.setNome("Teste");
        usuario.setEmail("teste@email");
        usuario.setSenha("HASH");

        when(repository.findByEmail(login.email())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(login.senha(), "HASH")).thenReturn(true);
        when(tokenService.generateToken(usuario)).thenReturn("TOKEN123");

        ResponseEntity<ResponseDTO> response = controller.login(login);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("TOKEN123", response.getBody().token());
    }

    // -----------------------------------------------------------------------
    // 5. Deve retornar NOT FOUND ao tentar logar com email inexistente
    // -----------------------------------------------------------------------
    @Test
    void naoDeveLogarUsuarioInexistente() {
        LoginRequestDTO login = new LoginRequestDTO("naoexiste@email", "123");

        when(repository.findByEmail(login.email())).thenReturn(Optional.empty());

        ResponseEntity<ResponseDTO> response = controller.login(login);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    // -----------------------------------------------------------------------
    // 6. Deve retornar UNAUTHORIZED ao tentar logar com senha incorreta
    // -----------------------------------------------------------------------
    @Test
    void naoDeveLogarSenhaIncorreta() {
        LoginRequestDTO login = new LoginRequestDTO("teste@email", "123");

        Usuario usuario = new Usuario();
        usuario.setSenha("HASH_CORRETO");

        when(repository.findByEmail(login.email())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(login.senha(), usuario.getSenha())).thenReturn(false);

        ResponseEntity<ResponseDTO> response = controller.login(login);

        assertEquals(401, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
}
