package com.example.crudObsidiana.interfaces.controller;

import com.example.crudObsidiana.domain.entities.Equipamento;
import com.example.crudObsidiana.domain.ports.EquipamentoRepositoryPort;
import com.example.crudObsidiana.domain.use_cases.EquipamentoUseCase;
import com.example.crudObsidiana.infrastructure.file.FileStorageService;
import com.example.crudObsidiana.interfaces.dto.EquipamentoDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Tag(name = "Equipamentos", description = "Operações relacionadas aos equipamentos")
@RestController
@RequestMapping("/api/equipamento")
public class EquipamentoController {

    private final EquipamentoRepositoryPort equipamentoRepository;
    private final EquipamentoUseCase        equipamentoUseCase;
    private final FileStorageService        fileStorageService;

    @Autowired
    public EquipamentoController(EquipamentoRepositoryPort equipamentoRepository,
                                 EquipamentoUseCase equipamentoUseCase,
                                 FileStorageService fileStorageService) {
        this.equipamentoRepository = equipamentoRepository;
        this.equipamentoUseCase    = equipamentoUseCase;
        this.fileStorageService    = fileStorageService;
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo equipamento multimídia")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Equipamento criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Equipamento.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<Equipamento> criarEquipamento(@RequestBody EquipamentoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(equipamentoUseCase.criarEquipamento(dto));
    }

    @GetMapping
    @Operation(summary = "Lista todos os equipamentos")
    @ApiResponse(responseCode = "200", description = "Lista de equipamentos retornada com sucesso",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Equipamento.class))))
    public ResponseEntity<List<Equipamento>> listarTodos() {
        List<Equipamento> equipamentos = equipamentoRepository.findAll();
        if (equipamentos.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(equipamentos);
    }

    / ---------------------------------------------------------------
    // GET /equipamento/paginado?page=0&size=10
    // ---------------------------------------------------------------
    @Operation(summary = "Lista equipamentos com paginação e busca",
            description = "Retorna uma página de equipamentos. Parâmetros: 'page' (base 0), 'size' (itens por página) e 'busca' (filtra por nome, opcional).")
    @ApiResponse(responseCode = "200", description = "Página de equipamentos retornada com sucesso")
    @GetMapping("/paginado")
    public ResponseEntity<Page<Equipamento>> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String busca) {
        Pageable pageable = PageRequest.of(page, size);
        // Usa findByBusca para pesquisar em todos os campos (nome, categoria, marca, modelo, etc.)
        Page<Equipamento> resultado = busca.isBlank()
                ? repository.findAll(pageable)
                : repository.findByBusca(busca, pageable);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Recupera um equipamento pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Equipamento encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Equipamento.class))),
            @ApiResponse(responseCode = "404", description = "Equipamento não encontrado")
    })
    public ResponseEntity<Equipamento> recuperar(@PathVariable Long id) {
        return equipamentoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um equipamento pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Equipamento removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Equipamento não encontrado")
    })
    public ResponseEntity<Equipamento> excluir(@PathVariable Long id) {
        Optional<Equipamento> opt = equipamentoRepository.findById(id);
        if (opt.isPresent()) {
            equipamentoRepository.deleteById(id);
            return ResponseEntity.ok(opt.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um equipamento existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Equipamento atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Equipamento.class))),
            @ApiResponse(responseCode = "404", description = "Equipamento não encontrado")
    })
    public ResponseEntity<Equipamento> atualizar(@PathVariable Long id,
                                                 @RequestBody Equipamento body) {
        return equipamentoRepository.findById(id).map(existente -> {
            if (body.getNome()            != null) existente.setNome(body.getNome());
            if (body.getCategoria()       != null) existente.setCategoria(body.getCategoria());
            if (body.getMarca()           != null) existente.setMarca(body.getMarca());
            if (body.getNumeroSerie()     != null) existente.setNumeroSerie(body.getNumeroSerie());
            if (body.getModelo()          != null) existente.setModelo(body.getModelo());
            if (body.getValorPorHora()    != null) existente.setValorPorHora(body.getValorPorHora());
            if (body.getQuantidadeTotal() != null) existente.setQuantidadeTotal(body.getQuantidadeTotal());
            return ResponseEntity.ok(equipamentoRepository.save(existente));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/imagem")
    @Operation(summary = "Upload de imagem para equipamento")
    public ResponseEntity<?> uploadImagem(@PathVariable Long id,
                                          @RequestParam("arquivo") MultipartFile arquivo) {
        try {
            if (!equipamentoRepository.existsById(id)) return ResponseEntity.notFound().build();
            Path caminho   = fileStorageService.salvarArquivo(arquivo);
            Equipamento eq = equipamentoRepository.findById(id).orElseThrow();
            eq.setNomeArquivoImagem(caminho.getFileName().toString());
            eq.setTipoImagem(arquivo.getContentType());
            eq.setCaminhoImagem(caminho.toString());
            return ResponseEntity.ok(equipamentoRepository.save(eq));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Erro ao salvar arquivo: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/imagem")
    @Operation(summary = "Download/visualizar imagem do equipamento")
    public ResponseEntity<byte[]> baixarImagem(@PathVariable Long id) {
        Optional<Equipamento> opt = equipamentoRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Equipamento eq     = opt.get();
        String nomeArquivo = eq.getNomeArquivoImagem();
        if (nomeArquivo == null || nomeArquivo.isEmpty()) return ResponseEntity.notFound().build();
        try {
            byte[] dados = fileStorageService.lerArquivo(nomeArquivo);
            String tipo  = (eq.getTipoImagem() == null || eq.getTipoImagem().isEmpty())
                    ? MediaType.APPLICATION_OCTET_STREAM_VALUE : eq.getTipoImagem();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomeArquivo + "\"")
                    .contentType(MediaType.parseMediaType(tipo))
                    .body(dados);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}