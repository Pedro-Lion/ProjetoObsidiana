package com.example.crudObsidiana.infrastructure.config;

import com.example.crudObsidiana.domain.entities.*;
import com.example.crudObsidiana.domain.ports.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Popula o H2 com dados de exemplo na inicialização.
 * Usa Ports (não JpaRepository diretamente) e domain entities (não model JPA).
 */
@Component
public class SampleDataLoader implements CommandLineRunner {

    @Value("${spring.datasource.url}")
    private String dataBaseUrl;

    private final EquipamentoRepositoryPort    equipamentoRepository;
    private final ServicoRepositoryPort        servicoRepository;
    private final ProfissionalRepositoryPort   profissionalRepository;
    private final UsuarioRepositoryPort        usuarioRepository;
    private final OrcamentoRepositoryPort      orcamentoRepository;
    private final UsoEquipamentoRepositoryPort usoEquipamentoRepository;
    private final PasswordEncoder              passwordEncoder;

    public SampleDataLoader(
            EquipamentoRepositoryPort equipamentoRepository,
            ServicoRepositoryPort servicoRepository,
            ProfissionalRepositoryPort profissionalRepository,
            UsuarioRepositoryPort usuarioRepository,
            OrcamentoRepositoryPort orcamentoRepository,
            UsoEquipamentoRepositoryPort usoEquipamentoRepository,
            PasswordEncoder passwordEncoder) {
        this.equipamentoRepository    = equipamentoRepository;
        this.servicoRepository        = servicoRepository;
        this.profissionalRepository   = profissionalRepository;
        this.usuarioRepository        = usuarioRepository;
        this.orcamentoRepository      = orcamentoRepository;
        this.usoEquipamentoRepository = usoEquipamentoRepository;
        this.passwordEncoder          = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String[] args) throws Exception {
        if (dataBaseUrl.contains("mysql")) return;

        // USUÁRIO
        if (usuarioRepository.findAll().isEmpty()) {
            Usuario u = new Usuario();
            u.setNome("Administrador");
            u.setEmail("admin@obsidiana.com");
            u.setSenha(passwordEncoder.encode("123456"));
            usuarioRepository.save(u);
            System.out.println("✔ Usuário criado: admin@obsidiana.com / 123456");
        }

        // EQUIPAMENTOS
        if (equipamentoRepository.findAll().isEmpty()) {
            Equipamento eq1 = new Equipamento();
            eq1.setNome("Câmera Canon EOS R6");
            eq1.setCategoria("Câmeras"); eq1.setMarca("Canon"); eq1.setModelo("R6");
            eq1.setNumeroSerie("CAN-R6-001");
            eq1.setQuantidadeTotal(3); eq1.setQuantidadeDisponivel(3);
            eq1.setValorPorHora(150.0);
            eq1 = equipamentoRepository.save(eq1);

            Equipamento eq2 = new Equipamento();
            eq2.setNome("Ilha de Luz LED 3x3");
            eq2.setCategoria("Iluminação"); eq2.setMarca("Godox"); eq2.setModelo("LED-3x3");
            eq2.setNumeroSerie("GDX-300");
            eq2.setQuantidadeTotal(5); eq2.setQuantidadeDisponivel(5);
            eq2.setValorPorHora(40.0);
            eq2 = equipamentoRepository.save(eq2);

            Equipamento eq3 = new Equipamento();
            eq3.setNome("Tripé");
            eq3.setCategoria("Suporte"); eq3.setMarca("Tripex"); eq3.setModelo("Novo");
            eq3.setNumeroSerie("DEL-E14");
            eq3.setQuantidadeTotal(50); eq3.setQuantidadeDisponivel(50);
            eq3.setValorPorHora(40.0);
            equipamentoRepository.save(eq3);

            System.out.println("✔ Equipamentos criados");
        }

        // SERVIÇO
        if (servicoRepository.findAll().isEmpty()) {
            List<Equipamento> equipamentos = equipamentoRepository.findAll();
            if (!equipamentos.isEmpty()) equipamentos.remove(equipamentos.size() - 1);

            Servico serv = new Servico();
            serv.setNome("Cobertura de Evento - Foto/Vídeo");
            serv.setDescricao("Serviço com câmera e iluminação.");
            serv.setHoras(1);
            serv.setValorPorHora(200.0);
            serv.setEquipamentos(equipamentos);
            servicoRepository.save(serv);
            System.out.println("✔ Serviço criado");
        }

        // PROFISSIONAL
        if (profissionalRepository.findAll().isEmpty()) {
            Profissional p = new Profissional("Haidê Landim", "Disponível", "haide.landim@outlook.com");
            profissionalRepository.save(p);
            System.out.println("✔ Profissional criado");
        }

        // ORÇAMENTO
        if (orcamentoRepository.findAll().isEmpty()) {
            List<Equipamento>  eqs   = equipamentoRepository.findAll();
            List<Servico>      servs = servicoRepository.findAll();
            List<Profissional> profs = profissionalRepository.findAll();

            List<Equipamento> eqsOrc = new ArrayList<>(eqs);
            if (!eqsOrc.isEmpty()) eqsOrc.remove(eqsOrc.size() - 1);

            Orcamento orc = new Orcamento(
                    new Date(),
                    new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 4),
                    "São Paulo",
                    "Gravação de frigorífico industrial",
                    "Em análise",
                    0.0,
                    null
            );
            Orcamento salvo = orcamentoRepository.save(orc);

            List<UsoEquipamento> usos = new ArrayList<>();
            for (int i = 0; i < Math.min(2, eqsOrc.size()); i++) {
                UsoEquipamento uso = new UsoEquipamento();
                uso.setOrcamento(salvo);
                uso.setEquipamento(eqsOrc.get(i));
                uso.setQuantidadeUsada(1);
                usos.add(usoEquipamentoRepository.save(uso));
            }

            salvo.setUsosEquipamentos(usos);
            salvo.setServicos(servs);
            salvo.setProfissionais(profs);
            salvo.setEquipamentos(eqsOrc);

            double total = usos.stream()
                    .filter(u -> u.getEquipamento() != null && u.getEquipamento().getValorPorHora() != null)
                    .mapToDouble(u -> u.getQuantidadeUsada() * u.getEquipamento().getValorPorHora())
                    .sum();
            salvo.setValorTotal(total);

            orcamentoRepository.save(salvo);
            System.out.println("✔ Orçamento de exemplo criado (id=" + salvo.getId() + ")");
        }

        System.out.println("\n🎉 SampleDataLoader finalizado — H2 populado!\n");
    }
}