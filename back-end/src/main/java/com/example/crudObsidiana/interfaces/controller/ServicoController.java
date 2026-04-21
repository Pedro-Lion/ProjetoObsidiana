package com.example.crudObsidiana.interfaces.controller;

import com.example.crudObsidiana.domain.entities.Servico;
import com.example.crudObsidiana.domain.ports.ServicoRepositoryPort;
import com.example.crudObsidiana.domain.use_cases.ServicoUseCase;
import com.example.crudObsidiana.interfaces.dto.ServicoDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    private final ServicoRepositoryPort servicoRepository;
    private final ServicoUseCase        servicoUseCase;

    @Autowired
    public ServicoController(ServicoRepositoryPort servicoRepository,
                             ServicoUseCase servicoUseCase) {
        this.servicoRepository = servicoRepository;
        this.servicoUseCase    = servicoUseCase;
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo serviço com equipamentos")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Serviço criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Servico.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<Servico> criarServico(@RequestBody ServicoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicoUseCase.criarServico(dto));
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
        return servicoRepository.findAll();
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
        // Usa findByBusca para pesquisar em todos os campos (nome, descrição, horas, valor)
        Page<Servico> resultado = busca.isBlank()
                ? repository.findAll(pageable)
                : repository.findByBusca(busca, pageable);
        return ResponseEntity.ok(resultado);
    }

    @Operation(summary = "Recupera um serviço pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Serviço encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Servico.class))),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Servico> recuperar(@PathVariable Long id) {
        return servicoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um serviço pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Serviço removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (servicoRepository.existsById(id)) {
            servicoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um serviço existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Serviço atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Servico.class))),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ResponseEntity<Servico> atualizar(@PathVariable Long id, @RequestBody ServicoDTO dto) {
        return ResponseEntity.ok(servicoUseCase.editarServico(id, dto));
    }
}