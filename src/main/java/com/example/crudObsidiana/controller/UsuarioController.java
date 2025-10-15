package com.example.crudObsidiana.controller;

import com.example.crudObsidiana.model.Usuario;
import com.example.crudObsidiana.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Usuários", description = "Operações relacionadas aos usuários")
@RestController
@RequestMapping("/usuarios")
//@CrossOrigin(origins = "http://localhost:5173")
public class UsuarioController {
  private final UsuarioRepository repository;

  public UsuarioController(UsuarioRepository repository) {
    this.repository = repository;
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
  @ApiResponse(responseCode = "200", description = "Usuário cadastrado com sucesso",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)))
  @PostMapping("/cadastro")
  public ResponseEntity<Usuario> cadastro(@RequestBody Usuario usuario) {
    Usuario novoUsuario = repository.save(usuario);
    return ResponseEntity.ok(novoUsuario);
  }


  @Operation(summary = "Login de um usuário")
  @ApiResponse(responseCode = "200", description = "Usuário logado com sucesso",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)))
  @PostMapping("/login")
  public ResponseEntity<Usuario> login(@RequestBody Map<String, String> credenciais) {
    Usuario usuario = repository.findByEmail(credenciais.get("email"));

    if (usuario == null) {
      return ResponseEntity.status(404).build();
    }

    if (!usuario.getSenha().equals(credenciais.get("senha"))) {
      return ResponseEntity.status(401).build();
    }

    return ResponseEntity.ok(usuario);
  }



}
