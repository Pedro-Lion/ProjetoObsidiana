package com.example.crudObsidiana.service;

import com.example.crudObsidiana.dto.OrcamentoDTO;
import com.example.crudObsidiana.model.Orcamento;
import com.example.crudObsidiana.repository.OrcamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrcamentoServiceTest {

    private OrcamentoRepository orcamentoRepository;
    private OrcamentoService service;

    @BeforeEach
    void setUp() {
        orcamentoRepository = mock(OrcamentoRepository.class);
        service = new OrcamentoService(orcamentoRepository);
    }

    @Test
    void deveCriarOrcamento() {
        // Arrange
        OrcamentoDTO dto = new OrcamentoDTO();
        dto.setDescricao("Evento teste");
        dto.setDataEvento(LocalDate.now());
        dto.setDuracaoEvento(5);
        dto.setLocalEvento("São Paulo");
        dto.setValorTotal(1200.50);
        dto.setStatus("ABERTO");

        when(orcamentoRepository.save(any(Orcamento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0)); // retorna o próprio objeto enviado

        // Act
        Orcamento orcamentoCriado = service.criarOrcamento(dto);

        // Assert
        assertNotNull(orcamentoCriado);
        assertEquals("Evento teste", orcamentoCriado.getDescricao());
        assertEquals(dto.getDataEvento(), orcamentoCriado.getDataEvento());
        assertEquals(5, orcamentoCriado.getDuracaoEvento());
        assertEquals("São Paulo", orcamentoCriado.getLocalEvento());
        assertEquals(1200.50, orcamentoCriado.getValorTotal());
        assertEquals("ABERTO", orcamentoCriado.getStatus());

        verify(orcamentoRepository, times(1)).save(any(Orcamento.class));
    }


    @Test
    void deveExcluirOrcamento() {
        Long id = 1L;

        doNothing().when(orcamentoRepository).deleteById(id);

        service.excluirOrcamento(id);

        verify(orcamentoRepository, times(1)).deleteById(id);
    }

    @Test
    void deveAtualizarOrcamento() {
        // Arrange
        Long id = 1L;

        Orcamento orcamentoExistente = new Orcamento();
        orcamentoExistente.setId(id);
        orcamentoExistente.setDescricao("Antigo");
        orcamentoExistente.setDataEvento(LocalDate.now().minusDays(1));
        orcamentoExistente.setDuracaoEvento(3);
        orcamentoExistente.setLocalEvento("Rio de Janeiro");
        orcamentoExistente.setValorTotal(800.00);
        orcamentoExistente.setStatus("PENDENTE");

        // DTO com novos dados
        OrcamentoDTO dto = new OrcamentoDTO();
        dto.setDescricao("Evento teste");
        dto.setDataEvento(LocalDate.now());
        dto.setDuracaoEvento(5);
        dto.setLocalEvento("São Paulo");
        dto.setValorTotal(1200.50);
        dto.setStatus("APROVADO");

        when(orcamentoRepository.findById(id)).thenReturn(Optional.of(orcamentoExistente));

        when(orcamentoRepository.save(any(Orcamento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Orcamento orcamentoAtualizado = service.atualizarOrcamento(id, dto);

        // Assert
        assertNotNull(orcamentoAtualizado);
        assertEquals("Evento teste", orcamentoAtualizado.getDescricao());
        assertEquals(dto.getDataEvento(), orcamentoAtualizado.getDataEvento());
        assertEquals(5, orcamentoAtualizado.getDuracaoEvento());
        assertEquals("São Paulo", orcamentoAtualizado.getLocalEvento());
        assertEquals(1200.50, orcamentoAtualizado.getValorTotal());
        assertEquals("APROVADO", orcamentoAtualizado.getStatus());

        verify(orcamentoRepository, times(1)).findById(id);
        verify(orcamentoRepository, times(1)).save(any(Orcamento.class));
    }

}
