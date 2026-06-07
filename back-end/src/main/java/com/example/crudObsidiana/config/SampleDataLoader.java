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
            // Dados realistas de um locador de equipamentos audiovisuais.
            // Arrays paralelos: nome[i] usa categoria[i], marca[i] e modelo[i].
            String[] nomesEq = {
                    "Câmera Canon EOS R6",         "Câmera Sony A7 III",          "Câmera Panasonic GH6",
                    "Câmera Blackmagic Pocket 6K", "Câmera GoPro Hero 12",        "Lente Canon RF 24-70mm f/2.8",
                    "Lente Sigma Art 35mm f/1.4",  "Iluminação Aputure 600d Pro", "Iluminação Godox SL-150W",
                    "Softbox Octabox 90cm",        "Painel LED Nanlite PavoTube", "Refletor HMI Arri M18",
                    "Microfone shotgun Rode NTG4+", "Microfone lapela Sennheiser EW100 G4", "Gravador de áudio Zoom H6",
                    "Tripé Manfrotto 055XPRO3",    "Slider Edelkrone SliderPLUS Pro", "Estabilizador DJI Ronin RS3",
                    "Drone DJI Mavic 3",           "Drone DJI Mini 4 Pro"
            };
            String[] categoriasEq = {
                    "Câmeras",    "Câmeras",    "Câmeras",
                    "Câmeras",    "Câmeras",    "Lentes",
                    "Lentes",     "Iluminação", "Iluminação",
                    "Iluminação", "Iluminação", "Iluminação",
                    "Áudio",      "Áudio",      "Áudio",
                    "Suporte",    "Suporte",    "Suporte",
                    "Drones",     "Drones"
            };
            String[] marcasEq = {
                    "Canon",      "Sony",       "Panasonic",
                    "Blackmagic", "GoPro",      "Canon",
                    "Sigma",      "Aputure",    "Godox",
                    "Godox",      "Nanlite",    "Arri",
                    "Rode",       "Sennheiser", "Zoom",
                    "Manfrotto",  "Edelkrone",  "DJI",
                    "DJI",        "DJI"
            };
            String[] modelosEq = {
                    "EOS R6",     "ILCE-7M3",   "GH6",
                    "BMPCC 6K",   "Hero 12",    "RF 24-70 II",
                    "Art 35 1.4", "600d Pro",   "SL-150W",
                    "S-90",       "PavoTube 30C", "M18",
                    "NTG4+",      "EW100 G4",   "H6",
                    "055XPRO3",   "SliderPLUS Pro", "Ronin RS3",
                    "Mavic 3",    "Mini 4 Pro"
            };

            for (int i = 0; i < nomesEq.length; i++) {
                Equipamento eq = new Equipamento();
                eq.setNome(nomesEq[i]);
                eq.setCategoria(categoriasEq[i]);
                eq.setMarca(marcasEq[i]);
                eq.setModelo(modelosEq[i]);
                // Número de série mantém um padrão sintético — não é exibido como destaque.
                eq.setNumeroSerie("SN-" + marcasEq[i].toUpperCase().replace(" ", "") + "-" + String.format("%03d", i + 1));
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

            // Serviços típicos de uma produtora audiovisual.
            String[] nomesServ = {
                    "Cobertura fotográfica de casamento",   "Captação de vídeo institucional",
                    "Edição de vídeo publicitário",         "Produção de podcast em estúdio",
                    "Cobertura de evento corporativo",      "Filmagem de clipe musical",
                    "Pós-produção e color grading",         "Cobertura jornalística",
                    "Filmagem com drone",                   "Sessão fotográfica de produto",
                    "Live streaming multicâmera",           "Vídeo aéreo cinematográfico",
                    "Cobertura de aniversário infantil",    "Sessão fotográfica de moda",
                    "Documentário institucional",           "Cobertura fotográfica de formatura",
                    "Filmagem de palestra e treinamento",   "Vídeo em time-lapse",
                    "Edição de teaser e trailer",           "Vídeo de imóvel para corretora"
            };
            String[] descsServ = {
                    "Cobertura completa da cerimônia e festa, com edição entregue em 30 dias.",
                    "Roteirização, captação e finalização de vídeo institucional curto (até 3 min).",
                    "Edição profissional de peça publicitária com motion graphics e trilha sonora.",
                    "Captação de áudio e vídeo multicâmera em estúdio acústico.",
                    "Registro fotográfico e vídeo do evento, com entrega expressa de teaser em 48h.",
                    "Direção de fotografia, captação e edição de videoclipe musical.",
                    "Tratamento de cor, correção e finalização para vídeos já editados.",
                    "Cobertura ao vivo com transmissão e arquivo final disponibilizado.",
                    "Imagens aéreas com drone homologado, com piloto ANAC.",
                    "Ensaio fotográfico de produtos em estúdio com fundo branco.",
                    "Transmissão ao vivo com até 4 câmeras, switcher e gerador de caracteres.",
                    "Captação aérea cinematográfica com estabilização avançada.",
                    "Cobertura lúdica e dinâmica de festas infantis (ensaios e festa).",
                    "Ensaio editorial de moda em locação ou estúdio.",
                    "Documentário curto para apresentação institucional.",
                    "Cobertura completa da cerimônia de colação de grau e festa.",
                    "Captação multicâmera de palestras, treinamentos e workshops.",
                    "Captação em time-lapse de obras, eventos ou paisagens.",
                    "Edição de teasers, trailers e cortes para redes sociais.",
                    "Vídeo curto de apresentação de imóveis para anúncios e portais."
            };

            for (int i = 0; i < nomesServ.length; i++) {
                Servico serv = new Servico();
                serv.setNome(nomesServ[i]);
                serv.setDescricao(descsServ[i]);
                serv.setHoras(2 + i);
                serv.setValorPorHora(100.0 + (i * 20));

                // associa equipamentos disponíveis ao serviço
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

            // Nomes brasileiros plausíveis e e-mails coerentes (primeiro nome + sobrenome principal).
            String[][] profs = {
                    {"Ana Carolina Souza",   "ana.souza@email.com"},
                    {"Bruno Almeida",        "bruno.almeida@email.com"},
                    {"Camila Ferreira",      "camila.ferreira@email.com"},
                    {"Daniel Oliveira",      "daniel.oliveira@email.com"},
                    {"Eduarda Castro",       "eduarda.castro@email.com"},
                    {"Felipe Ribeiro",       "felipe.ribeiro@email.com"},
                    {"Gabriela Nunes",       "gabriela.nunes@email.com"},
                    {"Henrique Martins",     "henrique.martins@email.com"},
                    {"Isabela Cardoso",      "isabela.cardoso@email.com"},
                    {"João Pedro Silva",     "joao.silva@email.com"},
                    {"Karina Lima",          "karina.lima@email.com"},
                    {"Lucas Mendes",         "lucas.mendes@email.com"},
                    {"Mariana Costa",        "mariana.costa@email.com"},
                    {"Nathália Barros",      "nathalia.barros@email.com"},
                    {"Otávio Pereira",       "otavio.pereira@email.com"},
                    {"Patrícia Rocha",       "patricia.rocha@email.com"},
                    {"Rafael Gomes",         "rafael.gomes@email.com"},
                    {"Sofia Andrade",        "sofia.andrade@email.com"},
                    {"Thiago Borges",        "thiago.borges@email.com"},
                    {"Vinícius Carvalho",    "vinicius.carvalho@email.com"}
            };

            for (int i = 0; i < profs.length; i++) {
                Profissional p = new Profissional(
                        profs[i][0],
                        i % 2 == 0 ? "Disponível" : "Ocupado",
                        profs[i][1]
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

            // Títulos plausíveis de eventos audiovisuais.
            String[] titulosOrc = {
                    "Casamento Silva & Oliveira",       "Aniversário 50 anos Carla",
                    "Conferência TechBR 2026",          "Lançamento produto NovaTech",
                    "Cobertura formatura UFMG",         "Workshop de fotografia avançada",
                    "Evento corporativo Banco Alfa",    "Festival gastronômico do Rio",
                    "Show beneficente Hospital Sírio",  "Casamento Marina & Diego",
                    "Sessão de book maternidade",       "Vídeo institucional Empresa X",
                    "Cobertura de bodas de prata",      "Inauguração loja conceito",
                    "Documentário ONG Esperança",       "Aniversário 15 anos Beatriz",
                    "Cobertura de sessão legislativa",  "Filmagem de prédio comercial",
                    "Evento de moda Verão 2026",        "Convenção anual Cooperativa"
            };
            // Locais plausíveis: cidade + UF.
            String[] locaisOrc = {
                    "São Paulo, SP",      "Rio de Janeiro, RJ", "Belo Horizonte, MG",
                    "Curitiba, PR",       "Porto Alegre, RS",   "Salvador, BA",
                    "Recife, PE",         "Fortaleza, CE",      "Brasília, DF",
                    "Manaus, AM",         "Florianópolis, SC",  "Goiânia, GO",
                    "Natal, RN",          "Vitória, ES",        "Campinas, SP",
                    "Niterói, RJ",        "João Pessoa, PB",    "São Luís, MA",
                    "Cuiabá, MT",         "Belém, PA"
            };
            // Observações curtas e variadas.
            String[] obsOrc = {
                    "Cerimônia ao ar livre, cerca de 200 convidados. Cliente solicitou álbum impresso.",
                    "Festa em salão fechado, 80 convidados. Pediu cobertura em estilo documental.",
                    "Auditório com 500 lugares, palestra principal de 2 horas.",
                    "Lançamento com coletiva de imprensa e tour pelo showroom.",
                    "Cobertura da cerimônia e da festa pós-formatura. Entrega expressa em 7 dias.",
                    "Workshop presencial de 2 dias, com captação de bastidores.",
                    "Encontro de lideranças, sala de conferências em hotel 5 estrelas.",
                    "Cobertura de 3 dias, vários ambientes e shows simultâneos.",
                    "Show com 6 atrações musicais, transmissão simultânea em redes sociais.",
                    "Cerimônia religiosa e festa em buffet, atenção especial à decoração.",
                    "Ensaio em estúdio externo, com luz natural ao final da tarde.",
                    "Roteiro pré-aprovado pelo cliente, narração em off em estúdio.",
                    "Renovação de votos em ambiente intimista, somente família e padrinhos.",
                    "Coquetel de inauguração para imprensa e clientes VIP.",
                    "Documentário de 15 minutos sobre projeto social, entrega em 60 dias.",
                    "Festa temática com mudança de figurino, valsa coreografada.",
                    "Cobertura jornalística da sessão pública, sem áudio dos parlamentares.",
                    "Tour fotográfico e em vídeo das salas para anúncio comercial.",
                    "Desfile com 12 modelos, 3 trocas de figurino e backstage.",
                    "Convenção de 2 dias com palestras, premiações e jantar de gala."
            };

            // Random fora do loop: usar um único Random é mais eficiente e gera melhor distribuição.
            Random random = new Random();

            for (int i = 0; i < titulosOrc.length; i++) {
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
                        titulosOrc[i], // titulo
                        locaisOrc[i],  // localEvento
                        obsOrc[i],     // observacoes
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

                // anexar usos e recalcular valorTotal completo (serviços + equipamentos)
                salvo.setUsosEquipamentos(usos);
                double total = 0.0;

                // Serviços: valorPorHora × horas
                if (salvo.getServicos() != null) {
                    for (Servico s : salvo.getServicos()) {
                        if (s == null) continue;
                        double vHora = (s.getValorPorHora() == null) ? 0.0 : s.getValorPorHora();
                        int horas = s.getHoras();
                        total += vHora * horas;
                    }
                }

                // Equipamentos: valorPorHora × quantidadeUsada
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