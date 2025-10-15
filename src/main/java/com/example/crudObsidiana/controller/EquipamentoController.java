package com.example.crudObsidiana.controller;

import com.example.crudObsidiana.model.Equipamento;
import com.example.crudObsidiana.repository.EquipamentoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Equipamentos", description = "Operações relacionadas aos equipamentos")
@RestController
@RequestMapping("/equipamento")
public class EquipamentoController {

    private final EquipamentoRepository repository;

    public EquipamentoController(EquipamentoRepository repository) {
        this.repository = repository;
    }

    @Operation(summary = "Cadastra um novo equipamento")
    @ApiResponse(responseCode = "200", description = "Equipamento criado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Equipamento.class)))
    @PostMapping
    public ResponseEntity<Equipamento> create(@RequestBody Equipamento equipamento) {
        Equipamento salvo = repository.save(equipamento);
        return ResponseEntity.ok(salvo);
    }

    @Operation(summary = "Lista todos os equipamentos")
    @ApiResponse(responseCode = "200", description = "Lista de equipamentos retornada com sucesso",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Equipamento.class))))
    @GetMapping
    public List<Equipamento> listarTodos() {
        return repository.findAll();
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
    public ResponseEntity<Equipamento> atualizar(@PathVariable("id") Long id, @RequestBody Equipamento equipamento) {
        if (repository.existsById(id)) {
            equipamento.setId(id);
            repository.save(equipamento);
            return ResponseEntity.ok(equipamento);
        }
        return ResponseEntity.notFound().build();
    }
}