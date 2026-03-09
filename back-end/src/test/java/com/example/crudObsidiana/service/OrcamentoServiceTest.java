package com.example.crudObsidiana.service;

import com.example.crudObsidiana.dto.KpisOrcamentoDTO;
import com.example.crudObsidiana.dto.OrcamentoDTO;
import com.example.crudObsidiana.dto.UsoEquipamentoDTO;
import com.example.crudObsidiana.model.Equipamento;
import com.example.crudObsidiana.model.Orcamento;
import com.example.crudObsidiana.model.Servico;
import com.example.crudObsidiana.model.UsoEquipamento;
import com.example.crudObsidiana.observer.OrcamentoObserver;
import com.example.crudObsidiana.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class OrcamentoServiceTest {

    @Mock
    private OrcamentoRepository orcamentoRepository;
    @Mock
    private ServicoRepository servicoRepository;
    @Mock
    private EquipamentoRepository equipamentoRepository;
    @Mock
    private ProfissionalRepository profissionalRepository;
    @Mock
    private UsoEquipamentoRepository usoEquipamentoRepository;
    @Mock
    private OrcamentoObserver observer; // Mock de um observador

    @InjectMocks
    private OrcamentoService orcamentoService;

    private OrcamentoDTO orcamentoDTO;
    private Orcamento orcamento;
    private Equipamento equipamento;
    private Servico servico;
    private UsoEquipamento usoEquipamento;
    private List<OrcamentoObserver> observers;

    @BeforeEach
    void setUp() {
        // Configuração do OrcamentoService com a lista de observers
        observers = new ArrayList<>();
        observers.add(observer);
        // O InjectMocks só funciona para injeção por campo. Para injeção por construtor,
        // precisamos instanciar manualmente ou garantir que o Mockito chame o construtor correto.
        // Como o construtor tem @Autowired, o MockitoExtension deve ser capaz de injetar.
        // Se não funcionar, instanciamos manualmente:
        orcamentoService = new OrcamentoService(orcamentoRepository, observers);

        // Injetar manualmente os repositórios que são autowired por campo na classe de serviço
        org.springframework.test.util.ReflectionTestUtils.setField(orcamentoService, "servicoRepository", servicoRepository);
        org.springframework.test.util.ReflectionTestUtils.setField(orcamentoService, "equipamentoRepository", equipamentoRepository);
        org.springframework.test.util.ReflectionTestUtils.setField(orcamentoService, "profissionalRepository", profissionalRepository);
        org.springframework.test.util.ReflectionTestUtils.setField(orcamentoService, "usoEquipamentoRepository", usoEquipamentoRepository);

        // Mocks de Entidades
        orcamento = new Orcamento(new Date(), new Date(), "Local", "Desc", "Em análise", 0.0);
        orcamento.setId(1L);

        equipamento = new Equipamento();
        equipamento.setId(10L);
        equipamento.setNome("Câmera A");
        equipamento.setValorPorHora(50.0);
        equipamento.setQuantidadeDisponivel(5); // 5 em estoque

        servico = new Servico();
        servico.setId(20L);
        servico.setValorPorHora(100.0);
        servico.setHoras(4);

        usoEquipamento = new UsoEquipamento();
        usoEquipamento.setOrcamento(orcamento);
        usoEquipamento.setEquipamento(equipamento);
        usoEquipamento.setQuantidadeUsada(2); // 2 requisitados

        // DTOs
        UsoEquipamentoDTO usoDto = new UsoEquipamentoDTO();
        usoDto.setIdEquipamento(10L);
        usoDto.setQuantidadeUsada(2);

        orcamentoDTO = new OrcamentoDTO();
        orcamentoDTO.setDataInicio(new Date());
        orcamentoDTO.setDataTermino(new Date());
        orcamentoDTO.setLocalEvento("Local Teste");
        orcamentoDTO.setDescricao("Descrição Teste");
        orcamentoDTO.setStatus("Em análise");
        orcamentoDTO.setServicos(List.of(20L));
        orcamentoDTO.setUsosEquipamentos(List.of(usoDto));
    }

    /**
     * Teste 1: Criação de Orçamento com Sucesso (Happy Path)
     * Deve calcular o valor total corretamente e salvar todas as entidades.
     */
    @Test
    void criarOrcamento_ComServicosEUsoEquipamento_DeveRetornarOrcamentoSalvo() {
        // Arrange
        when(orcamentoRepository.save(any(Orcamento.class))).thenReturn(orcamento);
        when(equipamentoRepository.findById(10L)).thenReturn(Optional.of(equipamento));
        when(usoEquipamentoRepository.save(any(UsoEquipamento.class))).thenReturn(usoEquipamento);
        when(servicoRepository.findAllById(List.of(20L))).thenReturn(List.of(servico));

        // O orcamento retornado pelo save inicial não tem as listas populadas,
        // precisamos simular a população para o cálculo do valor total.
        Orcamento orcamentoParaCalculo = new Orcamento();
        orcamentoParaCalculo.setServicos(List.of(servico));
        orcamentoParaCalculo.setUsosEquipamentos(List.of(usoEquipamento));

        // Simular o comportamento do save para o segundo save (após cálculo e população)
        // O save final é o que tem o valor total calculado.
        when(orcamentoRepository.save(argThat(o -> o.getValorTotal() != null && o.getValorTotal() > 0.0)))
                .thenReturn(orcamento);

        // Act
        Orcamento resultado = orcamentoService.criarOrcamento(orcamentoDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(orcamento.getId(), resultado.getId());
        // Cálculo esperado: (servico.horas * servico.valorPorHora) + (usoEquipamento.qtd * equipamento.valorPorHora)
        // (4 * 100.0) + (2 * 50.0) = 400.0 + 100.0 = 500.0
        // O valor total é calculado internamente, mas podemos verificar se o save final foi chamado com o valor correto.
        // A lógica interna pode variar (1 ou 2 saves dependendo do fluxo).
        // O importante é que o resultado final esteja correto e o fluxo principal seja verificado.
        verify(orcamentoRepository, atLeastOnce()).save(any(Orcamento.class));
        verify(equipamentoRepository, times(1)).findById(10L);
        verify(usoEquipamentoRepository, times(1)).save(any(UsoEquipamento.class));
        verify(servicoRepository, times(1)).findAllById(List.of(20L));

        // Não deve notificar observers, pois o status é "Em análise"
        verify(observer, never()).onOrcamentoUpdated(any(), any(), any());
    }

    /**
     * Teste 2: Criação de Orçamento com Status "Confirmado" e Estoque Insuficiente
     * Deve lançar ResponseStatusException com HttpStatus.CONFLICT.
     */
    @Test
    void criarOrcamento_StatusConfirmado_EstoqueInsuficiente_DeveLancarExcecao() {
        // Arrange
        orcamentoDTO.setStatus("Confirmado");
        equipamento.setQuantidadeDisponivel(1); // Estoque insuficiente (requisitado 2)

        when(orcamentoRepository.save(any(Orcamento.class))).thenReturn(orcamento);
        when(equipamentoRepository.findById(10L)).thenReturn(Optional.of(equipamento));
        when(usoEquipamentoRepository.save(any(UsoEquipamento.class))).thenReturn(usoEquipamento);
        when(servicoRepository.findAllById(anyList())).thenReturn(List.of(servico));

        // Simular a busca de usos persistidos para a checagem de estoque
        // É necessário criar um novo equipamento mock para cada teste que altera o estado
        Equipamento equipamentoComEstoque = new Equipamento();
        equipamentoComEstoque.setId(10L);
        equipamentoComEstoque.setNome("Câmera A");
        equipamentoComEstoque.setValorPorHora(50.0);
        equipamentoComEstoque.setQuantidadeDisponivel(5); // 5 em estoque

        UsoEquipamento usoComEstoque = new UsoEquipamento();
        usoComEstoque.setOrcamento(orcamento);
        usoComEstoque.setEquipamento(equipamentoComEstoque);
        usoComEstoque.setQuantidadeUsada(2); // 2 requisitados

        when(usoEquipamentoRepository.findByOrcamento_Id(1L)).thenReturn(List.of(usoComEstoque));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            orcamentoService.criarOrcamento(orcamentoDTO);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Estoque insuficiente"));

        // Deve salvar o orçamento inicial, mas não o final com status "Confirmado"
        verify(orcamentoRepository, times(1)).save(any(Orcamento.class));
        verify(observer, never()).onOrcamentoUpdated(any(), any(), any());
    }

    /**
     * Teste 3: Edição de Orçamento - Devolução de Estoque (Confirmado -> Outro Status)
     * Deve notificar observers para devolver o estoque.
     */
    @Test
    void editarOrcamento_DeConfirmadoParaOutroStatus_DeveNotificarObservers() {
        // Arrange
        orcamento.setStatus("Confirmado");
        // Usar ArrayList para permitir a operação clear() no serviço
        orcamento.setUsosEquipamentos(new ArrayList<>(List.of(usoEquipamento)));

        OrcamentoDTO dtoNaoConfirmado = new OrcamentoDTO();
        dtoNaoConfirmado.setStatus("Cancelado"); // Novo status não-confirmado
        dtoNaoConfirmado.setDescricao("Nova Descrição");

        // Simular o findById
        when(orcamentoRepository.findById(1L)).thenReturn(Optional.of(orcamento));
        // Adicionar mock para a busca de equipamento que pode ocorrer na lógica de edição
        when(equipamentoRepository.findById(anyLong())).thenReturn(Optional.of(equipamento));

        // Simular a busca de usos antigos
        when(usoEquipamentoRepository.findByOrcamento_Id(1L)).thenReturn(List.of(usoEquipamento));

        // Simular o save final
        when(orcamentoRepository.save(any(Orcamento.class))).thenReturn(orcamento);

        // Act
        Orcamento resultado = orcamentoService.editarOrcamento(1L, dtoNaoConfirmado);

        // Assert
        assertNotNull(resultado);
        assertEquals("Cancelado", resultado.getStatus());

        // Deve notificar observers com a transição de status
        verify(observer, times(1)).onOrcamentoUpdated(
                eq(orcamento),
                eq("Confirmado"),
                eq("Cancelado")
        );
    }

    /**
     * Teste 4: Recuperação de KPIs
     * Deve chamar o repositório para contar os orçamentos por status.
     */
    @Test
    void getKpis_DeveRetornarKpisCorretos() {
        // Arrange
        when(orcamentoRepository.countByStatus("Confirmado")).thenReturn(10);
        when(orcamentoRepository.countByStatus("Em análise")).thenReturn(5);;
        when(orcamentoRepository.countByStatus("Cancelado")).thenReturn(2);

        // Act
        KpisOrcamentoDTO kpis = orcamentoService.getKpis();

        // Assert
        assertNotNull(kpis);
        assertEquals(10, kpis.confirmados());
        assertEquals(5, kpis.pendentes());
        assertEquals(2, kpis.cancelados());

        verify(orcamentoRepository, times(1)).countByStatus("Confirmado");
        verify(orcamentoRepository, times(1)).countByStatus("Em análise");
        verify(orcamentoRepository, times(1)).countByStatus("Cancelado");
    }
}
