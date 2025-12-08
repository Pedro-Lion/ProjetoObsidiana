package com.example.crudObsidiana.controller;

import com.example.crudObsidiana.dto.EquipamentoDTO;
import com.example.crudObsidiana.model.Equipamento;
import com.example.crudObsidiana.repository.EquipamentoRepository;
import com.example.crudObsidiana.service.EquipamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EquipamentoControllerTest {

    private EquipamentoRepository equipamentoRepository;
    private EquipamentoService equipamentoService;

    @BeforeEach
    void setUp() {
        equipamentoRepository = mock(EquipamentoRepository.class);
        equipamentoService = new EquipamentoService(equipamentoRepository);
    }

    @Test
    void deveCriarEquipamentoCorretamente() {
        // Arrange
        EquipamentoDTO dto = new EquipamentoDTO();
        dto.setNome("Furadeira");
        dto.setQuantidade(10);
        dto.setCategoria("Ferramenta");
        dto.setMarca("Bosch");
        dto.setNumeroSerie("ABC-123");
        dto.setModelo("X1000");
        dto.setValorPorHora(50.0);

        Equipamento equipamentoSalvo = new Equipamento();
        equipamentoSalvo.setId(1L);

        when(equipamentoRepository.save(any(Equipamento.class))).thenReturn(equipamentoSalvo);

        // Act
        Equipamento resultado = equipamentoService.criarEquipamento(dto);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());

        // Captura do parâmetro enviado ao save()
        ArgumentCaptor<Equipamento> captor = ArgumentCaptor.forClass(Equipamento.class);
        verify(equipamentoRepository).save(captor.capture());

        Equipamento capturado = captor.getValue();

        assertEquals("Furadeira", capturado.getNome());
        assertEquals(10, capturado.getQuantidade());
        assertEquals("Ferramenta", capturado.getCategoria());
        assertEquals("Bosch", capturado.getMarca());
        assertEquals("ABC-123", capturado.getNumeroSerie());
        assertEquals("X1000", capturado.getModelo());
        assertEquals(50.0, capturado.getValorPorHora());
        assertEquals(10, capturado.getQuantidadeDisponivel());
    }
}
