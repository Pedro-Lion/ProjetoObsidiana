package com.example.crudObsidiana.controller;

import com.example.crudObsidiana.dto.UsoEquipamentoDTO;
import com.example.crudObsidiana.model.UsoEquipamento;
import com.example.crudObsidiana.repository.UsoEquipamentoRepository;
import com.example.crudObsidiana.service.UsoEquipamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Uso de Equipamentos", description = "Operações relacionadas ao uso de equipamentos em orçamentos e serviços")
@RestController
@RequestMapping("/api/uso-equipamento")
public class UsoEquipamentoController {

    private final UsoEquipamentoRepository repository;
    private final UsoEquipamentoService service;

    public UsoEquipamentoController(UsoEquipamentoRepository repository, UsoEquipamentoService service) {
        this.repository = repository;
        this.service = service;
    }

    @Operation(summary = "Registra o uso de um equipamento em um orçamento ou serviço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Uso de equipamento registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou campos obrigatórios ausentes")
    })
    @PostMapping
    public ResponseEntity<UsoEquipamento> criarUso(@Valid @RequestBody UsoEquipamentoDTO dto) {
        try {
            UsoEquipamento usoCriado = service.registrarUso(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(usoCriado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Lista todos os usos de equipamentos")
    @GetMapping
    public ResponseEntity<List<UsoEquipamento>> listarTodos() {
        return ResponseEntity.ok(repository.findAll());
    }

    @Operation(summary = "Recupera um uso de equipamento pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<UsoEquipamento> recuperar(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Remove um registro de uso de equipamento pelo ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<UsoEquipamento> excluir(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        UsoEquipamento uso = repository.findById(id).orElse(null);
        repository.deleteById(id);
        return ResponseEntity.ok(uso);
    }
}
