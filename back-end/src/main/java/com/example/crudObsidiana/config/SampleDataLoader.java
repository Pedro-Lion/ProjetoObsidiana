package com.example.crudObsidiana.config;

import com.example.crudObsidiana.model.Equipamento;
import com.example.crudObsidiana.model.Servico;
import com.example.crudObsidiana.model.Profissional;
import com.example.crudObsidiana.model.Usuario;

import com.example.crudObsidiana.repository.EquipamentoRepository;
import com.example.crudObsidiana.repository.ServicoRepository;
import com.example.crudObsidiana.repository.ProfissionalRepository;
import com.example.crudObsidiana.repository.UsuarioRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class SampleDataLoader implements CommandLineRunner {

    private final EquipamentoRepository equipamentoRepository;
    private final ServicoRepository servicoRepository;
    private final ProfissionalRepository profissionalRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public SampleDataLoader(
            EquipamentoRepository equipamentoRepository,
            ServicoRepository servicoRepository,
            ProfissionalRepository profissionalRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.equipamentoRepository = equipamentoRepository;
        this.servicoRepository = servicoRepository;
        this.profissionalRepository = profissionalRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

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
                    "Ana Souza",
                    "Disponível",
                    "ana.souza@email.com"
            );

            profissionalRepository.save(p);

            System.out.println("✔ Profissional criado");
        }

        System.out.println("\n🎉 SampleDataLoader finalizado — H2 populado automaticamente!\n");
    }
}