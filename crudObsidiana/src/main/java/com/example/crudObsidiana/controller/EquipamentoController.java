package com.example.crudObsidiana.controller;

import com.example.crudObsidiana.model.Equipamento;
import com.example.crudObsidiana.repository.EquipamentoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equipamento")
public class EquipamentoController {
    public EquipamentoController(EquipamentoRepository repository) {this.repository = repository;}

    @PostMapping
    public ResponseEntity<Equipamento> create(@RequestBody Equipamento equipamento){
        Equipamento salvo = repository.save(equipamento);
        return ResponseEntity.ok(salvo);
    }

    @GetMapping
    public List<Equipamento> listarTodos(){return repository.findAll();}



    private final EquipamentoRepository repository;
}
