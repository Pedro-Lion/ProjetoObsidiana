package com.example.crudObsidiana.controller;

import com.example.crudObsidiana.dto.ProfissionalDTO;
import com.example.crudObsidiana.model.Profissional;
import com.example.crudObsidiana.repository.ProfissionalRepository;
import com.example.crudObsidiana.service.FileStorageService;
import com.example.crudObsidiana.service.ProfissionalService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Tag(name = "Profissionais", description = "Operações relacionadas aos profissionais")
@RestController
@RequestMapping("/api/profissional")
public class ProfissionalController {

    private final ProfissionalRepository repository;
    private final FileStorageService fileStorageService;

    // EntityManager para montar a query paginada com ORDER BY dinâmico (whitelist)
    // — Spring Data não aplica Sort automaticamente em @Query nativas.
    @PersistenceContext
    private EntityManager entityManager;

    public ProfissionalController(ProfissionalRepository repository, FileStorageService fileStorageService) {
        this.repository = repository;
        this.fileStorageService = fileStorageService;
    }

    @Autowired
    private ProfissionalService profissionalService;

    // ---------------------------------------------------------------
    // GET /profissional — listagem completa (compatibilidade legada)
    // ---------------------------------------------------------------
    @Operation(summary = "Lista todos os profissionais (sem paginação)")
    @ApiResponse(responseCode = "200", description = "Lista de profissional retornada com sucesso",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Profissional.class))))
    @GetMapping
    public List<Profissional> listarTodos() {
        return repository.findAll();
    }

    // ---------------------------------------------------------------
    // GET /profissional/paginado?page=0&size=10
    // ---------------------------------------------------------------
    // Whitelist de campos ordenáveis → nome da COLUNA no banco (snake_case).
    // Obrigatório para evitar SQL injection no `ORDER BY ?#{#pageable}` da query nativa.
    private static final java.util.Map<String, String> COLUNAS_ORDENAVEIS = java.util.Map.of(
            "nome",      "nome",
            "categoria", "categoria"
    );

    @Operation(summary = "Lista profissionais com paginação, busca e ordenação",
            description = "Retorna uma página de profissionais. Parâmetros: 'page', 'size', 'busca', 'ordenarPor' (nome|categoria) e 'direcao' (asc|desc).")
    @ApiResponse(responseCode = "200", description = "Página de profissionais retornada com sucesso")
    @GetMapping("/paginado")
    public ResponseEntity<Page<Profissional>> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String busca,
            @RequestParam(defaultValue = "nome") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direcao) {
        // Resolve coluna SQL pela whitelist — único caminho de injeção possível, já bloqueado.
        String colunaSql = COLUNAS_ORDENAVEIS.getOrDefault(ordenarPor, "nome");
        String dir = "desc".equalsIgnoreCase(direcao) ? "DESC" : "ASC";

        // Mesma SQL da @Query findByBusca, mas montada aqui para conseguir ORDER BY dinâmico.
        // Concatenação direta evita String.formatted/printf, que interpretaria os '%' do LIKE como diretivas.
        String sql = """
            SELECT * FROM profissional p
            WHERE LOWER(COALESCE(p.nome,            '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(p.disponibilidade, '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(p.contato,         '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(p.categoria,       '')) LIKE LOWER(CONCAT('%', :busca, '%'))
            """ + " ORDER BY " + colunaSql + " " + dir;

        String countSql = """
            SELECT COUNT(*) FROM profissional p
            WHERE LOWER(COALESCE(p.nome,            '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(p.disponibilidade, '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(p.contato,         '')) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(COALESCE(p.categoria,       '')) LIKE LOWER(CONCAT('%', :busca, '%'))
            """;

        Pageable pageable = PageRequest.of(page, size);

        @SuppressWarnings("unchecked")
        List<Profissional> conteudo = entityManager.createNativeQuery(sql, Profissional.class)
                .setParameter("busca", busca)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long total = ((Number) entityManager.createNativeQuery(countSql)
                .setParameter("busca", busca)
                .getSingleResult()).longValue();

        return ResponseEntity.ok(new PageImpl<>(conteudo, pageable, total));
    }

    @Operation(summary = "Recupera um profissional pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profissional encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Profissional.class))),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Profissional> recuperar(@PathVariable("id") Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Cadastra um novo profissional")
    @ApiResponse(responseCode = "201", description = "Profissional criado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Profissional.class)))
    @PostMapping
    public ResponseEntity<Profissional> create(@RequestBody ProfissionalDTO dto) {
        Profissional profissionalCriado = profissionalService.criarProfissional(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(profissionalCriado);
    }

    @Operation(summary = "Remove um profissional pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profissional removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Atualiza dados de um profissional existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profissional atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Profissional.class))),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Profissional> atualizar(@PathVariable("id") Long id, @RequestBody Profissional profissional) {
        // Update parcial: só sobrescreve os campos que vieram não-nulos no body.
        // Importante: NÃO mexe em nomeArquivoImagem/tipoImagem/caminhoImagem aqui —
        // esses campos são exclusivamente gravados pelo endpoint POST /{id}/imagem.
        // Substituir a entidade inteira (save(profissional) direto) zerava as referências
        // de imagem ao editar dados textuais do profissional.
        return repository.findById(id).map(existente -> {
            if (profissional.getNome() != null) existente.setNome(profissional.getNome());
            if (profissional.getDisponibilidade() != null) existente.setDisponibilidade(profissional.getDisponibilidade());
            if (profissional.getContato() != null) existente.setContato(profissional.getContato());
            if (profissional.getCategoria() != null) existente.setCategoria(profissional.getCategoria());
            Profissional salvo = repository.save(existente);
            return ResponseEntity.ok(salvo);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/imagem")
    public ResponseEntity<?> uploadImagemEquipamento(@PathVariable Long id,
                                                     @RequestParam("arquivo") MultipartFile arquivo) {
        try {
            if (!repository.existsById(id)) return ResponseEntity.notFound().build();
            Path caminho = fileStorageService.salvarArquivo(arquivo);
            Profissional profissional = repository.findById(id).get();
            profissional.setNomeArquivoImagem(caminho.getFileName().toString());
            profissional.setTipoImagem(arquivo.getContentType());
            profissional.setCaminhoImagem(caminho.toString());
            repository.save(profissional);
            return ResponseEntity.ok(profissional);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Erro ao salvar arquivo: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/imagem")
    public ResponseEntity<byte[]> baixarImagemEquipamento(@PathVariable Long id) {
        java.util.Optional<Profissional> opt = repository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Profissional prof = opt.get();
        String nomeArquivo = prof.getNomeArquivoImagem();
        if (nomeArquivo == null || nomeArquivo.isEmpty()) return ResponseEntity.notFound().build();
        try {
            byte[] dados = fileStorageService.lerArquivo(nomeArquivo);
            String tipo = prof.getTipoImagem();
            if (tipo == null || tipo.isEmpty()) tipo = org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + nomeArquivo + "\"")
                    .contentType(org.springframework.http.MediaType.parseMediaType(tipo))
                    .body(dados);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}