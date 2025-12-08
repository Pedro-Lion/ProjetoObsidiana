package com.example.crudObsidiana.controller;

import com.example.crudObsidiana.dto.OrcamentoDTO;
import com.example.crudObsidiana.model.Orcamento;
import com.example.crudObsidiana.repository.OrcamentoRepository;
import com.example.crudObsidiana.service.OrcamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrcamentoControllerTest {

    private OrcamentoRepository orcamentoRepository;
    private OrcamentoService orcamentoService;

    @BeforeEach
    void setUp() {
        orcamentoRepository = mock(OrcamentoRepository.class);
        orcamentoService = new OrcamentoService(orcamentoRepository);
    }

    @Test
    void deveCriarOrcamentoCorretamente() {
        // Arrange
        OrcamentoDTO dto = new OrcamentoDTO();
        dto.setDescricao("Casamento");
        dto.setDataEvento(LocalDate.of(2025, 5, 10));
        dto.setDuracaoEvento(8);
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
        assertEquals(LocalDate.of(2025, 5, 10), capturado.getDataEvento());
        assertEquals(8, capturado.getDuracaoEvento());
        assertEquals("São Paulo", capturado.getLocalEvento());
        assertEquals(12000.0, capturado.getValorTotal());
        assertEquals("ABERTO", capturado.getStatus());
    }
}
