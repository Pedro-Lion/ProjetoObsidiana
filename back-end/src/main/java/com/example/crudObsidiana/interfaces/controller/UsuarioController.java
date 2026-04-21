package com.example.crudObsidiana.interfaces.controller;

import com.example.crudObsidiana.domain.entities.Usuario;
import com.example.crudObsidiana.domain.ports.UsuarioRepositoryPort;
import com.example.crudObsidiana.infrastructure.file.FileStorageService;
import com.example.crudObsidiana.infrastructure.security.TokenService;
import com.example.crudObsidiana.interfaces.dto.LoginRequestDTO;
import com.example.crudObsidiana.interfaces.dto.RegisterRequestDTO;
import com.example.crudObsidiana.interfaces.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Tag(name = "Usuários", description = "Operações relacionadas aos usuários")
@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

  private final UsuarioRepositoryPort usuarioRepository;
  private final PasswordEncoder       passwordEncoder;
  private final TokenService          tokenService;
  private final FileStorageService    fileStorageService;

  @Autowired
  public UsuarioController(UsuarioRepositoryPort usuarioRepository,
                           PasswordEncoder passwordEncoder,
                           TokenService tokenService,
                           FileStorageService fileStorageService) {
    this.usuarioRepository  = usuarioRepository;
    this.passwordEncoder    = passwordEncoder;
    this.tokenService       = tokenService;
    this.fileStorageService = fileStorageService;
  }

  @GetMapping
  @Operation(summary = "Lista todos os usuários")
  @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso",
          content = @Content(mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = Usuario.class))))
  public List<Usuario> listar() {
    return usuarioRepository.findAll();
  }

  @PostMapping("/cadastrar")
  @Operation(summary = "Cadastra um novo usuário")
  @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso")
  public ResponseEntity<ResponseDTO> cadastrar(@RequestBody RegisterRequestDTO body) {
    if (usuarioRepository.findByEmail(body.email()).isPresent()) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
    Usuario novo = new Usuario();
    novo.setNome(body.nome());
    novo.setEmail(body.email());
    novo.setSenha(passwordEncoder.encode(body.senha()));
    usuarioRepository.save(novo);
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ResponseDTO(novo.getNome(), novo.getEmail(), null));
  }

  @PostMapping("/login")
  @Operation(summary = "Login de um usuário")
  @ApiResponse(responseCode = "200", description = "Login realizado com sucesso")
  public ResponseEntity<ResponseDTO> login(@RequestBody LoginRequestDTO body) {
    Usuario usuario = usuarioRepository.findByEmail(body.email()).orElse(null);
    if (usuario == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    if (!passwordEncoder.matches(body.senha(), usuario.getSenha())) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    String token = tokenService.generateToken(usuario);
    return ResponseEntity.ok(new ResponseDTO(usuario.getNome(), usuario.getEmail(), token));
  }

  @PostMapping("/{id}/imagem")
  @Operation(summary = "Upload de imagem para usuário")
  public ResponseEntity<?> uploadImagem(@PathVariable Long id,
                                        @RequestParam("arquivo") MultipartFile arquivo) {
    try {
      if (!usuarioRepository.existsById(id)) return ResponseEntity.notFound().build();
      Path caminho    = fileStorageService.salvarArquivo(arquivo);
      Usuario usuario = usuarioRepository.findById(id).orElseThrow();
      usuario.setNomeArquivoImagem(caminho.getFileName().toString());
      usuario.setTipoImagem(arquivo.getContentType());
      usuario.setCaminhoImagem(caminho.toString());
      return ResponseEntity.ok(usuarioRepository.save(usuario));
    } catch (IOException e) {
      return ResponseEntity.internalServerError().body("Erro ao salvar arquivo: " + e.getMessage());
    }
  }

  @GetMapping("/{id}/imagem")
  @Operation(summary = "Download/visualizar imagem do usuário")
  public ResponseEntity<byte[]> baixarImagem(@PathVariable Long id) {
    Optional<Usuario> opt = usuarioRepository.findById(id);
    if (opt.isEmpty()) return ResponseEntity.notFound().build();
    Usuario usr    = opt.get();
    String nome    = usr.getNomeArquivoImagem();
    if (nome == null || nome.isEmpty()) return ResponseEntity.notFound().build();
    try {
      byte[] dados = fileStorageService.lerArquivo(nome);
      String tipo  = (usr.getTipoImagem() == null || usr.getTipoImagem().isEmpty())
              ? MediaType.APPLICATION_OCTET_STREAM_VALUE : usr.getTipoImagem();
      return ResponseEntity.ok()
              .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nome + "\"")
              .contentType(MediaType.parseMediaType(tipo))
              .body(dados);
    } catch (IOException e) {
      return ResponseEntity.notFound().build();
    }
  }
}