package com.example.crudObsidiana.interfaces.controller;

import com.example.crudObsidiana.interfaces.dto.EquipamentoDTO;
import com.example.crudObsidiana.domain.entities.Equipamento;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.crudObsidiana.infrastructure.file.FileStorageService;

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

//    CONSTRUCTORS
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


    @Operation(summary = "Lista todos os equipamentos")
    @ApiResponse(responseCode = "200", description = "Lista de equipamentos retornada com sucesso",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Equipamento.class))))
    @GetMapping
    public ResponseEntity<List<Equipamento>> listarTodos() {
        List<Equipamento> equipamentos = repository.findAll();
        if (equipamentos.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(equipamentos);
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
            // atualiza somente campos que fazem sentido atualizar via PUT (ou os que não vieram nulos)
            if (equipamentoBody.getNome() != null) existente.setNome(equipamentoBody.getNome());
            if (equipamentoBody.getCategoria() != null) existente.setCategoria(equipamentoBody.getCategoria());
            if (equipamentoBody.getMarca() != null) existente.setMarca(equipamentoBody.getMarca());
            if (equipamentoBody.getNumeroSerie() != null) existente.setNumeroSerie(equipamentoBody.getNumeroSerie());
            if (equipamentoBody.getModelo() != null) existente.setModelo(equipamentoBody.getModelo());
            if (equipamentoBody.getValorPorHora() != null) existente.setValorPorHora(equipamentoBody.getValorPorHora());

            // para quantidadeTotal: usa o setter (que agora aplica delta corretamente)
            if (equipamentoBody.getQuantidadeTotal() != null) {
                existente.setQuantidadeTotal(equipamentoBody.getQuantidadeTotal());
            }
            Equipamento salvo = repository.save(existente);
            return ResponseEntity.ok(salvo);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }


    // --- ENDPOINTS DE IMAGEM ---

    // Upload de imagem para equipamento
    @PostMapping("/{id}/imagem")
    public ResponseEntity<?> uploadImagemEquipamento(@PathVariable Long id,
                                                     @RequestParam("arquivo") MultipartFile arquivo) {
        try {
            if (!repository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            // chama o metodo de instância (NÃO estático)
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

    // Download/visualizar imagem do equipamento
    @GetMapping("/{id}/imagem")
    public ResponseEntity<byte[]> baixarImagemEquipamento(@PathVariable Long id) {
        // Busca o equipamento (Optional)
        java.util.Optional<Equipamento> opt = repository.findById(id);

        if (opt.isEmpty()) {
            // equipamento não existe
            return ResponseEntity.notFound().build();
        }

        Equipamento eqp = opt.get();

        String nomeArquivo = eqp.getNomeArquivoImagem();
        if (nomeArquivo == null || nomeArquivo.isEmpty()) {
            // equipamento existe, mas não tem imagem associada
            return ResponseEntity.notFound().build();
        }

        try {
            // Lê os bytes do arquivo
            byte[] dados = fileStorageService.lerArquivo(nomeArquivo);

            String tipo = eqp.getTipoImagem();
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