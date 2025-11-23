package com.example.crudObsidiana.controller;

import com.example.crudObsidiana.dto.OrcamentoDTO;
import com.example.crudObsidiana.model.Orcamento;
import com.example.crudObsidiana.repository.OrcamentoRepository;
import com.example.crudObsidiana.service.OrcamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import java.util.Optional;

@RestController
@RequestMapping("/orcamentos")
@Tag(
        name = "Orçamentos",
        description = "Endpoints para criação, consulta e alteração de status de orçamentos. " +
                "A alteração de status para 'Confirmado' ou saída desse status dispara o Observer " +
                "responsável por atualizar o estoque de equipamentos."
)
public class OrcamentoController {

    @Autowired
    private OrcamentoService orcamentoService;

    @Autowired
    private OrcamentoRepository orcamentoRepository;

    // DTO simples só para o corpo do PUT /status
    public static class AtualizarStatusRequest {
        @Schema(description = "Novo status do orçamento", example = "Confirmado")
        private String status;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    // ----------------------------------------------------------------------
    // POST /orcamentos  → Criação de orçamento
    // ----------------------------------------------------------------------
    @PostMapping
    @Operation(
            summary = "Criar um novo orçamento",
            description = "Cria um orçamento com os dados informados. " +
                    "Apenas a criação não altera o estoque de equipamentos; " +
                    "o ajuste de estoque ocorre na alteração de status."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Orçamento criado com sucesso",
                    content = @Content(schema = @Schema(implementation = Orcamento.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição")
    })
    public ResponseEntity<Orcamento> criarOrcamento(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados para criação do orçamento",
                    required = true,
                    content = @Content(schema = @Schema(implementation = OrcamentoDTO.class))
            )
            OrcamentoDTO dto
    ) {
        Orcamento criado = orcamentoService.criarOrcamento(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    // ----------------------------------------------------------------------
    // GET /orcamentos  → Listar todos
    // ----------------------------------------------------------------------
    @GetMapping
    @Operation(
            summary = "Listar todos os orçamentos",
            description = "Retorna a lista completa de orçamentos cadastrados."
    )
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<Orcamento>> listarTodos() {
        List<Orcamento> lista = orcamentoRepository.findAll();
        return ResponseEntity.ok(lista);
    }

    // ----------------------------------------------------------------------
    // GET /orcamentos/{id}  → Buscar por ID
    // ----------------------------------------------------------------------
    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar orçamento por ID",
            description = "Retorna um orçamento específico a partir do seu identificador."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orçamento encontrado",
                    content = @Content(schema = @Schema(implementation = Orcamento.class))),
            @ApiResponse(responseCode = "404", description = "Orçamento não encontrado")
    })
    public ResponseEntity<Orcamento> buscarPorId(
            @Parameter(description = "ID do orçamento", example = "1")
            @PathVariable Long id
    ) {
        Optional<Orcamento> opt = orcamentoRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(opt.get());
    }

    // ----------------------------------------------------------------------
    // PUT /orcamentos/{id}/status  → Alterar status (Observer dispara aqui)
    // ----------------------------------------------------------------------
    @PutMapping("/{id}/status")
    @Operation(
            summary = "Atualizar status do orçamento",
            description = """
                    Atualiza o status de um orçamento existente. \
                    Quando ocorre a transição de/para o status 'Confirmado', \
                    o padrão Observer é acionado para reservar ou devolver \
                    as quantidades de equipamentos associados a esse orçamento.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = Orcamento.class))),
            @ApiResponse(responseCode = "400", description = "Status inválido ou requisição inconsistente"),
            @ApiResponse(responseCode = "404", description = "Orçamento não encontrado")
    })
    public ResponseEntity<Orcamento> atualizarStatus(
            @Parameter(description = "ID do orçamento a ser atualizado", example = "1")
            @PathVariable Long id,
            @RequestBody AtualizarStatusRequest body
    ) {
        if (body == null || body.getStatus() == null || body.getStatus().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Orcamento atualizado = orcamentoService.atualizarStatus(id, body.getStatus());
            return ResponseEntity.ok(atualizado);
        } catch (RuntimeException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("não encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
