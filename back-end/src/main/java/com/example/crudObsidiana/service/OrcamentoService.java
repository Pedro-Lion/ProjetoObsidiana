package com.example.crudObsidiana.service;

import com.example.crudObsidiana.dto.KpisOrcamentoDTO;
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
import com.example.crudObsidiana.rabbitmq.OrcamentoEventPublisher;
import com.example.crudObsidiana.repository.OrcamentoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class OrcamentoService implements OrcamentoSubject {

    private final List<OrcamentoObserver> observers = new ArrayList<>();
    private final OrcamentoRepository orcamentoRepository;

    // Publisher RabbitMQ — publica eventos de status nas filas de mensageria
    private final OrcamentoEventPublisher eventPublisher;

    //    CONSTRUCTORS
    // Injeção via construtor
    // o Spring vai colocar aqui todos os beans que implementam OrcamentoObserver
    @Autowired
    public OrcamentoService(OrcamentoRepository orcamentoRepository,
                            List<OrcamentoObserver> observers,
                            OrcamentoEventPublisher eventPublisher) {
        this.orcamentoRepository = orcamentoRepository;
        this.eventPublisher      = eventPublisher;

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
    // CALCULAR DURAÇÃO DO EVENTO
    // ---------------------------------------------------------------------
    // Retorna a diferença entre dataTermino e dataInicio em horas decimais.
    // Ex: 3h30min → 3.5
    private Double calcularDuracaoEvento(Orcamento orcamento) {
        if (orcamento.getDataInicio() == null || orcamento.getDataTermino() == null) return null;
        long diffMs = orcamento.getDataTermino().getTime() - orcamento.getDataInicio().getTime();
        if (diffMs <= 0) return null;
        // converte milissegundos → horas com precisão de 2 casas
        double horas = diffMs / 3_600_000.0;
        return Math.round(horas * 100.0) / 100.0;
    }

    // ---------------------------------------------------------------------
    // CALCULAR BALOR TOTAL ORÇAMENTO
    // ---------------------------------------------------------------------
    // Mantive nomes e checagens de nulls: soma serviços (horas * valorPorHora)
    // e equipamentos via usosEquipamentos (quantidadeUsada * equipamento.valorPorHora)
    private double calcularValorTotal(Orcamento orcamento) {
        double total = 0.0;

        // Serviços: horas * valorPorHora (se houver serviços associados)
        if (orcamento.getServicos() != null) {
            for (Servico serv : orcamento.getServicos()) {
                if (serv == null) continue;
                Double valorHora = serv.getValorPorHora();
                Integer horas = serv.getHoras();
                double vHora = (valorHora == null) ? 0.0 : valorHora;
                int h = (horas == null) ? 0 : horas;
                total += vHora * h;
            }
        }

        // Equipamentos: usosEquipamentos (quantidadeUsada * equipamento.valorPorHora)
        if (orcamento.getUsosEquipamentos() != null) {
            for (UsoEquipamento uso : orcamento.getUsosEquipamentos()) {
                if (uso == null || uso.getEquipamento() == null) continue;
                Integer qtd = (uso.getQuantidadeUsada() == null) ? 0 : uso.getQuantidadeUsada();
                Double vHoraEq = uso.getEquipamento().getValorPorHora();
                double vEq = (vHoraEq == null) ? 0.0 : vHoraEq;
                total += qtd * vEq;
            }
        }
        return total;
    }


    // ---------------------------------------------------------------------
    // LISTAR TODOS OS ORÇAMENTOS
    // ---------------------------------------------------------------------
    public List<Orcamento> listarTodos() {
        List<Orcamento> lista = orcamentoRepository.findAll();
        lista.forEach(o -> o.setDuracaoEvento(calcularDuracaoEvento(o)));
        return lista;
    }

    // ---------------------------------------------------------------------
    // LISTAR COM PAGINAÇÃO E BUSCA
    // ---------------------------------------------------------------------
    public Page<Orcamento> listarPaginado(int page, int size, String busca) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Orcamento> paginaResult = busca.isBlank()
                ? orcamentoRepository.findAll(pageable)
                : orcamentoRepository.findByBusca(busca, pageable);

        // Aplica calcularDuracaoEvento em cada item, igual ao listarTodos()
        paginaResult.getContent().forEach(o -> o.setDuracaoEvento(calcularDuracaoEvento(o)));

        return paginaResult;
    }

    // ---------------------------------------------------------------------
    // METODOS PADRÃO
    // ---------------------------------------------------------------------

    @Transactional
    public Orcamento criarOrcamento(OrcamentoDTO dto) {
        // Mapear DTO -> Model Orcamento
        Orcamento novoOrcamento = new Orcamento(
                dto.getDataInicio(),
                dto.getDataTermino(),
                dto.getLocalEvento(),
                dto.getDescricao(),
                dto.getStatus(),
                dto.getValorTotal(),
                dto.getIdCalendar()
        );

        // Persistir para obter ID
        Orcamento salvo = orcamentoRepository.save(novoOrcamento);

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
                uso.setQuantidadeUsada(1);
                uso = usoEquipamentoRepository.save(uso);
                usos.add(uso);
            }
        }
        salvo.setUsosEquipamentos(usos);

        // popular relações many-to-many (servicos/profissionais/equipamentos)
        if (dto.getServicos() != null) {
            List<Servico> servicos = servicoRepository.findAllById(dto.getServicos());
            salvo.setServicos(servicos);
        }
        if (dto.getProfissionais() != null) {
            List<Profissional> profs = profissionalRepository.findAllById(dto.getProfissionais());
            salvo.setProfissionais(profs);
        }
        if (dto.getEquipamentos() != null && !dto.getEquipamentos().isEmpty()) {
            List<Equipamento> equipamentos = equipamentoRepository.findAllById(dto.getEquipamentos());
            salvo.setEquipamentos(equipamentos);
        }

        // Se o front-end enviou um valorTotal (calculado ou manual), usa esse valor.
        // Só recalcula automaticamente se o campo não foi informado no DTO.
        if (dto.getValorTotal() == null) {
            double total = calcularValorTotal(salvo);
            salvo.setValorTotal(total);
        }
        // (caso dto.getValorTotal() != null, o valor já foi definido no construtor do Orcamento)


        // --- Se o DTO pediu status "Confirmado", checar estoque e notificar observers ---
        String statusDto = dto.getStatus();
        if (statusDto != null && "Confirmado".equalsIgnoreCase(statusDto)) {

            // buscar usos persistidos (garante leitura correta)
            List<UsoEquipamento> usosPersistidos = usoEquipamentoRepository.findByOrcamento_Id(salvo.getId());
            if (usosPersistidos == null) usosPersistidos = new ArrayList<>();

            List<String> faltantes = new ArrayList<>();
            for (UsoEquipamento uso : usosPersistidos) {
                Equipamento eq = uso.getEquipamento();
                if (eq == null) continue;
                Integer disponivel = (eq.getQuantidadeDisponivel() == null) ? 0 : eq.getQuantidadeDisponivel();
                Integer requisitado = (uso.getQuantidadeUsada() == null) ? 0 : uso.getQuantidadeUsada();
                if (disponivel < requisitado) {
                    faltantes.add("Equipamento '" + eq.getNome() + "' (id=" + eq.getId() + "): disponível " + disponivel + ", requisitado " + requisitado);
                }
            }

            if (!faltantes.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Estoque insuficiente: " + String.join("; ", faltantes));
            }

            // OK: persistir status e notificar observers para reduzir estoque
            String statusAnterior = salvo.getStatus();
            salvo.setStatus("Confirmado");
            Orcamento salvoComStatus = orcamentoRepository.save(salvo);
            notifyObservers(salvoComStatus, statusAnterior, "Confirmado");
            // Publicar evento de confirmação no RabbitMQ (fila.orcamento.confirmado)
            eventPublisher.publicarConfirmado(salvoComStatus, statusAnterior);
            salvoComStatus.setDuracaoEvento(calcularDuracaoEvento(salvoComStatus));
            return salvoComStatus;
        }

        // não pediu confirmação imediata: retornar orcamento salvo (status como veio/por default)
        salvo.setDuracaoEvento(calcularDuracaoEvento(salvo));
        return salvo;
    }


    // ---------------------------------------------------------------------
    // Editar orçamento
    // ---------------------------------------------------------------------
    @Transactional
    public Orcamento editarOrcamento(Long id, OrcamentoDTO dto) {
        Orcamento existente = orcamentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orçamento não encontrado: " + id));

        // salvar status anterior para decisões posteriores
        String statusAnterior = existente.getStatus();

        // Atualiza campos simples
        // não puxar getStatus() aqui ainda! há uma lógica abaixo
        existente.setDescricao(dto.getDescricao());
        existente.setDataInicio(dto.getDataInicio());
        existente.setDataTermino(dto.getDataTermino());
        existente.setLocalEvento(dto.getLocalEvento());
        existente.setIdCalendar(dto.getIdCalendar());

        // atualização das listas
        if (dto.getServicos() != null && !dto.getServicos().isEmpty()) {
            List<Servico> servicos = servicoRepository.findAllById(dto.getServicos());
            existente.setServicos(servicos);
        } else {
            existente.setServicos(new ArrayList<>());
        }

        if (dto.getProfissionais() != null && !dto.getProfissionais().isEmpty()) {
            List<Profissional> profissionais = profissionalRepository.findAllById(dto.getProfissionais());
            existente.setProfissionais(profissionais);
        } else {
            existente.setProfissionais(new ArrayList<>());
        }

        // Buscar usos antigos vinculados
        List<UsoEquipamento> usosAntigos = usoEquipamentoRepository.findByOrcamento_Id(existente.getId());
        if (usosAntigos == null) usosAntigos = new ArrayList<>();

        // Construir mapa novo: equipamentoId -> quantidade
        Map<Long, Integer> novoMapa = new HashMap<>();
        if (dto.getUsosEquipamentos() != null && !dto.getUsosEquipamentos().isEmpty()) {
            for (UsoEquipamentoDTO uDto : dto.getUsosEquipamentos()) {
                Long idEq = uDto.getIdEquipamento();
                Integer qtd = (uDto.getQuantidadeUsada() == null) ? 1 : uDto.getQuantidadeUsada();
                novoMapa.put(idEq, novoMapa.getOrDefault(idEq, 0) + qtd);
            }
        } else if (dto.getEquipamentos() != null) {
            for (Long idEq : dto.getEquipamentos()) {
                novoMapa.put(idEq, novoMapa.getOrDefault(idEq, 0) + 1);
            }
        }

        // Mapear usos antigos para mapa
        Map<Long, Integer> antigoMapa = new HashMap<>();
        for (UsoEquipamento u : usosAntigos) {
            if (u.getEquipamento() == null) continue;
            Long idEq = u.getEquipamento().getId();
            Integer q = (u.getQuantidadeUsada() == null) ? 0 : u.getQuantidadeUsada();
            antigoMapa.put(idEq, antigoMapa.getOrDefault(idEq, 0) + q);
        }

        boolean estavaConfirmado = "Confirmado".equalsIgnoreCase(statusAnterior);

        // Se estava confirmado, aplicar deltas (checagem de disponibilidade para deltas positivos)
        if (estavaConfirmado) {
            Map<Long, Integer> deltas = new HashMap<>();
            Set<Long> allIds = new HashSet<>();
            allIds.addAll(antigoMapa.keySet());
            allIds.addAll(novoMapa.keySet());
            for (Long idEq : allIds) {
                int novoQtd = novoMapa.getOrDefault(idEq, 0);
                int antigoQtd = antigoMapa.getOrDefault(idEq, 0);
                int delta = novoQtd - antigoQtd;
                if (delta != 0) deltas.put(idEq, delta);
            }

            // checar disponibilidade para deltas positivos
            List<String> faltantes = new ArrayList<>();
            for (Map.Entry<Long, Integer> e : deltas.entrySet()) {
                Long idEq = e.getKey();
                int delta = e.getValue();
                if (delta <= 0) continue;
                Equipamento eq = equipamentoRepository.findById(idEq)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Equipamento não encontrado: " + idEq));
                Integer disponivel = (eq.getQuantidadeDisponivel() == null) ? 0 : eq.getQuantidadeDisponivel();
                if (disponivel < delta) {
                    faltantes.add("Equipamento '" + eq.getNome() + "' (id=" + idEq + "): disponível " + disponivel + ", requerido adicional " + delta);
                }
            }
            if (!faltantes.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Estoque insuficiente para edição: " + String.join("; ", faltantes));
            }

            // aplicar deltas
            for (Map.Entry<Long, Integer> e : deltas.entrySet()) {
                Long idEq = e.getKey();
                int delta = e.getValue();
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

        if (existente.getUsosEquipamentos() == null) {
            existente.setUsosEquipamentos(new ArrayList<>());
        } else {
            // remove todos os elementos da lista gerenciada (triggera orphanRemoval)
            existente.getUsosEquipamentos().clear();
        }

        // agora crie e salve os novos usos e ADICIONE à coleção gerenciada
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

            // add no MESMO objeto — NÃO usar setUsosEquipamentos(novosUsos)
            existente.getUsosEquipamentos().add(uso);
        }

        // atualizar lista many-to-many de equipamentos (opcional/compat)
        if (dto.getEquipamentos() != null && !dto.getEquipamentos().isEmpty()) {
            List<Equipamento> equipamentos = equipamentoRepository.findAllById(dto.getEquipamentos());
            existente.setEquipamentos(equipamentos);
        } else {
            existente.setEquipamentos(new ArrayList<>());
        }

        // Respeita o valorTotal enviado pelo front-end (pode ser manual ou calculado automaticamente).
        // Só recalcula se o campo não foi informado no DTO.
        if (dto.getValorTotal() == null) {
            double totalAtualizado = calcularValorTotal(existente);
            existente.setValorTotal(totalAtualizado);
        } else {
            existente.setValorTotal(dto.getValorTotal());
        }

        // tratar mudança de status solicitada no DTO
        String novoStatusDto = dto.getStatus();
        String novoStatus = (novoStatusDto == null) ? statusAnterior : novoStatusDto;
        boolean seraConfirmado = "Confirmado".equalsIgnoreCase(novoStatus);

        // Se não houve mudnça de status confirm<->nao-confirm, apenas persistir status e retornar
        if ( ("Confirmado".equalsIgnoreCase(statusAnterior)) == seraConfirmado ) {
            existente.setStatus(novoStatus);
            Orcamento salvo = orcamentoRepository.save(existente);
            // Publicar evento de cancelamento mesmo quando não vem de Confirmado
            // (ex: "Em análise" → "Cancelado" não passa pelo Observer, mas deve publicar na fila)
            if ("Cancelado".equalsIgnoreCase(novoStatus)) {
                eventPublisher.publicarCancelado(salvo, statusAnterior);
            }
            salvo.setDuracaoEvento(calcularDuracaoEvento(salvo));
            return salvo;
        }

        // Se vai virar Confirmado: checar estoque (usos já atualizados)
        if (seraConfirmado && !"Confirmado".equalsIgnoreCase(statusAnterior)) {
            List<UsoEquipamento> usos = usoEquipamentoRepository.findByOrcamento_Id(existente.getId());
            List<String> faltantes = new ArrayList<>();
            for (UsoEquipamento uso : usos) {
                Equipamento eq = uso.getEquipamento();
                if (eq == null) continue;
                Integer disponivel = (eq.getQuantidadeDisponivel() == null) ? 0 : eq.getQuantidadeDisponivel();
                Integer requisitado = (uso.getQuantidadeUsada() == null) ? 0 : uso.getQuantidadeUsada();
                if (disponivel < requisitado) {
                    faltantes.add("Equipamento '" + eq.getNome() + "' (id=" + eq.getId() + "): disponível " + disponivel + ", requisitado " + requisitado);
                }
            }
            if (!faltantes.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Estoque insuficiente: " + String.join("; ", faltantes));
            }
            existente.setStatus("Confirmado");
            Orcamento salvo = orcamentoRepository.save(existente);
            notifyObservers(salvo, statusAnterior, "Confirmado");
            // Publicar evento de confirmação no RabbitMQ (fila.orcamento.confirmado)
            eventPublisher.publicarConfirmado(salvo, statusAnterior);
            salvo.setDuracaoEvento(calcularDuracaoEvento(salvo));
            return salvo;
        }

        // Se estava confirmado e agora deixa de ser: persistir e notificar devolução
        if (!seraConfirmado && "Confirmado".equalsIgnoreCase(statusAnterior)) {
            existente.setStatus(novoStatus);
            Orcamento salvo = orcamentoRepository.save(existente);
            notifyObservers(salvo, statusAnterior, novoStatus);
            // Publicar evento de cancelamento no RabbitMQ (apenas se o destino for Cancelado)
            if ("Cancelado".equalsIgnoreCase(novoStatus)) {
                eventPublisher.publicarCancelado(salvo, statusAnterior);
            }
            salvo.setDuracaoEvento(calcularDuracaoEvento(salvo));
            return salvo;
        }

        // fallback - persistir e retornar
        existente.setStatus(novoStatus);
        Orcamento salvoFallback = orcamentoRepository.save(existente);
        salvoFallback.setDuracaoEvento(calcularDuracaoEvento(salvoFallback));
        return salvoFallback;
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

        // Se não houve mudança de status confirmada<->não-confirmada, atualiza e retorna
        if (eraConfirmado == seraConfirmado) {
            orcamento.setStatus(novoStatus);
            Orcamento salvo = orcamentoRepository.save(orcamento);
            // Publicar evento de cancelamento mesmo quando não vem de Confirmado
            // (ex: "Em análise" → "Cancelado" não passa pelo Observer, mas deve publicar na fila)
            if ("Cancelado".equalsIgnoreCase(novoStatus)) {
                eventPublisher.publicarCancelado(salvo, statusAnterior);
            }
            salvo.setDuracaoEvento(calcularDuracaoEvento(salvo));
            return salvo;
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

        // Publicar evento no RabbitMQ conforme novo status
        if (seraConfirmado && !eraConfirmado) {
            // "Em análise" → "Confirmado": publica na fila de confirmados
            eventPublisher.publicarConfirmado(salvo, statusAnterior);
        } else if ("Cancelado".equalsIgnoreCase(novoStatus) && eraConfirmado) {
            // "Confirmado" → "Cancelado": publica na fila de cancelados
            eventPublisher.publicarCancelado(salvo, statusAnterior);
        }

        salvo.setDuracaoEvento(calcularDuracaoEvento(salvo));
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

    // ---------------------------------------------------------------------
    // KPIs
    // ---------------------------------------------------------------------

    public KpisOrcamentoDTO getKpis() {
        Integer confirmados = orcamentoRepository.countByStatus("Confirmado");
        Integer pendentes = orcamentoRepository.countByStatus("Em análise");
        Integer cancelados = orcamentoRepository.countByStatus("Cancelado");

        return new KpisOrcamentoDTO(confirmados, pendentes, cancelados);
    }


} //FIM CLASSE