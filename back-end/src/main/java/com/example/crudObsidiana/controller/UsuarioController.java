package com.example.crudObsidiana.controller;

import com.example.crudObsidiana.dto.LoginRequestDTO;
import com.example.crudObsidiana.dto.ResponseDTO;
import com.example.crudObsidiana.model.Usuario;
import com.example.crudObsidiana.repository.UsuarioRepository;
import com.example.crudObsidiana.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Usuários", description = "Operações relacionadas aos usuários")
@RestController
@RequestMapping("/usuarios")
//@CrossOrigin(origins = "http://localhost:5173")
public class UsuarioController {
  private final UsuarioRepository repository;
  private final PasswordEncoder passwordEncoder;
  private final TokenService tokenService;

  public UsuarioController(
    UsuarioRepository repository, PasswordEncoder passwordEncoder, TokenService tokenService
  ) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
    this.tokenService = tokenService;
  }

  @Operation(summary = "Lista todos os usuários")
  @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso",
          content = @Content(mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = Usuario.class))))
  @GetMapping
  public List<Usuario> listar() {
    return repository.findAll();
  }

  @Operation(summary = "Cadastra um novo usuário")
  @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)))
  @PostMapping("/cadastro")
  public ResponseEntity<ResponseDTO> cadastro(@RequestBody Usuario body) {
    if (repository.findByEmail(body.getEmail()) != null) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    Usuario novoUsuario = new Usuario();
    novoUsuario.setNome(body.getNome());
    novoUsuario.setEmail(body.getEmail());
    novoUsuario.setSenha(passwordEncoder.encode(body.getSenha()));

    repository.save(novoUsuario);

    ResponseDTO response =
      new ResponseDTO(novoUsuario.getNome(), novoUsuario.getEmail(), null);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }


  @Operation(summary = "Login de um usuário")
  @ApiResponse(responseCode = "200", description = "Usuário logado com sucesso",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)))
  @PostMapping("/login")
  public ResponseEntity<ResponseDTO> login(@RequestBody LoginRequestDTO body) {
    Usuario usuario = repository.findByEmail(body.email());

    if (usuario == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    if (!passwordEncoder.matches(body.senha(), usuario.getSenha())) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String token = tokenService.generateToken(usuario);
    ResponseDTO response =
      new ResponseDTO(usuario.getNome(), usuario.getEmail(), token);
    return ResponseEntity.ok(response);
  }



}
