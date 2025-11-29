package com.example.crudObsidiana.controller;

import com.example.crudObsidiana.dto.OrcamentoDTO;
import com.example.crudObsidiana.model.Orcamento;
import com.example.crudObsidiana.repository.OrcamentoRepository;
import com.example.crudObsidiana.service.OrcamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Orçamentos", description = "Operações relacionadas aos orçamentos")
@RestController
@RequestMapping("/orcamento")
public class OrcamentoController {

    private final OrcamentoRepository repository;
    private final OrcamentoService orcamentoService;

    public OrcamentoController(OrcamentoRepository repository, OrcamentoService orcamentoService) {
        this.repository = repository;
        this.orcamentoService = orcamentoService;
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo orçamento audiovisual")
    public ResponseEntity<Orcamento> criarOrcamento(@RequestBody OrcamentoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                orcamentoService.criarOrcamento(dto)
        );
    }

    @GetMapping
    @Operation(summary = "Lista todos os orçamentos")
    public List<Orcamento> listarTodos() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um orçamento por ID")
    public ResponseEntity<Orcamento> recuperar(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um orçamento pelo ID")
    public ResponseEntity<Orcamento> atualizar(@PathVariable Long id, @RequestBody Orcamento dados) {
        return repository.findById(id)
                .map(existente -> {
                    dados.setId(id);
                    return ResponseEntity.ok(repository.save(dados));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um orçamento pelo ID")
    public ResponseEntity<Orcamento> excluir(@PathVariable Long id) {
        return repository.findById(id)
                .map(orc -> {
                    repository.deleteById(id);
                    return ResponseEntity.ok(orc);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
