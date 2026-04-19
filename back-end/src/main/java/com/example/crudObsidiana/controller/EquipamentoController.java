package com.example.crudObsidiana.controller;

import com.example.crudObsidiana.dto.EquipamentoDTO;
import com.example.crudObsidiana.model.Equipamento;
import com.example.crudObsidiana.repository.EquipamentoRepository;
import com.example.crudObsidiana.service.EquipamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.crudObsidiana.service.FileStorageService;

import java.nio.file.*;
import java.util.List;

@Tag(name = "Equipamentos", description = "Operações relacionadas aos equipamentos")
@RestController
@RequestMapping("/api/equipamento")
public class EquipamentoController {

    private final EquipamentoRepository repository;
    private final FileStorageService fileStorageService;

    @Autowired
    private EquipamentoService equipamentoService;

    @Autowired
    public EquipamentoController(EquipamentoRepository repository,
                                 EquipamentoService equipamentoService,
                                 FileStorageService fileStorageService) {
        this.repository = repository;
        this.equipamentoService = equipamentoService;
        this.fileStorageService = fileStorageService;
    }

    @Operation(summary = "Cadastra um novo equipamento multimídia")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Equipamento criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Equipamento.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<Equipamento> criarEquipamento(@RequestBody EquipamentoDTO dto) {
        Equipamento equipamentoCriado = equipamentoService.criarEquipamento(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(equipamentoCriado);
    }

    // ---------------------------------------------------------------
    // GET /equipamento — listagem completa (compatibilidade legada)
    // ---------------------------------------------------------------
    @Operation(summary = "Lista todos os equipamentos (sem paginação)")
    @ApiResponse(responseCode = "200", description = "Lista de equipamentos retornada com sucesso",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Equipamento.class))))
    @GetMapping
    public ResponseEntity<List<Equipamento>> listarTodos() {
        List<Equipamento> equipamentos = repository.findAll();
        if (equipamentos.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(equipamentos);
    }

    // ---------------------------------------------------------------
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

    @Operation(summary = "Recupera um equipamento pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipamento encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Equipamento.class))),
            @ApiResponse(responseCode = "404", description = "Equipamento não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Equipamento> recuperar(@PathVariable("id") Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Remove um equipamento pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipamento removido com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Equipamento.class))),
            @ApiResponse(responseCode = "404", description = "Equipamento não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Equipamento> excluir(@PathVariable("id") Long id) {
        if (repository.existsById(id)) {
            ResponseEntity<Equipamento> deletado = repository.findById(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
            repository.deleteById(id);
            return deletado;
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Atualiza um equipamento existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipamento atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Equipamento.class))),
            @ApiResponse(responseCode = "404", description = "Equipamento não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Equipamento> atualizar(@PathVariable("id") Long id, @RequestBody Equipamento equipamentoBody) {
        return repository.findById(id).map(existente -> {
            if (equipamentoBody.getNome() != null) existente.setNome(equipamentoBody.getNome());
            if (equipamentoBody.getCategoria() != null) existente.setCategoria(equipamentoBody.getCategoria());
            if (equipamentoBody.getMarca() != null) existente.setMarca(equipamentoBody.getMarca());
            if (equipamentoBody.getNumeroSerie() != null) existente.setNumeroSerie(equipamentoBody.getNumeroSerie());
            if (equipamentoBody.getModelo() != null) existente.setModelo(equipamentoBody.getModelo());
            if (equipamentoBody.getValorPorHora() != null) existente.setValorPorHora(equipamentoBody.getValorPorHora());
            if (equipamentoBody.getQuantidadeTotal() != null) {
                existente.setQuantidadeTotal(equipamentoBody.getQuantidadeTotal());
            }
            Equipamento salvo = repository.save(existente);
            return ResponseEntity.ok(salvo);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/imagem")
    public ResponseEntity<?> uploadImagemEquipamento(@PathVariable Long id,
                                                     @RequestParam("arquivo") MultipartFile arquivo) {
        try {
            if (!repository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            Path caminho = fileStorageService.salvarArquivo(arquivo);
            Equipamento equipamento = repository.findById(id).get();
            equipamento.setNomeArquivoImagem(caminho.getFileName().toString());
            equipamento.setTipoImagem(arquivo.getContentType());
            equipamento.setCaminhoImagem(caminho.toString());
            repository.save(equipamento);
            return ResponseEntity.ok(equipamento);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Erro ao salvar arquivo: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/imagem")
    public ResponseEntity<byte[]> baixarImagemEquipamento(@PathVariable Long id) {
        java.util.Optional<Equipamento> opt = repository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Equipamento eqp = opt.get();
        String nomeArquivo = eqp.getNomeArquivoImagem();
        if (nomeArquivo == null || nomeArquivo.isEmpty()) return ResponseEntity.notFound().build();
        try {
            byte[] dados = fileStorageService.lerArquivo(nomeArquivo);
            String tipo = eqp.getTipoImagem();
            if (tipo == null || tipo.isEmpty()) tipo = org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
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