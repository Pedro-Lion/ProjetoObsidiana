package com.example.crudObsidiana.controller;

import com.example.crudObsidiana.dto.LoginRequestDTO;
import com.example.crudObsidiana.dto.RegisterRequestDTO;
import com.example.crudObsidiana.dto.ResponseDTO;
import com.example.crudObsidiana.model.Usuario;
import com.example.crudObsidiana.repository.UsuarioRepository;
import com.example.crudObsidiana.security.TokenService;
import com.example.crudObsidiana.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;
import java.io.IOException;

import java.util.List;

@Tag(name = "Usuários", description = "Operações relacionadas aos usuários")
@RestController
@RequestMapping("/usuario")
public class UsuarioController {
  private final UsuarioRepository repository;
  private final PasswordEncoder passwordEncoder;
  private final TokenService tokenService;
  @Autowired
  private FileStorageService fileStorageService;

//  CONSTRUCTORS
  public UsuarioController(
    UsuarioRepository repository, PasswordEncoder passwordEncoder, TokenService tokenService
  ) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
    this.tokenService = tokenService;
  }

//  METHODS
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
  @PostMapping("/cadastrar")
  public ResponseEntity<ResponseDTO> cadastrar(@RequestBody RegisterRequestDTO body) {
    if (repository.findByEmail(body.email()).orElse(null) != null) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    Usuario novoUsuario = new Usuario();
    novoUsuario.setNome(body.nome());
    novoUsuario.setEmail(body.email());
    novoUsuario.setSenha(passwordEncoder.encode(body.senha()));

    repository.save(novoUsuario);

    ResponseDTO response =
      new ResponseDTO(novoUsuario.getNome(), novoUsuario.getEmail(), null);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Value("${auth.microservice.url:http://localhost:8081}")
  private String authServiceUrl;

  private final RestTemplate restTemplate = new RestTemplate();
  @PostMapping("/login")
  public ResponseEntity<ResponseDTO> login(@RequestBody LoginRequestDTO body) {
    try {
      HttpEntity<LoginRequestDTO> entity = new HttpEntity<>(body);
      ResponseEntity<ResponseDTO> resp = restTemplate.postForEntity(
              authServiceUrl + "/auth/login", entity, ResponseDTO.class);
      return ResponseEntity.status(resp.getStatusCode()).body(resp.getBody());
    } catch (HttpClientErrorException e) {
      return ResponseEntity.status(e.getStatusCode()).build();
    }
  }

  @PostMapping("/{id}/imagem")
  public ResponseEntity<?> uploadImagemUsuario(@PathVariable Long id,
                                               @RequestParam("arquivo") MultipartFile arquivo) {
    try {
      if (!repository.existsById(id)) {
        return ResponseEntity.notFound().build();
      }

      Path caminho = fileStorageService.salvarArquivo(arquivo);

      Usuario usuario = repository.findById(id).get();
      usuario.setNomeArquivoImagem(caminho.getFileName().toString());
      usuario.setTipoImagem(arquivo.getContentType());
      usuario.setCaminhoImagem(caminho.toString());

      repository.save(usuario);
      return ResponseEntity.ok(usuario);
    } catch (IOException e) {
      return ResponseEntity.internalServerError().body("Erro ao salvar arquivo: " + e.getMessage());
    }
  }

  @GetMapping("/{id}/imagem")
  public ResponseEntity<byte[]> baixarImagemUsuario(@PathVariable Long id) {
    java.util.Optional<Usuario> opt = repository.findById(id);
    if (opt.isEmpty()) return ResponseEntity.notFound().build();

    Usuario usr = opt.get();
    String nomeArquivo = usr.getNomeArquivoImagem();
    if (nomeArquivo == null || nomeArquivo.isEmpty()) return ResponseEntity.notFound().build();

    try {
      byte[] dados = fileStorageService.lerArquivo(nomeArquivo);
      String tipo = usr.getTipoImagem();
      if (tipo == null || tipo.isEmpty()) tipo = MediaType.APPLICATION_OCTET_STREAM_VALUE;

      return ResponseEntity.ok()
              .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomeArquivo + "\"")
              .contentType(MediaType.parseMediaType(tipo))
              .body(dados);
    } catch (IOException e) {
      return ResponseEntity.notFound().build();
    }
  }

}//FIM CLASSE
