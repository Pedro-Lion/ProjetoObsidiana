package com.example.crudObsidiana.controller;

import com.example.crudObsidiana.model.UsoEquipamento;
import com.example.crudObsidiana.repository.UsoEquipamentoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/uso-equipamento")
@Tag(name = "Uso de Equipamentos", description = "Gerencia o uso de equipamentos em orçamentos e serviços")
public class UsoEquipamentoController {

    @Autowired
    private UsoEquipamentoRepository repository;

    @GetMapping
    @Operation(summary = "Lista todos os usos de equipamentos")
    public List<UsoEquipamento> listar() {
        return repository.findAll();
    }

    @PostMapping
    @Operation(summary = "Cria um novo vínculo de uso de equipamento")
    public UsoEquipamento criar(@RequestBody UsoEquipamento uso) {
        return repository.save(uso);
    }
}
