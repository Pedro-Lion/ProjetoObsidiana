package com.example.crudObsidiana.service;

import com.example.crudObsidiana.dto.ServicoDTO;
import com.example.crudObsidiana.model.Equipamento;
import com.example.crudObsidiana.model.Servico;
import com.example.crudObsidiana.repository.EquipamentoRepository;
import com.example.crudObsidiana.repository.ServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class ServicoServiceTest {
    private ServicoRepository servicoRepository;
    private EquipamentoRepository equipamentoRepository;

    private ServicoService service;

    @BeforeEach
    void setUp() {

        servicoRepository = mock(ServicoRepository.class);
        equipamentoRepository = mock(EquipamentoRepository.class);

        service = new ServicoService(
                servicoRepository,
                equipamentoRepository
        );
    }

    @Test
    void deveCriarServicoComEquipamentos() {
        // DTO de entrada
        ServicoDTO dto = new ServicoDTO();
        dto.setNome("Corte de Piso");
        dto.setDescricao("Serviço especializado");
        dto.setHoras(8);
        dto.setValorPorHora(150);
        dto.setEquipamentosIds(Arrays.asList(1L, 2L));

        // Simulando equipamentos encontrados no banco
        Equipamento eq1 = new Equipamento();
        eq1.setId(1L);

        Equipamento eq2 = new Equipamento();
        eq2.setId(2L);

        List<Equipamento> equipamentos = Arrays.asList(eq1, eq2);

        when(equipamentoRepository.findAllById(dto.getEquipamentosIds()))
                .thenReturn(equipamentos);

        when(servicoRepository.save(any(Servico.class)))
                .thenAnswer(inv -> inv.getArgument(0)); // retorna o objeto salvo


        Servico servico = service.criarServico(dto);


        assertEquals("Corte de Piso", servico.getNome());
        assertEquals("Serviço especializado", servico.getDescricao());
        assertEquals(8, servico.getHoras());
        assertEquals(150.0, servico.getValorPorHora());
        assertEquals(2, servico.getEquipamentos().size());
        assertTrue(servico.getEquipamentos().contains(eq1));
        assertTrue(servico.getEquipamentos().contains(eq2));


        verify(equipamentoRepository, times(1))
                .findAllById(dto.getEquipamentosIds());

        verify(servicoRepository, times(1)).save(any(Servico.class));
    }
}
