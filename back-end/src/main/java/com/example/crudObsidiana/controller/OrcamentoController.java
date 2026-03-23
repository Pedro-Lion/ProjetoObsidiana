package com.example.crudObsidiana.controller;

import com.example.crudObsidiana.dto.KpisOrcamentoDTO;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orcamento")
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

    @Autowired
    private OrcamentoService orcamentoService;
    @Autowired
    private OrcamentoRepository orcamentoRepository;


    // Classe auxiliar para requisições de atualização de status
    public static class AtualizarStatusRequest {
        @Schema(description = "Novo status do orçamento", example = "Confirmado")
        private String status;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }



    // ----------------------------------------------------------------------
    // POST /orcamento → Criar um orçamento (sempre começa em "Em análise")
    // ----------------------------------------------------------------------
    @PostMapping
    @Operation(
            summary = "Criar um novo orçamento",
            description = """
                    Cria um orçamento com status inicial 'Em análise'.
                    
                    Observação importante:
                    - Mesmo que o DTO enviado possua um status,
                      o sistema irá sobrescrever para 'Em análise'.
                    - O estoque de equipamentos NÃO é atualizado nesta etapa.
                    - O controle de estoque só ocorre ao alterar o status via
                      PUT /orcamento/{id}/status.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Orçamento criado com sucesso",
                    content = @Content(schema = @Schema(implementation = Orcamento.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos enviados")
    })
    public ResponseEntity<Orcamento> criarOrcamento(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados para criação do orçamento",
                    required = true,
                    content = @Content(schema = @Schema(implementation = OrcamentoDTO.class))
            )
            @RequestBody OrcamentoDTO dto
    ) {
        Orcamento criado = orcamentoService.criarOrcamento(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }



    // ----------------------------------------------------------------------
    // GET /orcamento → Listar todos
    // ----------------------------------------------------------------------
    @GetMapping
    @Operation(
            summary = "Listar todos os orçamentos",
            description = "Retorna a lista completa de orçamentos cadastrados no sistema."
    )
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<Orcamento>> listarTodos() {
        return ResponseEntity.ok(orcamentoService.listarTodos());
    }

    // ----------------------------------------------------------------------
    // GET /orcamento/{id} → Buscar por ID
    // ----------------------------------------------------------------------
    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar orçamento por ID",
            description = "Retorna os detalhes de um orçamento específico."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orçamento encontrado"),
            @ApiResponse(responseCode = "404", description = "Orçamento não encontrado")
    })
    public ResponseEntity<Orcamento> buscarPorId(
            @Parameter(description = "ID do orçamento", example = "1")
            @PathVariable Long id
    ) {
        Optional<Orcamento> opt = orcamentoRepository.findById(id);
        return opt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // ----------------------------------------------------------------------
    // GET /orcamento/calendar/{idCalendar} → Buscar por IDCalendar
    // ----------------------------------------------------------------------
    @GetMapping("/calendar/{idCalendar}")
    @Operation(
            summary = "Buscar orçamento por ID do Calendário Outlook",
            description = "Retorna os detalhes de um orçamento específico."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orçamento encontrado"),
            @ApiResponse(responseCode = "404", description = "Orçamento não encontrado")
    })
    public ResponseEntity<Orcamento> buscarPorIdCalendar(
            @Parameter(description = "IDCalendar do orçamento", example = "ASAICuyvcaUICya234...")
            @PathVariable String idCalendar
    ) {
        Optional<Orcamento> opt = orcamentoRepository.findByIdCalendar(idCalendar);
        return opt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // ----------------------------------------------------------------------
    // KPIs
    // ----------------------------------------------------------------------
    @Operation(summary = "Retorna as KPIs dos orçamentos (aprovados, pendentes e concluídos)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "KPIs retornadas com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = KpisOrcamentoDTO.class)))
    })
    @GetMapping("/kpis")
    public ResponseEntity<KpisOrcamentoDTO> getKpis() {
        return ResponseEntity.ok(orcamentoService.getKpis());
    }

    // ----------------------------------------------------------------------
    // PUT /orcamento/{id} → Atualizar orcamento
    // ----------------------------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<Orcamento> atualizarOrcamento(@PathVariable("id") Long id, @RequestBody OrcamentoDTO dto) {
        try {
            Orcamento atualizado = orcamentoService.editarOrcamento(id, dto);
            return ResponseEntity.ok(atualizado);
        } catch (ResponseStatusException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else if (ex.getStatusCode() == HttpStatus.CONFLICT) {
                // opcional: retornar mensagem ou corpo com detalhes
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.status(ex.getStatusCode()).build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }

    // ----------------------------------------------------------------------
    // DELETE /orcamento/{id} → Excluir orçamento
    // ----------------------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {
        if (orcamentoRepository.existsById(id)) {
            orcamentoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

}// FIM CLASSE