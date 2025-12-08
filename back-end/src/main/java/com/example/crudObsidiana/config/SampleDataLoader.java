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

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

@Component
public class SampleDataLoader implements CommandLineRunner {

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

        // ========================
        //  USUÁRIO DE TESTE
        // ========================
        if (usuarioRepository.count() == 0) {
            Usuario u = new Usuario();
            u.setNome("Usuário Teste");
            u.setEmail("w@w");
            u.setSenha(passwordEncoder.encode("123"));
            usuarioRepository.save(u);
            System.out.println("✔ Usuário criado: w@w / 123");
        }

        // ========================
        //  EQUIPAMENTOS
        // ========================
        if (equipamentoRepository.count() == 0) {

            Equipamento eq1 = new Equipamento();
            eq1.setNome("Câmera Canon EOS R6");
            eq1.setCategoria("Câmeras");
            eq1.setMarca("Canon");
            eq1.setModelo("R6");
            eq1.setNumeroSerie("CAN-R6-001");
            eq1.setQuantidadeTotal(3);
            eq1.setQuantidadeDisponivel(3);
            eq1.setValorPorHora(150.0);
            eq1 = equipamentoRepository.save(eq1);

            Equipamento eq2 = new Equipamento();
            eq2.setNome("Ilha de Luz LED 3x3");
            eq2.setCategoria("Iluminação");
            eq2.setMarca("Godox");
            eq2.setModelo("LED-3x3");
            eq2.setNumeroSerie("GDX-300");
            eq2.setQuantidadeTotal(5);
            eq2.setQuantidadeDisponivel(5);
            eq2.setValorPorHora(40.0);
            eq2 = equipamentoRepository.save(eq2);

            System.out.println("✔ Equipamentos criados");
        }

        // ========================
        //  SERVIÇO
        // ========================
        if (servicoRepository.count() == 0) {

            List<Equipamento> equipamentos = equipamentoRepository.findAll();

            Servico serv = new Servico();
            serv.setNome("Cobertura de Evento - Foto/Vídeo");
            serv.setDescricao("Serviço com câmera e iluminação.");
            serv.setHoras(4);
            serv.setValorPorHora(200.0);
            serv.setEquipamentos(equipamentos);

            servicoRepository.save(serv);

            System.out.println("✔ Serviço criado");
        }

        // ========================
        //  PROFISSIONAL
        // ========================
        if (profissionalRepository.count() == 0) {

            Profissional p = new Profissional(
                    "Hannah Montana",
                    "Disponível",
                    "soueuhannah@email.com"
            );

            profissionalRepository.save(p);

            System.out.println("✔ Profissional criado");
        }

        // ========================
        //  ORÇAMENTO (novo bloco)
        // ========================
        if (orcamentoRepository.count() == 0) {
            // Pega entidades já salvas
            List<Servico> servicosExistentes = servicoRepository.findAll();
            List<Equipamento> equipamentosExistentes = equipamentoRepository.findAll();
            List<Profissional> profissionaisExistentes = profissionalRepository.findAll();

            // Monta um orçamento simples usando os campos do model/construtor
            Orcamento orc = new Orcamento(
                    new Date(),                    // dataInicio
                    new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 4), // dataTermino (+4h)
                    "Malibu Beach",             // localEvento
                    "YeeeEEeAahh....", // descricao
                    "Em análise",                  // status (inicial)
                    0.0                             // valorTotal (será calculado pelo serviço quando necessário)
            );

            // Persistir para obter ID
            Orcamento salvo = orcamentoRepository.save(orc);

            // Criar alguns usos de equipamento vinculados ao orçamento
            List<UsoEquipamento> usos = new ArrayList<>();

            if (!equipamentosExistentes.isEmpty()) {
                // usa o primeiro equipamento com quantidade 1
                Equipamento e1 = equipamentosExistentes.get(0);
                UsoEquipamento u1 = new UsoEquipamento();
                u1.setOrcamento(salvo);
                u1.setEquipamento(e1);
                u1.setQuantidadeUsada(1);
                u1 = usoEquipamentoRepository.save(u1);
                usos.add(u1);
            }

            if (equipamentosExistentes.size() > 1) {
                Equipamento e2 = equipamentosExistentes.get(1);
                UsoEquipamento u2 = new UsoEquipamento();
                u2.setOrcamento(salvo);
                u2.setEquipamento(e2);
                u2.setQuantidadeUsada(1);
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

        System.out.println("\n🎉 SampleDataLoader finalizado — H2 populado automaticamente!\n");
    }
}