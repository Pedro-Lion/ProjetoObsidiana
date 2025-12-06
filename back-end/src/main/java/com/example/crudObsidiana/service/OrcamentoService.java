package com.example.crudObsidiana.service;

import com.example.crudObsidiana.dto.OrcamentoDTO;
import com.example.crudObsidiana.dto.UsoEquipamentoDTO;
import com.example.crudObsidiana.model.*;
import com.example.crudObsidiana.repository.EquipamentoRepository;
import com.example.crudObsidiana.repository.UsoEquipamentoRepository;
import com.example.crudObsidiana.repository.OrcamentoRepository;
import com.example.crudObsidiana.repository.ProfissionalRepository;
import com.example.crudObsidiana.repository.ServicoRepository;
import com.example.crudObsidiana.observer.OrcamentoObserver;
import com.example.crudObsidiana.observer.OrcamentoSubject;
import com.example.crudObsidiana.repository.OrcamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrcamentoService implements OrcamentoSubject {

    private final OrcamentoRepository orcamentoRepository;
    // Lista de observers (EquipamentoObserver, e qualquer outro futuro):
    private final List<OrcamentoObserver> observers = new ArrayList<>();

    // Injeção via construtor
    // o Spring vai colocar aqui todos os beans que implementam OrcamentoObserver
    @Autowired
    public OrcamentoService(OrcamentoRepository orcamentoRepository,
                            List<OrcamentoObserver> observers) {
        this.orcamentoRepository = orcamentoRepository;

        if (observers != null) {
            this.observers.addAll(observers);
        }
    }

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private EquipamentoRepository equipamentoRepository;

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Autowired
    private UsoEquipamentoRepository usoEquipamentoRepository;

    // ---------------------------------------------------------------------
    // METODOS PADRÃO
    // ---------------------------------------------------------------------
    // Colar dentro de OrcamentoService (substitui o método criarOrcamento existente)
    @Transactional
    public Orcamento criarOrcamento(OrcamentoDTO dto) {
        Orcamento novoOrcamento = new Orcamento(
                dto.getDataEvento(),
                dto.getDuracaoEvento(),
                dto.getLocalEvento(),
                dto.getDescricao(),
                dto.getStatus(),
                dto.getValorTotal()
        );

        // Persistir o orçamento para garantir id e relacionamento correto
        Orcamento salvo = orcamentoRepository.save(novoOrcamento);

        // Criar usos de equipamento: prefere dto.getUsosEquipamentos() (com quantidades),
        // fallback: dto.getEquipamentos() (ids apenas -> quantidade = 1)
        List<UsoEquipamento> usos = new ArrayList<>();

        if (dto.getUsosEquipamentos() != null && !dto.getUsosEquipamentos().isEmpty()) {
            for (UsoEquipamentoDTO uDto : dto.getUsosEquipamentos()) {
                Long idEq = uDto.getIdEquipamento();
                Integer qtd = (uDto.getQuantidadeUsada() == null) ? 1 : uDto.getQuantidadeUsada();
                Equipamento eq = equipamentoRepository.findById(idEq)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Equipamento não encontrado: " + idEq));
                UsoEquipamento uso = new UsoEquipamento();
                uso.setOrcamento(salvo);
                uso.setEquipamento(eq);
                uso.setQuantidadeUsada(qtd);
                uso = usoEquipamentoRepository.save(uso);
                usos.add(uso);
            }
        } else if (dto.getEquipamentos() != null && !dto.getEquipamentos().isEmpty()) {
            for (Long idEq : dto.getEquipamentos()) {
                Equipamento eq = equipamentoRepository.findById(idEq)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Equipamento não encontrado: " + idEq));
                UsoEquipamento uso = new UsoEquipamento();
                uso.setOrcamento(salvo);
                uso.setEquipamento(eq);
                uso.setQuantidadeUsada(1); // default
                uso = usoEquipamentoRepository.save(uso);
                usos.add(uso);
            }
        }
        salvo.setUsosEquipamentos(usos);


        // Popular servicos/profissionais/equipamentos na model (mantendo compatibilidade)
        if (dto.getServicos() != null) {
            List<Servico> servicos = servicoRepository.findAllById(dto.getServicos());
            salvo.setServicos(servicos);
        }
        if (dto.getProfissionais() != null) {
            List<Profissional> profs = profissionalRepository.findAllById(dto.getProfissionais());
            salvo.setProfissionais(profs);
        }
        if (dto.getEquipamentos() != null && !dto.getEquipamentos().isEmpty()) {
            // já setamos usos, mas mantemos também a relação many-to-many se desejar
            List<Equipamento> equipamentos = equipamentoRepository.findAllById(dto.getEquipamentos());
            salvo.setEquipamentos(equipamentos);
        }

        return salvo;
    }

    // ---------------------------------------------------------------------
    // Editar orçamento
    // ---------------------------------------------------------------------
    @Transactional
    public Orcamento editarOrcamento(Long id, OrcamentoDTO dto) {
        Orcamento existente = orcamentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orçamento não encontrado: " + id));

        // Atualiza campos simples (mantendo o mesmo registro)
        existente.setDescricao(dto.getDescricao());
        existente.setDataEvento(dto.getDataEvento());
        existente.setDuracaoEvento(dto.getDuracaoEvento());
        existente.setLocalEvento(dto.getLocalEvento());
        existente.setValorTotal(dto.getValorTotal());
        // Não alteramos status aqui automaticamente

        // Buscar usos antigos persistidos
        List<UsoEquipamento> usosAntigos = usoEquipamentoRepository.findByOrcamento_Id(existente.getId());
        if (usosAntigos == null) usosAntigos = new ArrayList<>();

        // Construir mapa novo: equipamentoId -> quantidade (front envia ids -> default 1)
        // Construir mapa novo: equipamentoId -> quantidade
        Map<Long, Integer> novoMapa = new HashMap<>();

        if (dto.getUsosEquipamentos() != null && !dto.getUsosEquipamentos().isEmpty()) {
            // quando frontend envia explicitamente quantidades
            for (UsoEquipamentoDTO uDto : dto.getUsosEquipamentos()) {
                Long idEq = uDto.getIdEquipamento();
                Integer qtd = (uDto.getQuantidadeUsada() == null) ? 1 : uDto.getQuantidadeUsada();
                novoMapa.put(idEq, novoMapa.getOrDefault(idEq, 0) + qtd);
            }
        } else if (dto.getEquipamentos() != null) {
            // fallback: front envia apenas ids (cada id representa 1 unidade)
            for (Long idEq : dto.getEquipamentos()) {
                novoMapa.put(idEq, novoMapa.getOrDefault(idEq, 0) + 1);
            }
        }

        // Construir mapa antigo: equipamentoId -> quantidade
        Map<Long, Integer> antigoMapa = new HashMap<>();
        for (UsoEquipamento u : usosAntigos) {
            if (u.getEquipamento() == null) continue;
            Long idEq = u.getEquipamento().getId();
            Integer q = u.getQuantidadeUsada();
            antigoMapa.put(idEq, antigoMapa.getOrDefault(idEq, 0) + q);
        }

        boolean estavaConfirmado = "Confirmado".equalsIgnoreCase(existente.getStatus());

        if (estavaConfirmado) {
            // calcular deltas
            Map<Long, Integer> deltas = new HashMap<>();
            Set<Long> allIds = new HashSet<>();
            allIds.addAll(antigoMapa.keySet());
            allIds.addAll(novoMapa.keySet());
            for (Long idEq : allIds) {
                Integer novoQtd = novoMapa.getOrDefault(idEq, 0);
                Integer antigoQtd = antigoMapa.getOrDefault(idEq, 0);
                Integer delta = novoQtd - antigoQtd;
                if (delta != 0) deltas.put(idEq, delta);
            }

            // checar disponibilidade para deltas positivos
            List<String> faltantes = new ArrayList<>();
            for (Map.Entry<Long, Integer> e : deltas.entrySet()) {
                Long idEq = e.getKey();
                Integer delta = e.getValue();
                if (delta <= 0) continue;
                Equipamento eq = equipamentoRepository.findById(idEq)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Equipamento não encontrado: " + idEq));
                Integer disponivel = eq.getQuantidadeDisponivel();
                if (disponivel < delta) {
                    faltantes.add("Equipamento '" + eq.getNome() + "' (id=" + idEq + "): disponível " + disponivel + ", requerido adicional " + delta);
                }
            }
            if (!faltantes.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Estoque insuficiente para edição: " + String.join("; ", faltantes));
            }

            // aplicar deltas (reduzir ou devolver)
            for (Map.Entry<Long, Integer> e : deltas.entrySet()) {
                Long idEq = e.getKey();
                Integer delta = e.getValue();
                Equipamento eq = equipamentoRepository.findById(idEq)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Equipamento não encontrado: " + idEq));
                if (delta > 0) {
                    eq.reduzirQuantidade(delta);
                } else if (delta < 0) {
                    eq.devolverQuantidade(-delta);
                }
                equipamentoRepository.save(eq);
            }
        }

        // Agora sobrescrever usos: deletar apenas os usos deste orçamento
        // Usamos deleteByOrcamentoId (query) para performance (opção B)
        usoEquipamentoRepository.deleteByOrcamentoId(existente.getId());

        // Criar novos usos a partir do novoMapa
        List<UsoEquipamento> novosUsos = new ArrayList<>();
        for (Map.Entry<Long, Integer> e : novoMapa.entrySet()) {
            Long idEq = e.getKey();
            Integer qtd = e.getValue();
            Equipamento eq = equipamentoRepository.findById(idEq)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Equipamento não encontrado: " + idEq));
            UsoEquipamento uso = new UsoEquipamento();
            uso.setOrcamento(existente);
            uso.setEquipamento(eq);
            uso.setQuantidadeUsada(qtd);
            uso = usoEquipamentoRepository.save(uso);
            novosUsos.add(uso);
        }
        existente.setUsosEquipamentos(novosUsos);

        // Atualizar relações many-to-many (opcional: manter lista de equipamentos)
        if (dto.getEquipamentos() != null && !dto.getEquipamentos().isEmpty()) {
            List<Equipamento> equipamentos = equipamentoRepository.findAllById(dto.getEquipamentos());
            existente.setEquipamentos(equipamentos);
        } else {
            existente.setEquipamentos(new ArrayList<>());
        }

        Orcamento salvo = orcamentoRepository.save(existente);
        return salvo;
    }


    // ---------------------------------------------------------------------
    // Atualizar status do orçamento
    // ---------------------------------------------------------------------
    @Transactional
    public Orcamento atualizarStatus(Long idOrcamento, String novoStatus) {
        Orcamento orcamento = orcamentoRepository.findById(idOrcamento)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orçamento não encontrado (ID: " + idOrcamento + ")"));

        String statusAnterior = orcamento.getStatus();
        boolean eraConfirmado = "Confirmado".equalsIgnoreCase(statusAnterior);
        boolean seraConfirmado = "Confirmado".equalsIgnoreCase(novoStatus);

        // Idempotência: se não houve transição confirmada<->não-confirmada, atualiza e retorna
        if (eraConfirmado == seraConfirmado) {
            orcamento.setStatus(novoStatus);
            return orcamentoRepository.save(orcamento);
        }

        // Buscar usos persistidos
        List<UsoEquipamento> usos = usoEquipamentoRepository.findByOrcamento_Id(orcamento.getId());
        if (usos == null) usos = new ArrayList<>();

        // Se vai virar Confirmado -> checar estoque
        if (seraConfirmado && !eraConfirmado) {
            List<String> faltantes = new ArrayList<>();
            for (UsoEquipamento uso : usos) {
                Equipamento eq = uso.getEquipamento();
                if (eq == null) continue;
                Integer disponivel = eq.getQuantidadeDisponivel();
                Integer requisitado = uso.getQuantidadeUsada();
                if (disponivel < requisitado) {
                    faltantes.add("Equipamento '" + eq.getNome() + "' (id=" + eq.getId() + "): disponível " + disponivel + ", requisitado " + requisitado);
                }
            }
            if (!faltantes.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Estoque insuficiente: " + String.join("; ", faltantes));
            }
        }

        // Persistir novo status
        orcamento.setStatus(novoStatus);
        Orcamento salvo = orcamentoRepository.save(orcamento);

        // Notifica observers se houve transição de/para Confirmado
        if (eraConfirmado != seraConfirmado) {
            notifyObservers(salvo, statusAnterior, novoStatus);
        }

        return salvo;
    }


    // ---------------------------------------------------------------------
    // IMPLEMENTAÇÃO DO SUBJECT (OrcamentoSubject)
    // ---------------------------------------------------------------------
    @Override
    public void registerObserver(OrcamentoObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(OrcamentoObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Orcamento orcamento,
                                String statusAnterior,
                                String novoStatus) {
        for (OrcamentoObserver observer : observers) {
            observer.onOrcamentoUpdated(orcamento, statusAnterior, novoStatus);
        }
    }


} //FIM CLASSE
