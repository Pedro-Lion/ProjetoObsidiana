package com.example.crudObsidiana.service;

import com.example.crudObsidiana.dto.UsoEquipamentoDTO;
import com.example.crudObsidiana.model.Equipamento;
import com.example.crudObsidiana.model.Orcamento;
import com.example.crudObsidiana.model.UsoEquipamento;
import com.example.crudObsidiana.repository.EquipamentoRepository;
import com.example.crudObsidiana.repository.OrcamentoRepository;
import com.example.crudObsidiana.repository.ServicoRepository;
import com.example.crudObsidiana.repository.UsoEquipamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UsoEquipamentoServiceTest {

    private UsoEquipamentoRepository usoRepo;
    private EquipamentoRepository equipamentoRepo;
    private OrcamentoRepository orcRepo;
    private ServicoRepository servicoRepo;

    private UsoEquipamentoService service;

    @BeforeEach
    void setUp() {
        usoRepo = mock(UsoEquipamentoRepository.class);
        equipamentoRepo = mock(EquipamentoRepository.class);
        orcRepo = mock(OrcamentoRepository.class);
        servicoRepo = mock(ServicoRepository.class);

        service = new UsoEquipamentoService(
                usoRepo,
                equipamentoRepo,
                orcRepo,
                servicoRepo
        );
    }

    @Test
    void deveRegistrarUsoComOrcamento() {
        UsoEquipamentoDTO dto = new UsoEquipamentoDTO();
        dto.setIdEquipamento(1L);
        dto.setQuantidadeUsada(5);
        dto.setIdOrcamento(10L);
        dto.setIdServico(null);

        Equipamento eq = new Equipamento();
        eq.setId(1L);
        eq.setQuantidadeDisponivel(20);

        Orcamento orc = new Orcamento();
        orc.setId(10L);

        when(equipamentoRepo.findById(1L)).thenReturn(Optional.of(eq));
        when(orcRepo.findById(10L)).thenReturn(Optional.of(orc));
        when(usoRepo.save(any(UsoEquipamento.class))).thenAnswer(i -> i.getArgument(0));

        UsoEquipamento uso = service.registrarUso(dto);

        assertEquals(eq, uso.getEquipamento());
        assertEquals(orc, uso.getOrcamento());
        assertEquals(15, eq.getQuantidadeDisponivel());
    }


    @Test
    void deveRetirarUsoComOrcamento() {
        UsoEquipamentoDTO dto = new UsoEquipamentoDTO();
        dto.setIdEquipamento(1L);
        dto.setQuantidadeUsada(5);
        dto.setIdOrcamento(10L);
        dto.setIdServico(null);

        Equipamento eq = new Equipamento();
        eq.setId(1L);
        eq.setQuantidadeDisponivel(20);

        Orcamento orc = new Orcamento();
        orc.setId(10L);

        when(equipamentoRepo.findById(1L)).thenReturn(Optional.of(eq));
        when(orcRepo.findById(10L)).thenReturn(Optional.of(orc));
        when(usoRepo.save(any(UsoEquipamento.class))).thenAnswer(i -> i.getArgument(0));

        UsoEquipamento uso = service.registrarUso(dto);

        assertEquals(eq, uso.getEquipamento());
        assertEquals(orc, uso.getOrcamento());
        assertEquals(15, eq.getQuantidadeDisponivel());
    }

}
