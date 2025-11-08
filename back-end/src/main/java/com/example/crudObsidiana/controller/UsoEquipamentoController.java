package com.example.crudObsidiana.controller;

import com.example.crudObsidiana.dto.UsoEquipamentoDTO;
import com.example.crudObsidiana.model.UsoEquipamento;
import com.example.crudObsidiana.repository.UsoEquipamentoRepository;
import com.example.crudObsidiana.service.UsoEquipamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Uso de Equipamentos", description = "Operações relacionadas ao uso de equipamentos em orçamentos e serviços")
@RestController
@RequestMapping("/uso-equipamento")
public class UsoEquipamentoController {

    private final UsoEquipamentoRepository repository;

    public UsoEquipamentoController(UsoEquipamentoRepository repository) {
        this.repository = repository;
    }

    @Autowired
    private UsoEquipamentoService usoEquipamentoService;

    @Operation(summary = "Registra o uso de um equipamento em um orçamento ou serviço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Uso de equipamento registrado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsoEquipamento.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou campos obrigatórios ausentes")
    })
    @PostMapping
    public ResponseEntity<UsoEquipamento> criarUso(@Valid @RequestBody UsoEquipamentoDTO dto) {
        UsoEquipamento usoCriado = usoEquipamentoService.registrarUso(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(usoCriado);
    }

    @Operation(summary = "Lista todos os usos de equipamentos")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = UsoEquipamento.class))))
    @GetMapping
    public List<UsoEquipamento> listarTodos() {
        return repository.findAll();
    }

    @Operation(summary = "Recupera um uso de equipamento pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Uso de equipamento encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsoEquipamento.class))),
            @ApiResponse(responseCode = "404", description = "Uso de equipamento não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsoEquipamento> recuperar(@PathVariable("id") Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Remove um registro de uso de equipamento pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Uso de equipamento removido com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsoEquipamento.class))),
            @ApiResponse(responseCode = "404", description = "Uso de equipamento não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<UsoEquipamento> excluir(@PathVariable("id") Long id) {
        if (repository.existsById(id)) {
            ResponseEntity<UsoEquipamento> deletado = repository.findById(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
            repository.deleteById(id);
            return deletado;
        }
        return ResponseEntity.notFound().build();
    }
}
