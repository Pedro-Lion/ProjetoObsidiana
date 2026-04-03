package com.example.crudObsidiana.controller;

import com.example.crudObsidiana.dto.ProfissionalDTO;
import com.example.crudObsidiana.model.Equipamento;
import com.example.crudObsidiana.model.Profissional;
import com.example.crudObsidiana.repository.ProfissionalRepository;
import com.example.crudObsidiana.service.FileStorageService;
import com.example.crudObsidiana.service.ProfissionalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Tag(name = "Profissionais", description = "Operações relacionadas aos profissionais")
@RestController
@RequestMapping("/api/profissional")
public class ProfissionalController {

    private final ProfissionalRepository repository;

    private final FileStorageService fileStorageService;

    public ProfissionalController(ProfissionalRepository repository, FileStorageService fileStorageService) {
        this.repository = repository;
        this.fileStorageService = fileStorageService;
    }

    @Autowired
    private ProfissionalService profissionalService;

    @Operation(summary = "Lista todos os profissionais")
    @ApiResponse(responseCode = "200", description = "Lista de profissional retornada com sucesso",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Profissional.class))))
    @GetMapping
    public List<Profissional> listarTodos() {
        return repository.findAll();
    }

    @Operation(summary = "Recupera um profissional pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profissional encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Profissional.class))),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Profissional> recuperar(@PathVariable("id") Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Cadastra um novo profissional")
    @ApiResponse(responseCode = "201", description = "Profissional criado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Profissional.class)))
    @PostMapping
    public ResponseEntity<Profissional> create(@RequestBody ProfissionalDTO dto) {
        Profissional profissionalCriado = profissionalService.criarProfissional(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(profissionalCriado);
    }

    @Operation(summary = "Remove um profissional pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profissional removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Atualiza dados de um profissional existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profissional atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Profissional.class))),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Profissional> atualizar(@PathVariable("id") Long id, @RequestBody Profissional profissional) {
        if (repository.existsById(id)) {
            profissional.setId(id);
            repository.save(profissional);
            return ResponseEntity.ok(profissional);
        }
        return ResponseEntity.notFound().build();
    }

    // --- ENDPOINTS DE IMAGEM ---

    // Upload de imagem para profissional
    @PostMapping("/{id}/imagem")
    public ResponseEntity<?> uploadImagemEquipamento(@PathVariable Long id,
                                                     @RequestParam("arquivo") MultipartFile arquivo) {
        try {
            if (!repository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            // chama o metodo de instância (NÃO estático)
            Path caminho = fileStorageService.salvarArquivo(arquivo);

            Profissional profissional = repository.findById(id).get();
            profissional.setNomeArquivoImagem(caminho.getFileName().toString());
            profissional.setTipoImagem(arquivo.getContentType());
            profissional.setCaminhoImagem(caminho.toString());

            repository.save(profissional);

            return ResponseEntity.ok(profissional);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Erro ao salvar arquivo: " + e.getMessage());
        }
    }

    // Download/visualizar imagem do profissional
    @GetMapping("/{id}/imagem")
    public ResponseEntity<byte[]> baixarImagemEquipamento(@PathVariable Long id) {
        // Busca o profissional (Optional)
        java.util.Optional<Profissional> opt = repository.findById(id);

        if (opt.isEmpty()) {
            // profissional não existe
            return ResponseEntity.notFound().build();
        }

        Profissional prof = opt.get();

        String nomeArquivo = prof.getNomeArquivoImagem();
        if (nomeArquivo == null || nomeArquivo.isEmpty()) {
            // profissional existe, mas não tem imagem associada
            return ResponseEntity.notFound().build();
        }

        try {
            // Lê os bytes do arquivo
            byte[] dados = fileStorageService.lerArquivo(nomeArquivo);

            String tipo = prof.getTipoImagem();
            if (tipo == null || tipo.isEmpty()) {
                tipo = org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + nomeArquivo + "\"")
                    .contentType(org.springframework.http.MediaType.parseMediaType(tipo))
                    .body(dados);

        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
