CREATE DATABASE IF NOT EXISTS obsidiana;
USE obsidiana;

-- TABELAS-BASE
CREATE TABLE IF NOT EXISTS equipamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255),
    quantidade_total INT,
    quantidade_disponivel INT,
    categoria VARCHAR(255),
    marca VARCHAR(255),
    numero_serie VARCHAR(255),
    modelo VARCHAR(255),
    valor_por_hora DOUBLE,
    nome_arquivo_imagem VARCHAR(255),
    tipo_imagem VARCHAR(255),
    caminho_imagem VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS orcamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    data_inicio DATETIME,
    data_termino DATETIME,
    titulo varchar(255),
    local_evento VARCHAR(255),
    observacoes VARCHAR(255),
    status VARCHAR(100),
    valor_total DOUBLE,
    id_calendario VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS profissional (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255),
    disponibilidade VARCHAR(255),
    contato VARCHAR(255),
    categoria VARCHAR(255),
    nome_arquivo_imagem VARCHAR(255),
    caminho_imagem VARCHAR(500),
    tipo_imagem VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS servico (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255),
    descricao varchar(255),
    horas INT,
    valor_por_hora DOUBLE
);
    
CREATE TABLE IF NOT EXISTS usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255),
    email VARCHAR(255),
    senha VARCHAR(1000),
    nome_arquivo_imagem VARCHAR(255),
    tipo_imagem VARCHAR(100),
    caminho_imagem VARCHAR(500)
);

-- TABELAS DE JUNÇÃO
-- ORÇAMENTO
CREATE TABLE IF NOT EXISTS orcamento_servicos (
    orcamento_id BIGINT NOT NULL,
    servico_id BIGINT NOT NULL,
    PRIMARY KEY (orcamento_id, servico_id),

    CONSTRAINT fk_orcserv_orcamento
        FOREIGN KEY (orcamento_id) REFERENCES orcamento(id),

    CONSTRAINT fk_orcserv_servico
        FOREIGN KEY (servico_id) REFERENCES servico(id)
);

CREATE TABLE IF NOT EXISTS orcamento_equipamentos (
    orcamento_id BIGINT NOT NULL,
    equipamento_id BIGINT NOT NULL,
    PRIMARY KEY (orcamento_id, equipamento_id),

    CONSTRAINT fk_orceqp_orcamento
        FOREIGN KEY (orcamento_id) REFERENCES orcamento(id),

    CONSTRAINT fk_orceqp_equipamento
        FOREIGN KEY (equipamento_id) REFERENCES equipamento(id)
);

CREATE TABLE IF NOT EXISTS orcamento_profissionais (
    orcamento_id BIGINT NOT NULL,
    profissional_id BIGINT NOT NULL,
    PRIMARY KEY (orcamento_id, profissional_id),

    CONSTRAINT fk_orcprof_orcamento
        FOREIGN KEY (orcamento_id) REFERENCES orcamento(id),

    CONSTRAINT fk_orcprof_profissional
        FOREIGN KEY (profissional_id) REFERENCES profissional(id)
);

-- SERVIÇO
CREATE TABLE IF NOT EXISTS servico_equipamento (
    servico_id BIGINT NOT NULL,
    equipamento_id BIGINT NOT NULL,
    PRIMARY KEY (servico_id, equipamento_id),

    CONSTRAINT fk_serveqp_servico
        FOREIGN KEY (servico_id) REFERENCES servico(id),

    CONSTRAINT fk_serveqp_equipamento
        FOREIGN KEY (equipamento_id) REFERENCES equipamento(id)
);

-- TABELA DE RELACIONAMENTO: uso_equipamento
CREATE TABLE IF NOT EXISTS uso_equipamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quantidade_usada INT NOT NULL,
    fk_equipamento BIGINT NOT NULL,
    fk_orcamento BIGINT,
    fk_servico BIGINT,

    CONSTRAINT fk_usoEquip_equipamento
        FOREIGN KEY (fk_equipamento) REFERENCES equipamento(id),

    CONSTRAINT fk_usoEquip_orcamento
        FOREIGN KEY (fk_orcamento) REFERENCES orcamento(id),

    CONSTRAINT fk_usoEquip_servico
        FOREIGN KEY (fk_servico) REFERENCES servico(id),

    -- Garante que fkOrcamento e fkServico não sejam preenchidos juntos
    CONSTRAINT chk_dono_unico
        CHECK (
            (fk_orcamento IS NOT NULL AND fk_servico IS NULL)
            OR
            (fk_orcamento IS NULL AND fk_servico IS NOT NULL)
        )
);

-- INSERÇÃO DE VALORES
-- USUARIO
INSERT INTO usuario (nome, email, senha) VALUE
('Administrador', 'admin@obsidiana.com', '$2a$10$t3vFeR7Adnwv71M5HY/uGOqTyZuE7TPUCOSo0HKP0wAIrHg9q1b.u');

-- PROFISSIONAL
INSERT INTO profissional (nome, categoria, contato, disponibilidade) VALUES
('Ana Carolina Souza', 'Fotógrafo', 'ana.souza@email.com', 'Disponível'),
('Bruno Almeida', 'Videógrafo', 'bruno.almeida@email.com', 'Dias úteis'),
('Camila Ferreira', 'Editor', 'camila.ferreira@email.com', 'Aos fins de semana'),
('Daniel Oliveira', 'Diretor de Arte', 'daniel.oliveira@email.com', 'Somente às segundas'),
('Eduarda Castro', 'Operador de Drone', 'eduarda.castro@email.com', 'Terças e quintas'),
('Felipe Ribeiro', 'Fotógrafo', 'felipe.ribeiro@email.com', 'Período noturno'),
('Gabriela Nunes', 'Videógrafo', 'gabriela.nunes@email.com', 'Sob agendamento'),
('Henrique Martins', 'Editor', 'henrique.martins@email.com', 'Ocupado até o fim do mês'),
('Isabela Cardoso', 'Diretor de Arte', 'isabela.cardoso@email.com', 'Disponível a partir de 15/06'),
('João Pedro Silva', 'Operador de Drone', 'joao.silva@email.com', 'Apenas meio período'),
('Karina Lima', 'Fotógrafo', 'karina.lima@email.com', 'Disponível'),
('Lucas Mendes', 'Videógrafo', 'lucas.mendes@email.com', 'Dias úteis'),
('Mariana Costa', 'Editor', 'mariana.costa@email.com', 'Aos fins de semana'),
('Nathália Barros', 'Diretor de Arte', 'nathalia.barros@email.com', 'Somente às segundas'),
('Otávio Pereira', 'Operador de Drone', 'otavio.pereira@email.com', 'Terças e quintas'),
('Patrícia Rocha', 'Fotógrafo', 'patricia.rocha@email.com', 'Período noturno'),
('Rafael Gomes', 'Videógrafo', 'rafael.gomes@email.com', 'Sob agendamento'),
('Sofia Andrade', 'Editor', 'sofia.andrade@email.com', 'Ocupado até o fim do mês'),
('Thiago Borges', 'Diretor de Arte', 'thiago.borges@email.com', 'Disponível a partir de 15/06'),
('Vinícius Carvalho', 'Operador de Drone', 'vinicius.carvalho@email.com', 'Apenas meio período');

--  EQUIPAMENTO
INSERT INTO equipamento (nome, categoria, marca, modelo, numero_serie, quantidade_total, valor_por_hora) VALUES
('Câmera Canon EOS R6', 'Câmeras', 'Canon', 'EOS R6', 'SN-CANON-001', 5, 50.0),
('Câmera Sony A7 III', 'Câmeras', 'Sony', 'ILCE-7M3', 'SN-SONY-002', 6, 60.0),
('Câmera Panasonic GH6', 'Câmeras', 'Panasonic', 'GH6', 'SN-PANASONIC-003', 7, 70.0),
('Câmera Blackmagic Pocket 6K', 'Câmeras', 'Blackmagic', 'BMPCC 6K', 'SN-BLACKMAGIC-004', 8, 80.0),
('Câmera GoPro Hero 12', 'Câmeras', 'GoPro', 'Hero 12', 'SN-GOPRO-005', 9, 90.0),
('Lente Canon RF 24-70mm f/2.8', 'Lentes', 'Canon', 'RF 24-70 II', 'SN-CANON-006', 10, 100.0),
('Lente Sigma Art 35mm f/1.4', 'Lentes', 'Sigma', 'Art 35 1.4', 'SN-SIGMA-007', 11, 110.0),
('Iluminação Aputure 600d Pro', 'Iluminação', 'Aputure', '600d Pro', 'SN-APUTURE-008', 12, 120.0),
('Iluminação Godox SL-150W', 'Iluminação', 'Godox', 'SL-150W', 'SN-GODOX-009', 13, 130.0),
('Softbox Octabox 90cm', 'Iluminação', 'Godox', 'S-90', 'SN-GODOX-010', 14, 140.0),
('Painel LED Nanlite PavoTube', 'Iluminação', 'Nanlite', 'PavoTube 30C', 'SN-NANLITE-011', 15, 150.0),
('Refletor HMI Arri M18', 'Iluminação', 'Arri', 'M18', 'SN-ARRI-012', 16, 160.0),
('Microfone shotgun Rode NTG4+', 'Áudio', 'Rode', 'NTG4+', 'SN-RODE-013', 17, 170.0),
('Microfone lapela Sennheiser EW100 G4', 'Áudio', 'Sennheiser', 'EW100 G4', 'SN-SENNHEISER-014', 18, 180.0),
('Gravador de áudio Zoom H6', 'Áudio', 'Zoom', 'H6', 'SN-ZOOM-015', 19, 190.0),
('Tripé Manfrotto 055XPRO3', 'Suporte', 'Manfrotto', '055XPRO3', 'SN-MANFROTTO-016', 20, 200.0),
('Slider Edelkrone SliderPLUS Pro', 'Suporte', 'Edelkrone', 'SliderPLUS Pro', 'SN-EDELKRONE-017', 21, 210.0),
('Estabilizador DJI Ronin RS3', 'Suporte', 'DJI', 'Ronin RS3', 'SN-DJI-018', 22, 220.0),
('Drone DJI Mavic 3', 'Drones', 'DJI', 'Mavic 3', 'SN-DJI-019', 23, 230.0),
('Drone DJI Mini 4 Pro', 'Drones', 'DJI', 'Mini 4 Pro', 'SN-DJI-020', 24, 240.0);

--  SERVICO
INSERT INTO servico (nome, descricao, horas, valor_por_hora) VALUES
('Cobertura fotográfica de casamento', 'Cobertura completa da cerimônia e festa, com edição entregue em 30 dias.', 2, 100.0),
('Captação de vídeo institucional', 'Roteirização, captação e finalização de vídeo institucional curto (até 3 min).', 3, 120.0),
('Edição de vídeo publicitário', 'Edição profissional de peça publicitária com motion graphics e trilha sonora.', 4, 140.0),
('Produção de podcast em estúdio', 'Captação de áudio e vídeo multicâmera em estúdio acústico.', 5, 160.0),
('Cobertura de evento corporativo', 'Registro fotográfico e vídeo do evento, com entrega expressa de teaser em 48h.', 6, 180.0),
('Filmagem de clipe musical', 'Direção de fotografia, captação e edição de videoclipe musical.', 7, 200.0),
('Pós-produção e color grading', 'Tratamento de cor, correção e finalização para vídeos já editados.', 8, 220.0),
('Cobertura jornalística', 'Cobertura ao vivo com transmissão e arquivo final disponibilizado.', 9, 240.0),
('Filmagem com drone', 'Imagens aéreas com drone homologado, com piloto ANAC.', 10, 260.0),
('Sessão fotográfica de produto', 'Ensaio fotográfico de produtos em estúdio com fundo branco.', 11, 280.0),
('Live streaming multicâmera', 'Transmissão ao vivo com até 4 câmeras, switcher e gerador de caracteres.', 12, 300.0),
('Vídeo aéreo cinematográfico', 'Captação aérea cinematográfica com estabilização avançada.', 13, 320.0),
('Cobertura de aniversário infantil', 'Cobertura lúdica e dinâmica de festas infantis (ensaios e festa).', 14, 340.0),
('Sessão fotográfica de moda', 'Ensaio editorial de moda em locação ou estúdio.', 15, 360.0),
('Documentário institucional', 'Documentário curto para apresentação institucional.', 16, 380.0),
('Cobertura fotográfica de formatura', 'Cobertura completa da cerimônia de colação de grau e festa.', 17, 400.0),
('Filmagem de palestra e treinamento', 'Captação multicâmera de palestras, treinamentos e workshops.', 18, 420.0),
('Vídeo em time-lapse', 'Captação em time-lapse de obras, eventos ou paisagens.', 19, 440.0),
('Edição de teaser e trailer', 'Edição de teasers, trailers e cortes para redes sociais.', 20, 460.0),
('Vídeo de imóvel para corretora', 'Vídeo curto de apresentação de imóveis para anúncios e portais.', 21, 480.0);

--  ORCAMENTO
INSERT INTO orcamento (data_inicio, data_termino, titulo, local_evento, observacoes, status) VALUES
(DATE_ADD(NOW(), INTERVAL 5 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 5 DAY), INTERVAL 8 HOUR), 'Casamento Silva & Oliveira', 'São Paulo, SP', 'Cerimônia ao ar livre, cerca de 200 convidados. Cliente solicitou álbum impresso.', 'Em análise'),
(DATE_ADD(NOW(), INTERVAL 10 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 10 DAY), INTERVAL 6 HOUR), 'Aniversário 50 anos Carla', 'Rio de Janeiro, RJ', 'Festa em salão fechado, 80 convidados. Pediu cobertura em estilo documental.', 'Confirmado'),
(DATE_ADD(NOW(), INTERVAL 15 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 15 DAY), INTERVAL 10 HOUR), 'Conferência TechBR 2026', 'Belo Horizonte, MG', 'Auditório com 500 lugares, palestra principal de 2 horas.', 'Cancelado'),
(DATE_ADD(NOW(), INTERVAL 20 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 20 DAY), INTERVAL 7 HOUR), 'Lançamento produto NovaTech', 'Curitiba, PR', 'Lançamento com coletiva de imprensa e tour pelo showroom.', 'Em análise'),
(DATE_ADD(NOW(), INTERVAL 25 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 25 DAY), INTERVAL 9 HOUR), 'Cobertura formatura UFMG', 'Porto Alegre, RS', 'Cobertura da cerimônia e da festa pós-formatura. Entrega expressa em 7 dias.', 'Confirmado'),
(DATE_ADD(NOW(), INTERVAL 30 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 30 DAY), INTERVAL 5 HOUR), 'Workshop de fotografia avançada', 'Salvador, BA', 'Workshop presencial de 2 dias, com captação de bastidores.', 'Em análise'),
(DATE_ADD(NOW(), INTERVAL 8 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 8 DAY), INTERVAL 11 HOUR), 'Evento corporativo Banco Alfa', 'Recife, PE', 'Encontro de lideranças, sala de conferências em hotel 5 estrelas.', 'Confirmado'),
(DATE_ADD(NOW(), INTERVAL 12 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 12 DAY), INTERVAL 4 HOUR), 'Festival gastronômico do Rio', 'Fortaleza, CE', 'Cobertura de 3 dias, vários ambientes e shows simultâneos.', 'Cancelado'),
(DATE_ADD(NOW(), INTERVAL 18 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 18 DAY), INTERVAL 8 HOUR), 'Show beneficente Hospital Sírio', 'Brasília, DF', 'Show com 6 atrações musicais, transmissão simultânea em redes sociais.', 'Em análise'),
(DATE_ADD(NOW(), INTERVAL 22 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 22 DAY), INTERVAL 6 HOUR), 'Casamento Marina & Diego', 'Manaus, AM', 'Cerimônia religiosa e festa em buffet, atenção especial à decoração.', 'Confirmado'),
(DATE_ADD(NOW(), INTERVAL 7 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 7 DAY), INTERVAL 3 HOUR), 'Sessão de book maternidade', 'Florianópolis, SC', 'Ensaio em estúdio externo, com luz natural ao final da tarde.', 'Em análise'),
(DATE_ADD(NOW(), INTERVAL 14 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 14 DAY), INTERVAL 7 HOUR), 'Vídeo institucional Empresa X', 'Goiânia, GO', 'Roteiro pré-aprovado pelo cliente, narração em off em estúdio.', 'Confirmado'),
(DATE_ADD(NOW(), INTERVAL 19 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 19 DAY), INTERVAL 5 HOUR), 'Cobertura de bodas de prata', 'Natal, RN', 'Renovação de votos em ambiente intimista, somente família e padrinhos.', 'Cancelado'),
(DATE_ADD(NOW(), INTERVAL 24 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 24 DAY), INTERVAL 9 HOUR), 'Inauguração loja conceito', 'Vitória, ES', 'Coquetel de inauguração para imprensa e clientes VIP.', 'Em análise'),
(DATE_ADD(NOW(), INTERVAL 28 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 28 DAY), INTERVAL 8 HOUR), 'Documentário ONG Esperança', 'Campinas, SP', 'Documentário de 15 minutos sobre projeto social, entrega em 60 dias.', 'Confirmado'),
(DATE_ADD(NOW(), INTERVAL 6 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 6 DAY), INTERVAL 6 HOUR), 'Aniversário 15 anos Beatriz', 'Niterói, RJ', 'Festa temática com mudança de figurino, valsa coreografada.', 'Em análise'),
(DATE_ADD(NOW(), INTERVAL 12 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 12 DAY), INTERVAL 7 HOUR), 'Cobertura de sessão legislativa', 'João Pessoa, PB', 'Cobertura jornalística da sessão pública, sem áudio dos parlamentares.', 'Cancelado'),
(DATE_ADD(NOW(), INTERVAL 23 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 23 DAY), INTERVAL 11 HOUR), 'Filmagem de prédio comercial', 'São Luís, MA', 'Tour fotográfico e em vídeo das salas para anúncio comercial.', 'Em análise'),
(DATE_ADD(NOW(), INTERVAL 35 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 35 DAY), INTERVAL 5 HOUR), 'Evento de moda Verão 2026', 'Cuiabá, MT', 'Desfile com 12 modelos, 3 trocas de figurino e backstage.', 'Em análise'),
(DATE_ADD(NOW(), INTERVAL 48 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 48 DAY), INTERVAL 14 HOUR), 'Convenção anual Cooperativa', 'Belém, PA', 'Convenção de 2 dias com palestras, premiações e jantar de gala.', 'Confirmado');