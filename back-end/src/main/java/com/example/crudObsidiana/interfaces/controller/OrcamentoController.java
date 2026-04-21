package com.example.crudObsidiana.interfaces.controller;

import com.example.crudObsidiana.domain.entities.Orcamento;
import com.example.crudObsidiana.domain.ports.OrcamentoRepositoryPort;
import com.example.crudObsidiana.domain.use_cases.OrcamentoUseCase;
import com.example.crudObsidiana.interfaces.dto.KpisOrcamentoDTO;
import com.example.crudObsidiana.interfaces.dto.OrcamentoDTO;

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
import org.springframework.web.server.ResponseStatusException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orcamento")
@Tag(
        name = "Orçamento",
        description = """
        Endpoints para criação, consulta e alteração de status de orçamentos.
        A alteração de status para 'Confirmado' (ou saída desse status)
        aciona o padrão de projeto Observer, que atualiza automaticamente
        a quantidade disponível dos equipamentos vinculados.
        """
)
public class OrcamentoController {

    private final OrcamentoUseCase        orcamentoUseCase;
    private final OrcamentoRepositoryPort orcamentoRepository;

    @Autowired
    public OrcamentoController(OrcamentoUseCase orcamentoUseCase,
                               OrcamentoRepositoryPort orcamentoRepository) {
        this.orcamentoUseCase    = orcamentoUseCase;
        this.orcamentoRepository = orcamentoRepository;
    }

    public static class AtualizarStatusRequest {
        @Schema(description = "Novo status do orçamento", example = "Confirmado")
        private String status;
        public String getStatus()              { return status; }
        public void   setStatus(String status) { this.status = status; }
    }

    @PostMapping
    @Operation(
            summary = "Criar um novo orçamento",
            description = """
            Cria um orçamento com status inicial 'Em análise'.
            O estoque de equipamentos NÃO é atualizado nesta etapa.
            O controle de estoque só ocorre ao alterar o status para 'Confirmado'.
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Orçamento criado com sucesso",
                    content = @Content(schema = @Schema(implementation = Orcamento.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos enviados")
    })
    public ResponseEntity<Orcamento> criarOrcamento(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados para criação do orçamento", required = true,
                    content = @Content(schema = @Schema(implementation = OrcamentoDTO.class)))
            @RequestBody OrcamentoDTO dto) {
        Orcamento criado = orcamentoUseCase.criarOrcamento(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @GetMapping
    @Operation(summary = "Listar todos os orçamentos")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<Orcamento>> listarTodos() {
        return ResponseEntity.ok(orcamentoRepository.findAll());
    }

    // ----------------------------------------------------------------------
    // GET /orcamento/paginado?page=0&size=8&busca= → Listar com paginação
    // ----------------------------------------------------------------------
    @GetMapping("/paginado")
    @Operation(
            summary = "Listar orçamentos com paginação e busca",
            description = "Retorna uma página de orçamentos. Parâmetros: 'page' (base 0), 'size' (itens por página) e 'busca' (filtra por local do evento ou descrição, opcional)."
    )
    @ApiResponse(responseCode = "200", description = "Página de orçamentos retornada com sucesso")
    public ResponseEntity<Page<Orcamento>> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "") String busca) {
        Page<Orcamento> resultado = orcamentoUseCase.listarPaginado(page, size, busca);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar orçamento por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orçamento encontrado"),
            @ApiResponse(responseCode = "404", description = "Orçamento não encontrado")
    })
    public ResponseEntity<Orcamento> buscarPorId(
            @Parameter(description = "ID do orçamento", example = "1")
            @PathVariable Long id) {
        Optional<Orcamento> opt = orcamentoRepository.findById(id);
        return opt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/calendar/{idCalendar}")
    @Operation(summary = "Buscar orçamento por ID do Calendário Outlook")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orçamento encontrado"),
            @ApiResponse(responseCode = "404", description = "Orçamento não encontrado")
    })
    public ResponseEntity<Orcamento> buscarPorIdCalendar(
            @Parameter(description = "IDCalendar do orçamento", example = "ASAICuyvcaUICya234...")
            @PathVariable String idCalendar) {
        Optional<Orcamento> opt = orcamentoRepository.findByIdCalendar(idCalendar);
        return opt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/kpis")
    @Operation(summary = "Retorna as KPIs dos orçamentos (confirmados, pendentes e cancelados)")
    @ApiResponse(responseCode = "200", description = "KPIs retornadas com sucesso",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = KpisOrcamentoDTO.class)))
    public ResponseEntity<KpisOrcamentoDTO> getKpis() {
        return ResponseEntity.ok(orcamentoUseCase.getKpis());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar orçamento")
    public ResponseEntity<Orcamento> atualizarOrcamento(
            @PathVariable Long id,
            @RequestBody OrcamentoDTO dto) {
        try {
            return ResponseEntity.ok(orcamentoUseCase.editarOrcamento(id, dto));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir orçamento")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (orcamentoRepository.existsById(id)) {
            orcamentoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}