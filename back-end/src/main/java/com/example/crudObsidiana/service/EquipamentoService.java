package com.example.crudObsidiana.service;

import com.example.crudObsidiana.dto.EquipamentoDTO;
import com.example.crudObsidiana.model.Equipamento;
import com.example.crudObsidiana.repository.EquipamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipamentoService {

    @Autowired
    private EquipamentoRepository equipamentoRepository;

    public EquipamentoService(EquipamentoRepository equipamentoRepository) {
        this.equipamentoRepository = equipamentoRepository;
    }

    // -------------------------------------------------------------------------
    // LISTAR TODOS — resultado cacheado em "equipamentos"
    // -------------------------------------------------------------------------
    // @Cacheable: na primeira chamada, executa o método e guarda o resultado
    // no cache com a chave "todos". Nas chamadas seguintes, retorna direto
    // da memória sem consultar o banco.
    @Cacheable(cacheNames = "equipamentos", key = "'todos'")
    public List<Equipamento> listarTodos() {
        System.out.println("[CACHE] MISS → buscando equipamentos no banco...");
        return equipamentoRepository.findAll();
    }

    // -------------------------------------------------------------------------
    // BUSCAR POR ID — resultado cacheado em "equipamento" com chave = id
    // -------------------------------------------------------------------------
    @Cacheable(cacheNames = "equipamento", key = "#id")
    public Equipamento buscarPorId(Long id) {
        System.out.println("[CACHE] MISS → buscando equipamento ID " + id + " no banco...");
        return equipamentoRepository.findById(id).orElse(null);
    }

    // -------------------------------------------------------------------------
    // CRIAR — invalida o cache de lista após inserção
    // -------------------------------------------------------------------------
    // @CacheEvict com allEntries=true: limpa TODO o cache "equipamentos"
    // para garantir que a próxima listagem vá ao banco e traga o novo item.
    @CacheEvict(cacheNames = "equipamentos", allEntries = true)
    public Equipamento criarEquipamento(EquipamentoDTO dto) {
        Equipamento equipamento = new Equipamento();
        equipamento.setNome(dto.getNome());
        equipamento.setQuantidadeTotal(dto.getQuantidadeTotal());
        equipamento.setCategoria(dto.getCategoria());
        equipamento.setMarca(dto.getMarca());
        equipamento.setNumeroSerie(dto.getNumeroSerie());
        equipamento.setModelo(dto.getModelo());
        equipamento.setValorPorHora(dto.getValorPorHora());
        equipamento.setQuantidadeDisponivel(
                equipamento.getQuantidadeTotal() == null ? 0 : equipamento.getQuantidadeTotal()
        );

        return equipamentoRepository.save(equipamento);
    }

    // -------------------------------------------------------------------------
    // ATUALIZAR — invalida cache de lista e do item específico
    // -------------------------------------------------------------------------
    // @Caching: permite combinar múltiplas anotações de cache na mesma linha.
    // Aqui limpamos tanto o cache da lista quanto o cache do item por ID.
    @Caching(evict = {
            @CacheEvict(cacheNames = "equipamentos", allEntries = true),
            @CacheEvict(cacheNames = "equipamento", key = "#id")
    })
    public Equipamento atualizarEquipamento(Long id, Equipamento equipamentoBody) {
        return equipamentoRepository.findById(id).map(existente -> {
            if (equipamentoBody.getNome() != null) existente.setNome(equipamentoBody.getNome());
            if (equipamentoBody.getCategoria() != null) existente.setCategoria(equipamentoBody.getCategoria());
            if (equipamentoBody.getMarca() != null) existente.setMarca(equipamentoBody.getMarca());
            if (equipamentoBody.getNumeroSerie() != null) existente.setNumeroSerie(equipamentoBody.getNumeroSerie());
            if (equipamentoBody.getModelo() != null) existente.setModelo(equipamentoBody.getModelo());
            if (equipamentoBody.getValorPorHora() != null) existente.setValorPorHora(equipamentoBody.getValorPorHora());
            if (equipamentoBody.getQuantidadeTotal() != null) {
                existente.setQuantidadeTotal(equipamentoBody.getQuantidadeTotal());
            }
            return equipamentoRepository.save(existente);
        }).orElse(null);
    }

    // -------------------------------------------------------------------------
    // DELETAR — invalida cache de lista e do item específico
    // -------------------------------------------------------------------------
    @Caching(evict = {
            @CacheEvict(cacheNames = "equipamentos", allEntries = true),
            @CacheEvict(cacheNames = "equipamento", key = "#id")
    })
    public boolean deletarEquipamento(Long id) {
        if (equipamentoRepository.existsById(id)) {
            equipamentoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}