package com.example.crudObsidiana.controller;

import com.example.crudObsidiana.dto.ServicoDTO;
import com.example.crudObsidiana.model.Servico;
import com.example.crudObsidiana.repository.ServicoRepository;
import com.example.crudObsidiana.service.ServicoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

class ServicoControllerTest {

    private ServicoRepository repository;
    private ServicoService servicoService;
    private ServicoController controller;



    @BeforeEach
    void setUp() {
        repository = mock(ServicoRepository.class);
        servicoService = mock(ServicoService.class);

        controller = new ServicoController(repository);

        // Injeta o serviço correto no campo private
        ReflectionTestUtils.setField(controller, "servicoService", servicoService);
    }

    // -------------------------
    // TESTE: Criar serviço
    // -------------------------
    @Test
    void deveCriarServico() {
        ServicoDTO dto = new ServicoDTO();
        dto.setDescricao("Filmagem");

        Servico salvo = new Servico();
        salvo.setId(1L);
        salvo.setDescricao("Filmagem");

        when(servicoService.criarServico(dto)).thenReturn(salvo);

        ResponseEntity<Servico> response = controller.criarServico(dto);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    // -------------------------
    // TESTE: Listar todos
    // -------------------------
    @Test
    void deveListarTodos() {
        Servico s1 = new Servico();
        s1.setId(1L);

        Servico s2 = new Servico();
        s2.setId(2L);

        when(repository.findAll()).thenReturn(Arrays.asList(s1, s2));

        var result = controller.listarTodos();

        assertEquals(2, result.size());
    }

    // -------------------------
    // TESTE: Recuperar por ID - encontrado
    // -------------------------
    @Test
    void deveRecuperarPorIdEncontrado() {
        Servico servico = new Servico();
        servico.setId(10L);

        when(repository.findById(10L)).thenReturn(Optional.of(servico));

        ResponseEntity<Servico> response = controller.recuperar(10L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(10L, response.getBody().getId());
    }

    // -------------------------
    // TESTE: Recuperar por ID - não encontrado
    // -------------------------
    @Test
    void deveRetornar404AoBuscarInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Servico> response = controller.recuperar(99L);

        assertEquals(404, response.getStatusCodeValue());
    }

    // -------------------------
    // TESTE: Excluir - encontrado
    // -------------------------
    @Test
    void deveExcluirServico() {
        when(repository.existsById(5L)).thenReturn(true);

        ResponseEntity<Void> response = controller.excluir(5L);

        assertEquals(200, response.getStatusCodeValue());
        verify(repository, times(1)).deleteById(5L);
    }

    // -------------------------
    // TESTE: Excluir - não encontrado
    // -------------------------
    @Test
    void naoDeveExcluirServicoInexistente() {
        when(repository.existsById(123L)).thenReturn(false);

        ResponseEntity<Void> response = controller.excluir(123L);

        assertEquals(404, response.getStatusCodeValue());
        verify(repository, never()).deleteById(anyLong());
    }

    // -------------------------
    // TESTE: Atualizar - encontrado
    // -------------------------
    @Test
    void deveAtualizarServico() {
        Servico servico = new Servico();
        servico.setDescricao("Atualizado");

        when(repository.existsById(50L)).thenReturn(true);

        ResponseEntity<Servico> response = controller.atualizar(50L, servico);

        assertEquals(200, response.getStatusCodeValue());

        ArgumentCaptor<Servico> captor = ArgumentCaptor.forClass(Servico.class);
        verify(repository).save(captor.capture());

        Servico capturado = captor.getValue();

        assertEquals(50L, capturado.getId());
        assertEquals("Atualizado", capturado.getDescricao());
    }

    // -------------------------
    // TESTE: Atualizar - não encontrado
    // -------------------------
    @Test
    void naoDeveAtualizarServicoInexistente() {
        Servico servico = new Servico();

        when(repository.existsById(999L)).thenReturn(false);

        ResponseEntity<Servico> response = controller.atualizar(999L, servico);

        assertEquals(404, response.getStatusCodeValue());
        verify(repository, never()).save(any());
    }
}
