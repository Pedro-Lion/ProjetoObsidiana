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
        dto.setNome("Furadeira Industrial");
        dto.setQuantidade(10);
        dto.setCategoria("Ferramenta Elétrica");
        dto.setMarca("Bosch");
        dto.setNumeroSerie("ABC12345");
        dto.setModelo("X2000");
        dto.setValorPorHora(55.90);

        when(equipamentoRepository.save(any(Equipamento.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Equipamento equipamento = service.criarEquipamento(dto);

        assertEquals("Furadeira Industrial", equipamento.getNome());
        assertEquals(10, equipamento.getQuantidade());
        assertEquals("Ferramenta Elétrica", equipamento.getCategoria());
        assertEquals("Bosch", equipamento.getMarca());
        assertEquals("ABC12345", equipamento.getNumeroSerie());
        assertEquals("X2000", equipamento.getModelo());
        assertEquals(55.90, equipamento.getValorPorHora());
        assertEquals(10, equipamento.getQuantidadeDisponivel()); // Setado igual ao quantidade!

        verify(equipamentoRepository, times(1)).save(any(Equipamento.class));
    }
}
