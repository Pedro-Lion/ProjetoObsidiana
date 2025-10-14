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

//    @GetMapping("/{id}")
//    public Equipamento selecionar(@PathVariable("id") Long id) {
//        Equipamento equipamento = repository.findById(id).orElse(null);
//        return equipamento;
//    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipamento> recuperar(@PathVariable("id") Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

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

    @PutMapping("/{id}")
    public ResponseEntity<Equipamento> atualizar(@PathVariable("id") Long id, @RequestBody
    Equipamento equipamento) {
        if (repository.existsById(id)) {
            equipamento.setId(id);
            repository.save(equipamento);
            return ResponseEntity.ok(equipamento);
        }
        return ResponseEntity.notFound().build();
    }

    private final EquipamentoRepository repository;
}
