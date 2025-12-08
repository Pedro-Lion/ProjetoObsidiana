package com.example.crudObsidiana.service;

import com.example.crudObsidiana.dto.EquipamentoDTO;
import com.example.crudObsidiana.model.Equipamento;
import com.example.crudObsidiana.repository.EquipamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EquipamentoServiceTest {

    private EquipamentoRepository equipamentoRepository;
    private EquipamentoService service;

    @BeforeEach
    void setUp() {
        equipamentoRepository = mock(EquipamentoRepository.class);

        service = new EquipamentoService(
                equipamentoRepository
        );
    }

    @Test
    void deveCriarEquipamento() {
        EquipamentoDTO dto = new EquipamentoDTO();
        dto.setNome("Câmera Canon EOS R6");
        dto.setQuantidadeTotal(5);
        dto.setCategoria("Câmeras");
        dto.setMarca("Canon");
        dto.setNumeroSerie("CN123456");
        dto.setModelo("EOS R6");
        dto.setValorPorHora(250.0);

        when(equipamentoRepository.save(any(Equipamento.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Equipamento equipamento = service.criarEquipamento(dto);

        assertEquals("Câmera Canon EOS R6", equipamento.getNome());
        assertEquals(5, equipamento.getQuantidadeTotal());
        assertEquals("Câmeras", equipamento.getCategoria());
        assertEquals("Canon", equipamento.getMarca());
        assertEquals("CN123456", equipamento.getNumeroSerie());
        assertEquals("EOS R6", equipamento.getModelo());
        assertEquals(250.0, equipamento.getValorPorHora());
        assertEquals(5, equipamento.getQuantidadeDisponivel()); // quantidadeDisponivel = quantidadeTotal na criação

        verify(equipamentoRepository, times(1)).save(any(Equipamento.class));
    }
}
