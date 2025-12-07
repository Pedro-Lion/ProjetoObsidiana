package com.example.crudObsidiana.controller;

import com.example.crudObsidiana.dto.KpisOrcamentoDTO;
import com.example.crudObsidiana.dto.OrcamentoDTO;
import com.example.crudObsidiana.model.Orcamento;
import com.example.crudObsidiana.repository.OrcamentoRepository;
import com.example.crudObsidiana.service.OrcamentoService;
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

import java.util.List;

@Tag(name = "Orçamentos", description = "Operações relacionadas aos orçamentos")
@RestController
@RequestMapping("/orcamento")
public class OrcamentoController {

    private final OrcamentoRepository repository;

    public OrcamentoController(OrcamentoRepository repository) {
        this.repository = repository;
    }

    @Autowired
    private OrcamentoService orcamentoService;

    // Criar um novo orçamento
    @Operation(summary = "Cadastra um novo orçamento audiovisual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orçamento criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Orcamento.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<Orcamento> criarOrcamento(@RequestBody OrcamentoDTO dto) {
        Orcamento orcamentoCriado = orcamentoService.criarOrcamento(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(orcamentoCriado);
    }

    // Listar todos os orçamentos
    @Operation(summary = "Lista todos os orçamentos cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de orçamentos retornada com sucesso",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Orcamento.class))))
    @GetMapping
    public List<Orcamento> listarTodos() {
        return repository.findAll();
    }

    // Buscar um orçamento por ID
    @Operation(summary = "Recupera um orçamento pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orçamento encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Orcamento.class))),
            @ApiResponse(responseCode = "404", description = "Orçamento não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Orcamento> recuperar(@PathVariable("id") Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Atualizar um orçamento existente
    @Operation(summary = "Atualiza um orçamento existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orçamento atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Orcamento.class))),
            @ApiResponse(responseCode = "404", description = "Orçamento não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Orcamento> atualizar(@PathVariable("id") Long id,@RequestBody Orcamento orcamentoAtualizado) {
        return repository.findById(id)
                .map(orcamentoExistente -> {
                    orcamentoAtualizado.setId(id);
                    repository.save(orcamentoAtualizado);
                    return ResponseEntity.ok(orcamentoAtualizado);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Remover um orçamento
    @Operation(summary = "Remove um orçamento pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orçamento removido com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Orcamento.class))),
            @ApiResponse(responseCode = "404", description = "Orçamento não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Orcamento> excluir(@PathVariable("id") Long id) {
        return repository.findById(id)
                .map(orcamento -> {
                    repository.deleteById(id);
                    return ResponseEntity.ok(orcamento);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Retorna as KPIs dos orçamentos (aprovados, pendentes e concluídos)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "KPIs retornadas com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = KpisOrcamentoDTO.class)))
    })
    @GetMapping("/kpis")
    public ResponseEntity<KpisOrcamentoDTO> getKpis() {
        return ResponseEntity.ok(orcamentoService.getKpis());
    }

}
