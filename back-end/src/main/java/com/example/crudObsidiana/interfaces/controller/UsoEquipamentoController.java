package com.example.crudObsidiana.interfaces.controller;

import com.example.crudObsidiana.domain.entities.UsoEquipamento;
import com.example.crudObsidiana.domain.ports.UsoEquipamentoRepositoryPort;
import com.example.crudObsidiana.domain.use_cases.UsoEquipamentoUseCase;
import com.example.crudObsidiana.interfaces.dto.UsoEquipamentoDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Uso de Equipamentos",
        description = "Operações relacionadas ao uso de equipamentos em orçamentos e serviços")
@RestController
@RequestMapping("/uso-equipamento")
public class UsoEquipamentoController {

    private final UsoEquipamentoRepositoryPort usoEquipamentoRepository;
    private final UsoEquipamentoUseCase        usoEquipamentoUseCase;

    @Autowired
    public UsoEquipamentoController(UsoEquipamentoRepositoryPort usoEquipamentoRepository,
                                    UsoEquipamentoUseCase usoEquipamentoUseCase) {
        this.usoEquipamentoRepository = usoEquipamentoRepository;
        this.usoEquipamentoUseCase    = usoEquipamentoUseCase;
    }

    @PostMapping
    @Operation(summary = "Registra o uso de um equipamento em um orçamento ou serviço")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Uso de equipamento registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou campos obrigatórios ausentes")
    })
    public ResponseEntity<UsoEquipamento> criarUso(@Valid @RequestBody UsoEquipamentoDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(usoEquipamentoUseCase.registrarUso(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @Operation(summary = "Lista todos os usos de equipamentos")
    public ResponseEntity<List<UsoEquipamento>> listarTodos() {
        return ResponseEntity.ok(usoEquipamentoRepository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Recupera um uso de equipamento pelo ID")
    public ResponseEntity<UsoEquipamento> recuperar(@PathVariable Long id) {
        return usoEquipamentoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um registro de uso de equipamento pelo ID")
    public ResponseEntity<UsoEquipamento> excluir(@PathVariable Long id) {
        if (!usoEquipamentoRepository.existsById(id)) return ResponseEntity.notFound().build();
        UsoEquipamento uso = usoEquipamentoRepository.findById(id).orElse(null);
        usoEquipamentoRepository.deleteById(id);
        return ResponseEntity.ok(uso);
    }
}