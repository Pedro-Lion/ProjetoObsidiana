package com.example.crudObsidiana.service;

import com.example.crudObsidiana.dto.OrcamentoDTO;
import com.example.crudObsidiana.model.Orcamento;
import com.example.crudObsidiana.model.Profissional;
import com.example.crudObsidiana.model.Servico;
import com.example.crudObsidiana.repository.EquipamentoRepository;
import com.example.crudObsidiana.repository.OrcamentoRepository;
import com.example.crudObsidiana.repository.ProfissionalRepository;
import com.example.crudObsidiana.repository.ServicoRepository;
import com.example.crudObsidiana.observer.OrcamentoObserver;
import com.example.crudObsidiana.observer.OrcamentoSubject;
import com.example.crudObsidiana.repository.OrcamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    // ---------------------------------------------------------------------
    // METODOS PADRÃO
    // ---------------------------------------------------------------------
    public Orcamento criarOrcamento(OrcamentoDTO dto) {
        Orcamento novoOrcamento = new Orcamento(
            dto.getDataEvento(),
            dto.getDuracaoEvento(),
            dto.getLocalEvento(),
            dto.getDescricao(),
            dto.getStatus(),
            dto.getValorTotal()
        );

        // O status padrão é "Em análise",
        // pois o Observer só entra em ação quando o endpoint de status é chamado.
        // Dessa forma, a qtd de equipamentos não é modificada na criação do orçamento, evitando falhas futuras

        List<Servico> servicos = servicoRepository.findAllById(dto.getServicos());
        novoOrcamento.setServicos(servicos);

        List<Equipamento> equipamentos = equipamentoRepository.findAllById(dto.getEquipamentos());
        novoOrcamento.setEquipamentos(equipamentos);

        List<Profissional> profissionais = profissionalRepository.findAllById(dto.getProfissionais());
        novoOrcamento.setProfissionais(profissionais);

        return orcamentoRepository.save(novoOrcamento);
    }
  
    // ---------------------------------------------------------------------
    // Atualizar status do orçamento
    // ---------------------------------------------------------------------
    public Orcamento atualizarStatus(Long idOrcamento, String novoStatus) {
        Orcamento orcamento = orcamentoRepository.findById(idOrcamento)
                .orElseThrow(() -> new RuntimeException(
                        "Orçamento não encontrado (ID: " + idOrcamento + ")"));

        String statusAnterior = orcamento.getStatus();
        orcamento.setStatus(novoStatus);

        Orcamento salvo = orcamentoRepository.save(orcamento);

        boolean eraConfirmado = "Confirmado".equalsIgnoreCase(statusAnterior);
        boolean ehConfirmado  = "Confirmado".equalsIgnoreCase(novoStatus);

        // Dispara observer nos dois cenários:
        // 1) virou Confirmado
        // 2) deixou de ser Confirmado
        if (eraConfirmado != ehConfirmado) {
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
