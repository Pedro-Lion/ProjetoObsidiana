package com.example.crudObsidiana.interfaces.controller;

import com.example.crudObsidiana.domain.entities.Profissional;
import com.example.crudObsidiana.domain.ports.ProfissionalRepositoryPort;
import com.example.crudObsidiana.domain.use_cases.ProfissionalUseCase;
import com.example.crudObsidiana.interfaces.dto.ProfissionalDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Profissionais", description = "Operações relacionadas aos profissionais")
@RestController
@RequestMapping("/api/profissional")
public class ProfissionalController {

    private final ProfissionalRepositoryPort profissionalRepository;
    private final ProfissionalUseCase        profissionalUseCase;

    @Autowired
    public ProfissionalController(ProfissionalRepositoryPort profissionalRepository,
                                  ProfissionalUseCase profissionalUseCase) {
        this.profissionalRepository = profissionalRepository;
        this.profissionalUseCase    = profissionalUseCase;
    }

    @GetMapping
    @Operation(summary = "Lista todos os profissionais")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Profissional.class))))
    public List<Profissional> listarTodos() {
        return profissionalRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Recupera um profissional pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profissional encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Profissional.class))),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado")
    })
    public ResponseEntity<Profissional> recuperar(@PathVariable Long id) {
        return profissionalRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo profissional")
    @ApiResponse(responseCode = "201", description = "Profissional criado com sucesso",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Profissional.class)))
    public ResponseEntity<Profissional> create(@RequestBody ProfissionalDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(profissionalUseCase.criarProfissional(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um profissional pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profissional removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado")
    })
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (profissionalRepository.existsById(id)) {
            profissionalRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza dados de um profissional existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profissional atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Profissional.class))),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado")
    })
    public ResponseEntity<Profissional> atualizar(@PathVariable Long id,
                                                  @RequestBody Profissional profissional) {
        if (profissionalRepository.existsById(id)) {
            profissional.setId(id);
            return ResponseEntity.ok(profissionalRepository.save(profissional));
        }
        return ResponseEntity.notFound().build();
    }
}