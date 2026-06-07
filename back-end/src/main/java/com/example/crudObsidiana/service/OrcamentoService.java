package com.example.crudObsidiana.service;

import com.example.crudObsidiana.dto.*;
import com.example.crudObsidiana.exception.EstoqueInsuficienteException;
import com.example.crudObsidiana.model.*;
import com.example.crudObsidiana.repository.EquipamentoRepository;
import com.example.crudObsidiana.repository.UsoEquipamentoRepository;
import com.example.crudObsidiana.repository.OrcamentoRepository;
import com.example.crudObsidiana.repository.ProfissionalRepository;
import com.example.crudObsidiana.repository.ServicoRepository;
import com.example.crudObsidiana.observer.OrcamentoObserver;
import com.example.crudObsidiana.observer.OrcamentoSubject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;

@Service
public class OrcamentoService implements OrcamentoSubject {

    private final List<OrcamentoObserver> observers = new ArrayList<>();
    private final OrcamentoRepository orcamentoRepository;

    //    CONSTRUCTORS
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
                dto.getTitulo(),
                dto.getLocalEvento(),
                dto.getObservacoes(),
                dto.getStatus(),
                dto.getValorTotal(),
                dto.getIdCalendar()
        );

        List<UsoEquipamento> usos = popularUsos(dto, novoOrcamento);
        novoOrcamento.setUsosEquipamentos(usos);

        // popular relações many-to-many (servicos/profissionais/equipamentos)
        if (dto.getServicos() != null) {
            List<Servico> servicos = servicoRepository.findAllById(dto.getServicos());
            novoOrcamento.setServicos(servicos);
        }
        if (dto.getProfissionais() != null) {
            List<Profissional> profs = profissionalRepository.findAllById(dto.getProfissionais());
            novoOrcamento.setProfissionais(profs);
        }
        if (dto.getEquipamentos() != null && !dto.getEquipamentos().isEmpty()) {
            List<Equipamento> equipamentos = equipamentoRepository.findAllById(dto.getEquipamentos());
            novoOrcamento.setEquipamentos(equipamentos);
        }

        // Se o front-end enviou um valorTotal (calculado ou manual), usa esse valor.
        // Só recalcula automaticamente se o campo não foi informado no DTO.
        if (dto.getValorTotal() == null) {
            double total = calcularValorTotal(novoOrcamento);
            novoOrcamento.setValorTotal(total);
        }
        // (caso dto.getValorTotal() != null, o valor já foi definido no construtor do Orcamento)


        String statusDto = dto.getStatus();
        // --- Se o DTO pediu status "Confirmado", checar estoque e notificar observers ---
        if (statusDto.equalsIgnoreCase("Confirmado")) {
            // Verificar e se há sobreposições para o período passado
            tratarSobreposicoes(novoOrcamento);

            // OK: persistir status e notificar observers
            String statusAnterior = novoOrcamento.getStatus();
            novoOrcamento.setStatus("Confirmado");
            Orcamento salvoComStatus = orcamentoRepository.save(novoOrcamento);

            // Após salvar o orçamento, tem que popular o usoEquipamento
            usoEquipamentoRepository.saveAll(usos);

            notifyObservers(salvoComStatus, statusAnterior, "Confirmado");

            // Enviar com campo de duração para usar no front
            salvoComStatus.setDuracaoEvento(calcularDuracaoEvento(salvoComStatus));
            return salvoComStatus;
        }

        // não pediu confirmação imediata: retornar orcamento salvo (status como veio/por default)
        Orcamento salvo = orcamentoRepository.save(novoOrcamento);

        // Após salvar o orçamento, tem que popular o usoEquipamento (????????????)
        // usoEquipamentoRepository.saveAll(usos);

        // Enviar com campo de duração para usar no front
        salvo.setDuracaoEvento(calcularDuracaoEvento(salvo));
        return salvo;
    }

    // ---------------------------------------------------------------------
    // Editar orçamento
    // ---------------------------------------------------------------------
    @Transactional
    public Orcamento editarOrcamento(Long id, OrcamentoDTO dto)     {
        String statusAnterior = orcamentoRepository.findById(id)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orçamento não encontrado: " + id))
          .getStatus();

        // salvar status anterior para decisões posteriores
        Orcamento orcamentoAtualizar = new Orcamento(
          dto.getDataInicio(),
          dto.getDataTermino(),
          dto.getTitulo(),
          dto.getLocalEvento(),
          dto.getObservacoes(),
          dto.getStatus(),
          dto.getValorTotal(),
          dto.getIdCalendar()
        );
        orcamentoAtualizar.setId(id);

        orcamentoAtualizar.setUsosEquipamentos(popularUsos(dto, orcamentoAtualizar));

        // atualização das listas
        if (dto.getServicos() != null && !dto.getServicos().isEmpty()) {
            List<Servico> servicos = servicoRepository.findAllById(dto.getServicos());
            orcamentoAtualizar.setServicos(servicos);
        }

        if (dto.getProfissionais() != null && !dto.getProfissionais().isEmpty()) {
            List<Profissional> profissionais = profissionalRepository.findAllById(dto.getProfissionais());
            orcamentoAtualizar.setProfissionais(profissionais);
        }

        if (dto.getEquipamentos() != null && !dto.getEquipamentos().isEmpty()) {
            List<Equipamento> equipamentos = equipamentoRepository.findAllById(dto.getEquipamentos());
            orcamentoAtualizar.setEquipamentos(equipamentos);
        }

        // Respeita o valorTotal enviado pelo front-end (pode ser manual ou calculado automaticamente).
        // Só recalcula se o campo não foi informado no DTO.
        if (dto.getValorTotal() == null) {
            double totalAtualizado = calcularValorTotal(orcamentoAtualizar);
            orcamentoAtualizar.setValorTotal(totalAtualizado);
        }

        // Se vai virar Confirmado: checar estoque (usos já atualizados)
        if (orcamentoAtualizar.getStatus().equalsIgnoreCase("Confirmado")) {
            tratarSobreposicoes(orcamentoAtualizar);

            // OK: persistir status e notificar observers
            Orcamento salvo = orcamentoRepository.save(orcamentoAtualizar);
            notifyObservers(salvo, statusAnterior, "Confirmado");

            // Enviar com campo de duração para usar no front
            salvo.setDuracaoEvento(calcularDuracaoEvento(salvo));
            return salvo;
        }

        Orcamento salvoFallback = orcamentoRepository.save(orcamentoAtualizar);

        // Enviar com campo de duração para usar no front
        salvoFallback.setDuracaoEvento(calcularDuracaoEvento(salvoFallback));
        return salvoFallback;
    }

    // ------------------------------------------------------------------------------
    // POPULAR USOS
    // mét0do privado para converter dto em lista de Usos,
    // lança exceção caso a qtd solicitada do equipamento seja maior que a qtd total
    // ------------------------------------------------------------------------------
    private List<UsoEquipamento> popularUsos(OrcamentoDTO dto, Orcamento orcamento) {
        List<UsoEquipamento> usos = new ArrayList<>();

        if (dto.getUsosEquipamentos() != null && !dto.getUsosEquipamentos().isEmpty()) {
            List<String> eqsInsuficientes = new ArrayList<>();

            for (UsoEquipamentoDTO uDto : dto.getUsosEquipamentos()) {
                Long idEq = uDto.getIdEquipamento();
                Integer qtd = (uDto.getQuantidadeUsada() == null) ? 1 : uDto.getQuantidadeUsada();
                Equipamento eq = equipamentoRepository.findById(idEq)
                  .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Equipamento não encontrado: " + idEq));
                UsoEquipamento uso = new UsoEquipamento();

                if (qtd > eq.getQuantidadeTotal()) {
                    eqsInsuficientes.add(
                      "%s | Disponíveis: %d | Requisitados: %d"
                        .formatted(
                          eq.getNome(),
                          eq.getQuantidadeTotal(),
                          qtd
                        )
                    );
                }

                if (!eqsInsuficientes.isEmpty()) continue;

                uso.setOrcamento(orcamento);
                uso.setEquipamento(eq);
                uso.setQuantidadeUsada(qtd);
                usos.add(uso);
            }

            if (!eqsInsuficientes.isEmpty()) {
                throw new EstoqueInsuficienteException(eqsInsuficientes);
            }
        } else if (dto.getEquipamentos() != null && !dto.getEquipamentos().isEmpty()) {
            for (Long idEq : dto.getEquipamentos()) {
                Equipamento eq = equipamentoRepository.findById(idEq)
                  .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Equipamento não encontrado: " + idEq));
                UsoEquipamento uso = new UsoEquipamento();
                uso.setOrcamento(orcamento);
                uso.setEquipamento(eq);
                uso.setQuantidadeUsada(1);
                usos.add(uso);
            }
        }

        return usos;
    }

    // ---------------------------------------------------------------------
    // TRATAR SOBREPOSIÇÕES - mét0do privado para orçamentos confirmados
    // ---------------------------------------------------------------------
    private void tratarSobreposicoes(Orcamento orcamento) {
        List<EquipamentoSobrepostoDTO> eqsSob =
          orcamento.getId() != null
            ? usoEquipamentoRepository.findSobrepostosOrcamentoNot(
                orcamento.getId(),
                orcamento.getDataInicio(), orcamento.getDataTermino(),
                orcamento.getEquipamentos()
                    .stream().mapToLong(Equipamento::getId).toArray()
            )
            : usoEquipamentoRepository.findSobrepostos(
                orcamento.getDataInicio(), orcamento.getDataTermino(),
                orcamento.getEquipamentos()
                    .stream().mapToLong(Equipamento::getId).toArray()
            );

        if (eqsSob.isEmpty()) {
            return;
        }

        List<String> equipamentosEmConflito = new ArrayList<>();
        for (EquipamentoSobrepostoDTO eqSob : eqsSob) {
            for (UsoEquipamento uso : orcamento.getUsosEquipamentos()) {
                if (!Objects.equals(uso.getEquipamento().getId(), eqSob.getIdEquipamento())) {
                    continue;
                }

                long soma = eqSob.getQuantidadeUsada().longValue() + uso.getQuantidadeUsada();
                if (soma > eqSob.getQuantidadeTotal()) {
                    long disponiveis =
                      eqSob.getQuantidadeTotal().longValue() - eqSob.getQuantidadeUsada().longValue();
                    equipamentosEmConflito.add(
                      "%s | Usado em: %s | Disponíveis: %d | Requisitados: %d"
                        .formatted(
                          uso.getEquipamento().getNome(),
                          eqSob.getOrcamentos(),
                          disponiveis < 0 ? 0 : disponiveis,
                          uso.getQuantidadeUsada()
                        )
                    );
                }
            }
        }

        if (!equipamentosEmConflito.isEmpty()) {
            throw new EstoqueInsuficienteException(
              "Estoque insuficiente do(s) equipamento(s) para o período (data de início e data de término) selecionado",
              equipamentosEmConflito
            );
        }

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
        // Notifica todos os observers registrados (EquipamentoObserver, RabbitMQOrcamentoObserver, etc.)
        // O OrcamentoService não precisa conhecer o RabbitMQ — ele só sabe que tem uma lista de observers.
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