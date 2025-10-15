package com.example.crudObsidiana.controller;

import com.example.crudObsidiana.model.Profissional;
import com.example.crudObsidiana.repository.ProfissionalRepository;
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

@Tag(name = "Profissionais", description = "Operações relacionadas aos profissionais")
@RestController
@RequestMapping("/profissionais")
public class ProfissionalController {

    private final ProfissionalRepository repository;

    public ProfissionalController(ProfissionalRepository repository) {
        this.repository = repository;
    }

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
    @ApiResponse(responseCode = "200", description = "Profissional criado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Profissional.class)))
    @PostMapping
    public ResponseEntity<Profissional> create(@RequestBody Profissional profissional) {
        Profissional salvo = repository.save(profissional);
        return ResponseEntity.ok(salvo);
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
}
