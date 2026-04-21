package com.example.crudObsidiana.domain.use_cases;

import com.example.crudObsidiana.domain.entities.*;
import com.example.crudObsidiana.domain.ports.*;
import com.example.crudObsidiana.interfaces.dto.OrcamentoDTO;
import com.example.crudObsidiana.interfaces.dto.UsoEquipamentoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

/**
 * Use Case de Orçamento — migração direta do OrcamentoService original.
 *
 * Implementa OrcamentoSubject para gerenciar os observers,
 * exatamente como o OrcamentoService original fazia.
 *
 * Depende apenas de Ports (interfaces) — nunca de JpaRepository diretamente.
 * O Spring injeta via construtor todos os beans OrcamentoObserver
 * (ex: EquipamentoObserver em infrastructure/observer).
 */
@Service
@Transactional
public class OrcamentoUseCase implements OrcamentoSubject {

    private final List<OrcamentoObserver>      observers = new ArrayList<>();
    private final OrcamentoRepositoryPort      orcamentoRepository;
    private final EquipamentoRepositoryPort    equipamentoRepository;
    private final ServicoRepositoryPort        servicoRepository;
    private final ProfissionalRepositoryPort   profissionalRepository;
    private final UsoEquipamentoRepositoryPort usoEquipamentoRepository;

    @Autowired
    public OrcamentoUseCase(
            OrcamentoRepositoryPort orcamentoRepository,
            EquipamentoRepositoryPort equipamentoRepository,
            ServicoRepositoryPort servicoRepository,
            ProfissionalRepositoryPort profissionalRepository,
            UsoEquipamentoRepositoryPort usoEquipamentoRepository,
            List<OrcamentoObserver> observers) {
        this.orcamentoRepository      = orcamentoRepository;
        this.equipamentoRepository    = equipamentoRepository;
        this.servicoRepository        = servicoRepository;
        this.profissionalRepository   = profissionalRepository;
        this.usoEquipamentoRepository = usoEquipamentoRepository;
        if (observers != null) this.observers.addAll(observers);
    }

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
    // CALCULAR VALOR TOTAL ORÇAMENTO
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

    // =========================================================================
    // CRIAR ORÇAMENTO
    // =========================================================================
    public Orcamento criarOrcamento(OrcamentoDTO dto) {

        Orcamento novoOrcamento = new Orcamento(
                dto.getDataInicio(),
                dto.getDataTermino(),
                dto.getLocalEvento(),
                dto.getDescricao(),
                dto.getStatus(),
                dto.getValorTotal(),
                dto.getIdCalendar()
        );

        Orcamento salvo = orcamentoRepository.save(novoOrcamento);

        List<UsoEquipamento> usos = new ArrayList<>();

        if (dto.getUsosEquipamentos() != null && !dto.getUsosEquipamentos().isEmpty()) {
            for (UsoEquipamentoDTO uDto : dto.getUsosEquipamentos()) {
                Long    idEq = uDto.getIdEquipamento();
                Integer qtd  = (uDto.getQuantidadeUsada() == null) ? 1 : uDto.getQuantidadeUsada();

                Equipamento eq = equipamentoRepository.findById(idEq)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.BAD_REQUEST, "Equipamento não encontrado: " + idEq));

                UsoEquipamento uso = new UsoEquipamento();
                uso.setOrcamento(salvo);
                uso.setEquipamento(eq);
                uso.setQuantidadeUsada(qtd);
                usos.add(usoEquipamentoRepository.save(uso));
            }
        } else if (dto.getEquipamentos() != null && !dto.getEquipamentos().isEmpty()) {
            for (Long idEq : dto.getEquipamentos()) {
                Equipamento eq = equipamentoRepository.findById(idEq)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.BAD_REQUEST, "Equipamento não encontrado: " + idEq));

                UsoEquipamento uso = new UsoEquipamento();
                uso.setOrcamento(salvo);
                uso.setEquipamento(eq);
                uso.setQuantidadeUsada(1);
                usos.add(usoEquipamentoRepository.save(uso));
            }
        }
        salvo.setUsosEquipamentos(usos);

        if (dto.getServicos() != null) {
            salvo.setServicos(servicoRepository.findAllById(dto.getServicos()));
        }
        if (dto.getProfissionais() != null) {
            salvo.setProfissionais(profissionalRepository.findAllById(dto.getProfissionais()));
        }
        if (dto.getEquipamentos() != null && !dto.getEquipamentos().isEmpty()) {
            salvo.setEquipamentos(equipamentoRepository.findAllById(dto.getEquipamentos()));
        }

        salvo.setValorTotal(calcularValorTotal(salvo));

        String statusDto = dto.getStatus();
        if (statusDto != null && "Confirmado".equalsIgnoreCase(statusDto)) {

            List<UsoEquipamento> usosPersistidos =
                    usoEquipamentoRepository.findByOrcamentoId(salvo.getId());
            if (usosPersistidos == null) usosPersistidos = new ArrayList<>();

            verificarEstoque(usosPersistidos);

            String statusAnterior = salvo.getStatus();
            salvo.setStatus("Confirmado");
            Orcamento salvoComStatus = orcamentoRepository.save(salvo);
            notifyObservers(salvoComStatus, statusAnterior, "Confirmado");
            return salvoComStatus;
        }

        return orcamentoRepository.save(salvo);
    }

    // =========================================================================
    // EDITAR ORÇAMENTO
    // =========================================================================
    public Orcamento editarOrcamento(Long id, OrcamentoDTO dto) {

        Orcamento existente = orcamentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Orçamento não encontrado: " + id));

        String statusAnterior = existente.getStatus();

        existente.setDescricao(dto.getDescricao());
        existente.setDataInicio(dto.getDataInicio());
        existente.setDataTermino(dto.getDataTermino());
        existente.setLocalEvento(dto.getLocalEvento());

        List<UsoEquipamento> usosAntigos =
                usoEquipamentoRepository.findByOrcamentoId(existente.getId());
        if (usosAntigos == null) usosAntigos = new ArrayList<>();

        // Construir mapa novo: equipamentoId -> quantidade
        Map<Long, Integer> novoMapa = new HashMap<>();
        if (dto.getUsosEquipamentos() != null && !dto.getUsosEquipamentos().isEmpty()) {
            for (UsoEquipamentoDTO uDto : dto.getUsosEquipamentos()) {
                Long    idEq = uDto.getIdEquipamento();
                Integer qtd  = (uDto.getQuantidadeUsada() == null) ? 1 : uDto.getQuantidadeUsada();
                novoMapa.put(idEq, novoMapa.getOrDefault(idEq, 0) + qtd);
            }
        } else if (dto.getEquipamentos() != null) {
            for (Long idEq : dto.getEquipamentos()) {
                novoMapa.put(idEq, novoMapa.getOrDefault(idEq, 0) + 1);
            }
        }

        // Construir mapa antigo
        Map<Long, Integer> antigoMapa = new HashMap<>();
        for (UsoEquipamento u : usosAntigos) {
            if (u.getEquipamento() == null) continue;
            Long    idEq = u.getEquipamento().getId();
            Integer q    = (u.getQuantidadeUsada() == null) ? 0 : u.getQuantidadeUsada();
            antigoMapa.put(idEq, antigoMapa.getOrDefault(idEq, 0) + q);
        }

        boolean estavaConfirmado = "Confirmado".equalsIgnoreCase(statusAnterior);

        // Se estava confirmado: calcular e aplicar deltas de estoque
        if (estavaConfirmado) {
            Map<Long, Integer> deltas = new HashMap<>();
            Set<Long> allIds = new HashSet<>();
            allIds.addAll(antigoMapa.keySet());
            allIds.addAll(novoMapa.keySet());

            for (Long idEq : allIds) {
                int delta = novoMapa.getOrDefault(idEq, 0) - antigoMapa.getOrDefault(idEq, 0);
                if (delta != 0) deltas.put(idEq, delta);
            }

            List<String> faltantes = new ArrayList<>();
            for (Map.Entry<Long, Integer> e : deltas.entrySet()) {
                if (e.getValue() <= 0) continue;
                Equipamento eq = equipamentoRepository.findById(e.getKey())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.BAD_REQUEST, "Equipamento não encontrado: " + e.getKey()));
                int disponivel = (eq.getQuantidadeDisponivel() == null) ? 0 : eq.getQuantidadeDisponivel();
                if (disponivel < e.getValue()) {
                    faltantes.add("Equipamento '" + eq.getNome() + "' (id=" + e.getKey()
                            + "): disponível " + disponivel + ", requerido adicional " + e.getValue());
                }
            }
            if (!faltantes.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Estoque insuficiente para edição: " + String.join("; ", faltantes));
            }

            for (Map.Entry<Long, Integer> e : deltas.entrySet()) {
                Equipamento eq = equipamentoRepository.findById(e.getKey())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.BAD_REQUEST, "Equipamento não encontrado: " + e.getKey()));
                if (e.getValue() > 0)      eq.reduzirQuantidade(e.getValue());
                else if (e.getValue() < 0) eq.devolverQuantidade(-e.getValue());
                equipamentoRepository.save(eq);
            }
        }

        // Remover usos antigos e recriar
        usoEquipamentoRepository.deleteByOrcamentoId(existente.getId());

        List<UsoEquipamento> novosUsos = new ArrayList<>();
        for (Map.Entry<Long, Integer> e : novoMapa.entrySet()) {
            Equipamento eq = equipamentoRepository.findById(e.getKey())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Equipamento não encontrado: " + e.getKey()));
            UsoEquipamento uso = new UsoEquipamento();
            uso.setOrcamento(existente);
            uso.setEquipamento(eq);
            uso.setQuantidadeUsada(e.getValue());
            novosUsos.add(usoEquipamentoRepository.save(uso));
        }
        existente.setUsosEquipamentos(novosUsos);

        if (dto.getEquipamentos() != null && !dto.getEquipamentos().isEmpty()) {
            existente.setEquipamentos(equipamentoRepository.findAllById(dto.getEquipamentos()));
        } else {
            existente.setEquipamentos(new ArrayList<>());
        }
        if (dto.getServicos() != null) {
            existente.setServicos(servicoRepository.findAllById(dto.getServicos()));
        }
        if (dto.getProfissionais() != null) {
            existente.setProfissionais(profissionalRepository.findAllById(dto.getProfissionais()));
        }

        existente.setValorTotal(calcularValorTotal(existente));

        String novoStatus  = (dto.getStatus() == null) ? statusAnterior : dto.getStatus();
        boolean seraConfirmado = "Confirmado".equalsIgnoreCase(novoStatus);

        // Sem transição confirm <-> não-confirm
        if (estavaConfirmado == seraConfirmado) {
            existente.setStatus(novoStatus);
            return orcamentoRepository.save(existente);
        }

        // Vai virar Confirmado
        if (seraConfirmado) {
            List<UsoEquipamento> usos = usoEquipamentoRepository.findByOrcamentoId(existente.getId());
            verificarEstoque(usos);
            existente.setStatus("Confirmado");
            Orcamento salvo = orcamentoRepository.save(existente);
            notifyObservers(salvo, statusAnterior, "Confirmado");
            return salvo;
        }

        // Era confirmado e agora deixa de ser
        existente.setStatus(novoStatus);
        Orcamento salvo = orcamentoRepository.save(existente);
        notifyObservers(salvo, statusAnterior, novoStatus);
        return salvo;
    }

    // =========================================================================
    // ATUALIZAR STATUS
    // =========================================================================
    public Orcamento atualizarStatus(Long idOrcamento, String novoStatus) {

        Orcamento orcamento = orcamentoRepository.findById(idOrcamento)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Orçamento não encontrado (ID: " + idOrcamento + ")"));

        String  statusAnterior = orcamento.getStatus();
        boolean eraConfirmado  = "Confirmado".equalsIgnoreCase(statusAnterior);
        boolean seraConfirmado = "Confirmado".equalsIgnoreCase(novoStatus);

        if (eraConfirmado == seraConfirmado) {
            orcamento.setStatus(novoStatus);
            return orcamentoRepository.save(orcamento);
        }

        List<UsoEquipamento> usos = usoEquipamentoRepository.findByOrcamentoId(orcamento.getId());
        if (usos == null) usos = new ArrayList<>();

        if (seraConfirmado) {
            verificarEstoque(usos);
        }

        orcamento.setStatus(novoStatus);
        Orcamento salvo = orcamentoRepository.save(orcamento);
        notifyObservers(salvo, statusAnterior, novoStatus);
        return salvo;
    }

    // =========================================================================
    // KPIS
    // =========================================================================
    public com.example.crudObsidiana.interfaces.dto.KpisOrcamentoDTO getKpis() {
        Integer confirmados = orcamentoRepository.countByStatus("Confirmado");
        Integer pendentes   = orcamentoRepository.countByStatus("Em análise");
        Integer cancelados  = orcamentoRepository.countByStatus("Cancelado");
        return new com.example.crudObsidiana.interfaces.dto.KpisOrcamentoDTO(confirmados, pendentes, cancelados);
    }

    // =========================================================================
    // HELPERS PRIVADOS
    // =========================================================================
    private double calcularValorTotal(Orcamento orcamento) {
        double total = 0.0;
        if (orcamento.getServicos() != null) {
            for (Servico serv : orcamento.getServicos()) {
                if (serv == null) continue;
                double vHora = (serv.getValorPorHora() == null) ? 0.0 : serv.getValorPorHora();
                int    h     = (serv.getHoras()        == null) ? 0   : serv.getHoras();
                total += vHora * h;
            }
        }
        if (orcamento.getUsosEquipamentos() != null) {
            for (UsoEquipamento uso : orcamento.getUsosEquipamentos()) {
                if (uso == null || uso.getEquipamento() == null) continue;
                int    qtd  = (uso.getQuantidadeUsada()               == null) ? 0   : uso.getQuantidadeUsada();
                double vEq  = (uso.getEquipamento().getValorPorHora() == null) ? 0.0 : uso.getEquipamento().getValorPorHora();
                total += qtd * vEq;
            }
        }
        return total;
    }

    private void verificarEstoque(List<UsoEquipamento> usos) {
        List<String> faltantes = new ArrayList<>();
        for (UsoEquipamento uso : usos) {
            Equipamento eq = uso.getEquipamento();
            if (eq == null) continue;
            int disponivel  = (eq.getQuantidadeDisponivel() == null) ? 0 : eq.getQuantidadeDisponivel();
            int requisitado = (uso.getQuantidadeUsada()     == null) ? 0 : uso.getQuantidadeUsada();
            if (disponivel < requisitado) {
                faltantes.add("Equipamento '" + eq.getNome() + "' (id=" + eq.getId()
                        + "): disponível " + disponivel + ", requisitado " + requisitado);
            }
        }
        if (!faltantes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Estoque insuficiente: " + String.join("; ", faltantes));
        }
    }

    // =========================================================================
    // OBSERVER PATTERN — idêntico ao OrcamentoService original
    // =========================================================================
    @Override
    public void registerObserver(OrcamentoObserver observer) {
        if (observer != null && !observers.contains(observer)) observers.add(observer);
    }

    @Override
    public void removeObserver(OrcamentoObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Orcamento orcamento, String statusAnterior, String novoStatus) {
        for (OrcamentoObserver observer : observers) {
            observer.onOrcamentoUpdated(orcamento, statusAnterior, novoStatus);
        }
    }
}