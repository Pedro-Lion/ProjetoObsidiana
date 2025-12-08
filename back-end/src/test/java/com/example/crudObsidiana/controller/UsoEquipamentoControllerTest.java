package com.example.crudObsidiana.controller;

import com.example.crudObsidiana.dto.UsoEquipamentoDTO;
import com.example.crudObsidiana.model.UsoEquipamento;
import com.example.crudObsidiana.repository.UsoEquipamentoRepository;
import com.example.crudObsidiana.service.UsoEquipamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsoEquipamentoControllerTest {

    @InjectMocks
    private UsoEquipamentoController controller;

    @Mock
    private UsoEquipamentoService service;

    @Mock
    private UsoEquipamentoRepository repository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        controller = new UsoEquipamentoController(repository, service);
    }

    @Test
    void deveCriarUsoComSucesso() {
        UsoEquipamentoDTO dto = new UsoEquipamentoDTO();
        dto.setIdEquipamento(1L);
        dto.setIdServico(2L);
        dto.setQuantidadeUsada(5);

        UsoEquipamento salvo = new UsoEquipamento();
        salvo.setId(10L);

        when(service.registrarUso(dto)).thenReturn(salvo);

        ResponseEntity<UsoEquipamento> resposta = controller.criarUso(dto);

        assertEquals(201, resposta.getStatusCodeValue());
        assertEquals(10L, resposta.getBody().getId());
    }

    @Test
    void deveRetornarBadRequestQuandoServiceLancarExcecao() {
        UsoEquipamentoDTO dto = new UsoEquipamentoDTO();
        dto.setIdEquipamento(1L);
        dto.setQuantidadeUsada(5);

        when(service.registrarUso(dto)).thenThrow(new RuntimeException());

        ResponseEntity<UsoEquipamento> resposta = controller.criarUso(dto);

        assertEquals(400, resposta.getStatusCodeValue());
    }

    @Test
    void deveListarTodosUsos() {
        UsoEquipamento uso1 = new UsoEquipamento();
        UsoEquipamento uso2 = new UsoEquipamento();
        when(repository.findAll()).thenReturn(Arrays.asList(uso1, uso2));

        ResponseEntity<java.util.List<UsoEquipamento>> resposta = controller.listarTodos();

        assertEquals(200, resposta.getStatusCodeValue());
        assertEquals(2, resposta.getBody().size());
    }

    @Test
    void deveRecuperarUsoExistente() {
        UsoEquipamento uso = new UsoEquipamento();
        uso.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(uso));

        ResponseEntity<UsoEquipamento> resposta = controller.recuperar(1L);

        assertEquals(200, resposta.getStatusCodeValue());
        assertEquals(1L, resposta.getBody().getId());
    }

    @Test
    void deveRetornar404AoRecuperarIdInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<UsoEquipamento> resposta = controller.recuperar(99L);

        assertEquals(404, resposta.getStatusCodeValue());
        assertNull(resposta.getBody());
    }

    @Test
    void deveExcluirUsoExistente() {
        UsoEquipamento uso = new UsoEquipamento();
        uso.setId(1L);

        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(uso));

        ResponseEntity<UsoEquipamento> resposta = controller.excluir(1L);

        verify(repository, times(1)).deleteById(1L);
        assertEquals(200, resposta.getStatusCodeValue());
        assertEquals(1L, resposta.getBody().getId());
    }

    @Test
    void deveRetornar404AoExcluirUsoInexistente() {
        when(repository.existsById(99L)).thenReturn(false);

        ResponseEntity<UsoEquipamento> resposta = controller.excluir(99L);

        verify(repository, never()).deleteById(anyLong());
        assertEquals(404, resposta.getStatusCodeValue());
    }
}
