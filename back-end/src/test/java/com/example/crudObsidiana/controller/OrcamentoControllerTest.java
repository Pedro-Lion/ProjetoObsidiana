package com.example.crudObsidiana.controller;

import com.example.crudObsidiana.dto.OrcamentoDTO;
import com.example.crudObsidiana.model.Orcamento;
import com.example.crudObsidiana.repository.*;
import com.example.crudObsidiana.observer.OrcamentoObserver;
import com.example.crudObsidiana.service.OrcamentoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrcamentoControllerTest {

    private OrcamentoRepository orcamentoRepository;
    private ServicoRepository servicoRepository;
    private EquipamentoRepository equipamentoRepository;
    private ProfissionalRepository profissionalRepository;
    private UsoEquipamentoRepository usoEquipamentoRepository;

    private OrcamentoService orcamentoService;

    @BeforeEach
    void setUp() {

        // --- Mocks dos repositórios usados no service ---
        orcamentoRepository = mock(OrcamentoRepository.class);
        servicoRepository = mock(ServicoRepository.class);
        equipamentoRepository = mock(EquipamentoRepository.class);
        profissionalRepository = mock(ProfissionalRepository.class);
        usoEquipamentoRepository = mock(UsoEquipamentoRepository.class);

        // --- Lista vazia de observers (para passar no construtor) ---
        List<OrcamentoObserver> observers = new ArrayList<>();

        // --- Instancia o service com o construtor atualizado ---
        orcamentoService = new OrcamentoService(orcamentoRepository, observers);

        // --- Injeta manualmente os @Autowired (pois não existe Spring no teste) ---
        // (Sim, isso funciona pois os campos não são final)
        setField(orcamentoService, "servicoRepository", servicoRepository);
        setField(orcamentoService, "equipamentoRepository", equipamentoRepository);
        setField(orcamentoService, "profissionalRepository", profissionalRepository);
        setField(orcamentoService, "usoEquipamentoRepository", usoEquipamentoRepository);
    }

    // Método utilitário para injetar dependências privadas
    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deveCriarOrcamentoCorretamente() {
        // Arrange
        OrcamentoDTO dto = new OrcamentoDTO();
        dto.setDescricao("Casamento");

        dto.setDataInicio(Date.from(LocalDate.of(2025, 5, 10)
                .atStartOfDay(ZoneId.systemDefault()).toInstant()));

        dto.setDataTermino(Date.from(LocalDate.of(2025, 5, 10)
                .plusYears(8)
                .atStartOfDay(ZoneId.systemDefault()).toInstant()));

        dto.setLocalEvento("São Paulo");
        dto.setValorTotal(12000.0);
        dto.setStatus("ABERTO");

        Orcamento salvo = new Orcamento();
        salvo.setId(1L);

        when(orcamentoRepository.save(any(Orcamento.class))).thenReturn(salvo);

        // Act
        Orcamento resultado = orcamentoService.criarOrcamento(dto);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());

        // Captura do objeto enviado ao save()
        ArgumentCaptor<Orcamento> captor = ArgumentCaptor.forClass(Orcamento.class);
        verify(orcamentoRepository).save(captor.capture());

        Orcamento capturado = captor.getValue();

        assertEquals("Casamento", capturado.getDescricao());
        assertEquals(dto.getDataInicio(), capturado.getDataInicio());
        assertEquals(dto.getDataTermino(), capturado.getDataTermino());
        assertEquals("São Paulo", capturado.getLocalEvento());
        assertEquals("ABERTO", capturado.getStatus());
    }
}
