package com.example.crudObsidiana.controller;

import com.example.crudObsidiana.dto.ServicoDTO;
import com.example.crudObsidiana.model.Servico;
import com.example.crudObsidiana.repository.ServicoRepository;
import com.example.crudObsidiana.service.ServicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Serviços", description = "Operações relacionadas aos serviços")
@RestController
@RequestMapping("/api/servico")
public class ServicoController {

    private final ServicoRepository repository;

    public ServicoController(ServicoRepository repository) {
        this.repository = repository;
    }

    @Autowired
    private ServicoService servicoService;

    @Operation(summary = "Cadastra um novo serviço com equipamentos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Serviço criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Servico.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<Servico> criarServico(@RequestBody ServicoDTO dto) {
        Servico servicoCriado = servicoService.criarServico(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(servicoCriado));
    }

    // ---------------------------------------------------------------
    // GET /servico — listagem completa (compatibilidade legada)
    // ---------------------------------------------------------------
    @Operation(summary = "Lista todos os serviços (sem paginação)")
    @ApiResponse(responseCode = "200", description = "Lista de serviços retornada com sucesso",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Servico.class))))
    @GetMapping
    public List<Servico> listarTodos() {
        return repository.findAll();
    }

    // ---------------------------------------------------------------
    // GET /servico/paginado?page=0&size=10
    // ---------------------------------------------------------------
    @Operation(summary = "Lista serviços com paginação e busca",
            description = "Retorna uma página de serviços. Parâmetros: 'page' (base 0), 'size' (itens por página) e 'busca' (filtra por nome, opcional).")
    @ApiResponse(responseCode = "200", description = "Página de serviços retornada com sucesso")
    @GetMapping("/paginado")
    public ResponseEntity<Page<Servico>> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String busca) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Servico> resultado = busca.isBlank()
                ? repository.findAll(pageable)
                : repository.findByNomeContainingIgnoreCase(busca, pageable);
        return ResponseEntity.ok(resultado);
    }

    @Operation(summary = "Recupera um serviço pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Servico.class))),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Servico> recuperar(@PathVariable("id") Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Remove um serviço pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Atualiza um serviço existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Servico.class))),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Servico> atualizar(@PathVariable("id") Long id, @RequestBody ServicoDTO dto) {
        Servico servico = servicoService.criarServico(dto);
        servico.setId(id);
        return ResponseEntity.ok(repository.save(servico));
    }
}