package com.example.crudObsidiana.controller;

import com.example.crudObsidiana.model.Equipamento;
import com.example.crudObsidiana.model.Servico;
import com.example.crudObsidiana.repository.EquipamentoRepository;
import com.example.crudObsidiana.repository.ServicoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servico")
public class ServicoController {
    public ServicoController(ServicoRepository repository) {this.repository = repository;}

    @PostMapping
    public ResponseEntity<Servico> create(@RequestBody Servico servico){
        Servico salvo = repository.save(servico);
        return ResponseEntity.ok(salvo);
    }

    @GetMapping
    public List<Servico> listarTodos(){return repository.findAll();}

    @GetMapping("/{id}")
    public ResponseEntity<Servico> recuperar(@PathVariable("id") Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Servico> excluir(@PathVariable("id") Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Servico> atualizar(@PathVariable("id") Long id, @RequestBody
    Servico servico) {
        if (repository.existsById(id)) {
            servico.setId(id);
            repository.save(servico);
            return ResponseEntity.ok(servico);
        }
        return ResponseEntity.notFound().build();
    }


    private final ServicoRepository repository;
}
