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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Serviços", description = "Operações relacionadas aos serviços")
@RestController
@RequestMapping("/api/servico")
public class ServicoController {

    private final ServicoRepository repository;

    // EntityManager para montar a query paginada com ORDER BY dinâmico (whitelist)
    // — Spring Data não aplica Sort automaticamente em @Query nativas (sobretudo com DISTINCT).
    @PersistenceContext
    private EntityManager entityManager;

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
    // Whitelist de campos ordenáveis → nome da COLUNA no banco (snake_case).
    // Obrigatório para evitar SQL injection no `ORDER BY ?#{#pageable}` da query nativa.
    private static final java.util.Map<String, String> COLUNAS_ORDENAVEIS = java.util.Map.of(
            "nome",         "nome",
            "valorPorHora", "valor_por_hora",
            "horas",        "horas"
    );

    @Operation(summary = "Lista serviços com paginação, busca e ordenação",
            description = "Retorna uma página de serviços. Parâmetros: 'page', 'size', 'busca', 'ordenarPor' (nome|valorPorHora|horas) e 'direcao' (asc|desc).")
    @ApiResponse(responseCode = "200", description = "Página de serviços retornada com sucesso")
    @GetMapping("/paginado")
    public ResponseEntity<Page<Servico>> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String busca,
            @RequestParam(defaultValue = "nome") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direcao) {
        // Resolve coluna SQL pela whitelist — único caminho de injeção possível, já bloqueado.
        String colunaSql = COLUNAS_ORDENAVEIS.getOrDefault(ordenarPor, "nome");
        String dir = "desc".equalsIgnoreCase(direcao) ? "DESC" : "ASC";

        // Mesma SQL da @Query findByBusca, mas montada aqui para conseguir ORDER BY dinâmico.
        // DISTINCT preserva 1 linha por serviço mesmo com vários equipamentos casando a busca.
        // Concatenação direta evita String.formatted/printf, que interpretaria os '%' do LIKE como diretivas.
        String sql = """
            SELECT DISTINCT s.* FROM servico s
            LEFT JOIN servico_equipamento se ON s.id = se.servico_id
            LEFT JOIN equipamento e ON se.equipamento_id = e.id
            WHERE LOWER(COALESCE(s.nome,      '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(s.descricao, '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR CAST(s.horas AS CHAR)             LIKE CONCAT('%', :busca, '%')
               OR CAST(COALESCE(s.valor_por_hora, 0) AS CHAR) LIKE CONCAT('%', :busca, '%')
               OR LOWER(COALESCE(e.nome,      '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(e.categoria, '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(e.marca,     '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(e.modelo,    '')) LIKE LOWER(CONCAT('%', :busca, '%'))
            """ + " ORDER BY " + colunaSql + " " + dir;

        String countSql = """
            SELECT COUNT(DISTINCT s.id) FROM servico s
            LEFT JOIN servico_equipamento se ON s.id = se.servico_id
            LEFT JOIN equipamento e ON se.equipamento_id = e.id
            WHERE LOWER(COALESCE(s.nome,      '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(s.descricao, '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR CAST(s.horas AS CHAR)             LIKE CONCAT('%', :busca, '%')
               OR CAST(COALESCE(s.valor_por_hora, 0) AS CHAR) LIKE CONCAT('%', :busca, '%')
               OR LOWER(COALESCE(e.nome,      '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(e.categoria, '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(e.marca,     '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(e.modelo,    '')) LIKE LOWER(CONCAT('%', :busca, '%'))
            """;

        Pageable pageable = PageRequest.of(page, size);

        @SuppressWarnings("unchecked")
        List<Servico> conteudo = entityManager.createNativeQuery(sql, Servico.class)
                .setParameter("busca", busca)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long total = ((Number) entityManager.createNativeQuery(countSql)
                .setParameter("busca", busca)
                .getSingleResult()).longValue();

        return ResponseEntity.ok(new PageImpl<>(conteudo, pageable, total));
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