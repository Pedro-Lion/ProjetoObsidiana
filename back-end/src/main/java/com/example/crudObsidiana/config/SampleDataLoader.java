package com.example.crudObsidiana.config;

import com.example.crudObsidiana.model.Equipamento;
import com.example.crudObsidiana.model.Servico;
import com.example.crudObsidiana.model.Profissional;
import com.example.crudObsidiana.model.Usuario;
import com.example.crudObsidiana.model.Orcamento;
import com.example.crudObsidiana.model.UsoEquipamento;

import com.example.crudObsidiana.repository.EquipamentoRepository;
import com.example.crudObsidiana.repository.ServicoRepository;
import com.example.crudObsidiana.repository.ProfissionalRepository;
import com.example.crudObsidiana.repository.UsuarioRepository;
import com.example.crudObsidiana.repository.OrcamentoRepository;
import com.example.crudObsidiana.repository.UsoEquipamentoRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

@Component
public class SampleDataLoader implements CommandLineRunner {

    // variável para determinar se está usando H2 ou MYSQL
    @Value("${spring.datasource.url}")
    private String dataBaseUrl;

    private final EquipamentoRepository equipamentoRepository;
    private final ServicoRepository servicoRepository;
    private final ProfissionalRepository profissionalRepository;
    private final UsuarioRepository usuarioRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final UsoEquipamentoRepository usoEquipamentoRepository;
    private final PasswordEncoder passwordEncoder;

    public SampleDataLoader(
            EquipamentoRepository equipamentoRepository,
            ServicoRepository servicoRepository,
            ProfissionalRepository profissionalRepository,
            UsuarioRepository usuarioRepository,
            OrcamentoRepository orcamentoRepository,
            UsoEquipamentoRepository usoEquipamentoRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.equipamentoRepository = equipamentoRepository;
        this.servicoRepository = servicoRepository;
        this.profissionalRepository = profissionalRepository;
        this.usuarioRepository = usuarioRepository;
        this.orcamentoRepository = orcamentoRepository;
        this.usoEquipamentoRepository = usoEquipamentoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String[] args) throws Exception {
        if (dataBaseUrl.contains("mysql")) {
            return;
        }

        // ========================
        //  USUÁRIO DE TESTE
        // ========================
        if (usuarioRepository.count() == 0) {
            Usuario u = new Usuario();
            u.setNome("Administrador");
            u.setEmail("admin@obsidiana.com");
            u.setSenha(passwordEncoder.encode("123456"));
            usuarioRepository.save(u);
            System.out.println("✔ Usuário criado: admin@obsidiana.com / 123456");
        }

        // ========================
        //  EQUIPAMENTOS
        // ========================
        if (equipamentoRepository.count() == 0) {

//            Equipamento eq1 = new Equipamento();
//            eq1.setNome("Câmera Canon EOS R6");
//            eq1.setCategoria("Câmeras");
//            eq1.setMarca("Canon");
//            eq1.setModelo("R6");
//            eq1.setNumeroSerie("CAN-R6-001");
//            eq1.setQuantidadeTotal(3);
//            eq1.setQuantidadeDisponivel(3);
//            eq1.setValorPorHora(150.0);
//            eq1 = equipamentoRepository.save(eq1);
//
//            Equipamento eq2 = new Equipamento();
//            eq2.setNome("Ilha de Luz LED 3x3");
//            eq2.setCategoria("Iluminação");
//            eq2.setMarca("Godox");
//            eq2.setModelo("LED-3x3");
//            eq2.setNumeroSerie("GDX-300");
//            eq2.setQuantidadeTotal(5);
//            eq2.setQuantidadeDisponivel(5);
//            eq2.setValorPorHora(40.0);
//            eq2 = equipamentoRepository.save(eq2);
//
//            Equipamento eq3 = new Equipamento();
//            eq3.setNome("Tripé");
//            eq3.setCategoria("Suporte");
//            eq3.setMarca("Tripex");
//            eq3.setModelo("Novo");
//            eq3.setNumeroSerie("DEL-E14");
//            eq3.setQuantidadeTotal(50);
//            eq3.setQuantidadeDisponivel(50);
//            eq3.setValorPorHora(40.0);
//            eq3 = equipamentoRepository.save(eq3);
            for (int i = 1; i <= 20; i++) {
                Equipamento eq = new Equipamento();
                eq.setNome("Equipamento " + i);
                eq.setCategoria(i % 2 == 0 ? "Áudio" : "Vídeo");
                eq.setMarca("Marca " + i);
                eq.setModelo("Modelo X" + i);
                eq.setNumeroSerie("SERIE-" + i);
                eq.setQuantidadeTotal(5 + i);
                eq.setValorPorHora(50.0 + (i * 10));

                eq = equipamentoRepository.save(eq);
            }

            System.out.println("✔ Equipamentos criados");
        }

        // ========================
        //  SERVIÇO
        // ========================
        if (servicoRepository.count() == 0) {

            List<Equipamento> equipamentos = equipamentoRepository.findAll();
            equipamentos.removeLast();

//            Servico serv = new Servico();
//            serv.setNome("Cobertura de Evento - Foto/Vídeo");
//            serv.setDescricao("Serviço com câmera e iluminação.");
//            serv.setHoras(1);
//            serv.setValorPorHora(200.0);
//            serv.setEquipamentos(equipamentos);
//
//            servicoRepository.save(serv);

            for (int i = 1; i <= 20; i++) {
                Servico serv = new Servico();
                serv.setNome("Serviço " + i);
                serv.setDescricao("Descrição do serviço " + i);
                serv.setHoras(2 + i);
                serv.setValorPorHora(100.0 + (i * 20));

                // associa 2 equipamentos por serviço
                serv.setEquipamentos(equipamentos);

                servicoRepository.save(serv);
            }

            System.out.println("✔ Serviços criados");
        }

        // ========================
        //  PROFISSIONAL
        // ========================

        if (profissionalRepository.count() == 0) {

            // Categorias de profissionais de audiovisual para variar os dados de exemplo
            String[] categorias = {"Fotógrafo", "Videógrafo", "Editor", "Diretor de Arte", "Operador de Drone"};

            for (int i = 1; i <= 20; i++) {
                Profissional p = new Profissional(
                        "Profissional " + i,
                        i % 2 == 0 ? "Disponível" : "Ocupado",
                        "profissional" + i + "@email.com"
                );

                // Rotaciona entre as categorias usando o índice do loop
                p.setCategoria(categorias[i % categorias.length]);

                profissionalRepository.save(p);
            }

            System.out.println("✔ Profissionais criados");
        }

        // ========================
        //  ORÇAMENTO (novo bloco)
        // ========================

        if (orcamentoRepository.count() == 0) {
            // Pega entidades já salvas
            List<Servico> servicosExistentes = servicoRepository.findAll();
            List<Equipamento> equipamentosExistentes = equipamentoRepository.findAll();
            List<Profissional> profissionaisExistentes = profissionalRepository.findAll();

            List<String> statusPossiveis = List.of(
                    "Em análise",
                    "Confirmado",
                    "Cancelado"
            );

            equipamentosExistentes.removeLast();

            for (int i = 1; i <= 20; i++) {
                Random random = new Random();
                // intervalo: hoje até 60 dias no futuro
                long agora = System.currentTimeMillis();
                long dias60 = 1000L * 60 * 60 * 24 * 60;
                // gera data inicial aleatória
                long inicioMillis = agora + (long) (random.nextDouble() * dias60);
                Date dataInicio = new Date(inicioMillis);
                // duração entre 2 e 12 horas
                long duracaoHoras = 2 + random.nextInt(10);
                long fimMillis = inicioMillis + (duracaoHoras * 1000L * 60 * 60);
                Date dataTermino = new Date(fimMillis);

                // Monta um orçamento simples usando os campos do model/construtor
                Orcamento orc = new Orcamento(
                        dataInicio,
                        dataTermino,
                        "Cidade " + i, // localEvento
                        "Evento exemplo " + i, // descricao
                        statusPossiveis.get(random.nextInt(statusPossiveis.size())), // status inicial
                        0.0, // valorTotal (será calculado pelo serviço quando necessário)
                        null // idCalendar só é usado após orçamento ser aprovado
                );

                // Persistir para obter ID
                Orcamento salvo = orcamentoRepository.save(orc);

                // Criar alguns usos de equipamento vinculados ao orçamento
                List<UsoEquipamento> usos = new ArrayList<>();

                if (!equipamentosExistentes.isEmpty()) {
                    // usa o primeiro equipamento com quantidade variável
                    Equipamento e1 = equipamentosExistentes.get(i % equipamentosExistentes.size());
                    UsoEquipamento u1 = new UsoEquipamento();
                    u1.setOrcamento(salvo);
                    u1.setEquipamento(e1);
                    u1.setQuantidadeUsada((i % 3) + 1);
                    u1 = usoEquipamentoRepository.save(u1);
                    usos.add(u1);
                }

                if (equipamentosExistentes.size() > 1) {
                    Equipamento e2 = equipamentosExistentes.get((i + 1) % equipamentosExistentes.size());
                    UsoEquipamento u2 = new UsoEquipamento();
                    u2.setOrcamento(salvo);
                    u2.setEquipamento(e2);
                    u2.setQuantidadeUsada((i % 2) + 1);
                    u2 = usoEquipamentoRepository.save(u2);
                    usos.add(u2);
                }

                // vincular listas many-to-many (se existirem)
                if (!servicosExistentes.isEmpty()) {
                    salvo.setServicos(servicosExistentes);
                }
                if (!profissionaisExistentes.isEmpty()) {
                    salvo.setProfissionais(profissionaisExistentes);
                }
                if (!equipamentosExistentes.isEmpty()) {
                    salvo.setEquipamentos(equipamentosExistentes);
                }

                // anexar usos e recalcular valorTotal simplificado (soma valorPorHora * qtd)
                salvo.setUsosEquipamentos(usos);
                double total = 0.0;
                for (UsoEquipamento uso : usos) {
                    if (uso.getEquipamento() != null && uso.getEquipamento().getValorPorHora() != null) {
                        total += uso.getQuantidadeUsada() * uso.getEquipamento().getValorPorHora();
                    }
                }
                salvo.setValorTotal(total);

                // salvar novamente com relações completas
                orcamentoRepository.save(salvo);

                System.out.println("✔ Orçamento de exemplo criado (id=" + salvo.getId() + ")");
            }
        }

        System.out.println("\n🎉 SampleDataLoader finalizado — H2 populado automaticamente!\n");
    }
}