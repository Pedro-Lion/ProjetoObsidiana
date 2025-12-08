package com.example.crudObsidiana.service;

import com.example.crudObsidiana.dto.OrcamentoDTO;
import com.example.crudObsidiana.model.Orcamento;
import com.example.crudObsidiana.repository.OrcamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrcamentoServiceTest {

    private OrcamentoRepository orcamentoRepository;
    private OrcamentoService service;

    @BeforeEach
    void setUp() {
        orcamentoRepository = mock(OrcamentoRepository.class);

        service = new OrcamentoService(
                orcamentoRepository
        );
    }

    @Test
    void deveCriarOrcamento() {
        OrcamentoDTO dto = new OrcamentoDTO();
        dto.setDescricao("Evento teste");
        dto.setDataEvento(new Date());
        dto.setDuracaoEvento(5);
        dto.setLocalEvento("São Paulo");
        dto.setValorTotal(1200.50);
        dto.setStatus("ABERTO");

        when(orcamentoRepository.save(any(Orcamento.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Orcamento orc = service.criarOrcamento(dto);

        assertEquals("Evento teste", orc.getDescricao());
        assertEquals(5, orc.getDuracaoEvento());
        assertEquals("São Paulo", orc.getLocalEvento());
        assertEquals(1200.50, orc.getValorTotal());
        assertEquals("ABERTO", orc.getStatus());

        verify(orcamentoRepository, times(1)).save(any(Orcamento.class));
    }
}
