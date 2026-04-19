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
    local_evento VARCHAR(255),
    descricao VARCHAR(255),
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
    nome_arquivo_imagem VARCHAR(255)
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
INSERT INTO
    usuario (nome, email, senha)
    VALUE
    ('Administrador', 'admin@obsidiana.com', '$2a$10$t3vFeR7Adnwv71M5HY/uGOqTyZuE7TPUCOSo0HKP0wAIrHg9q1b.u');

-- EQUIPAMENTO
INSERT INTO
    equipamento (nome, categoria, marca, modelo, numero_serie, quantidade_total, quantidade_disponivel, valor_por_hora)
    VALUES
    ('Câmera Canon EOS R6', 'Câmeras', 'Canon', 'R6', 'CAN-R6-001', 3, 3, 150),
    ('Ilha de Luz LED 3x3', 'Iluminação', 'Godox', 'LED-3x3', 'GDX-300', 5, 5, 40.0),
    ('Tripé', 'Suporte', 'Tripex', 'Novo', 'DEL-E14', 50, 50, 40.0);

-- SERVICO
INSERT INTO
    servico (nome, descricao, horas, valor_por_hora)
    VALUES
    ('Cobertura de Evento - Foto/Vídeo', 'Serviço com câmera e iluminação.', 1, 200.0);

-- SERVICO_EQUIPAMENTO
INSERT INTO
    servico_equipamento (servico_id, equipamento_id)
    VALUES
    (1, 1),
    (1, 2);

-- PROFISSIONAL
INSERT INTO
    profissional (nome, disponibilidade, contato, categoria)
    VALUE
    ('Haidê Landim', 'Disponível', 'haide.landim@outlook.com', 'Fotógrafa');

-- ORCAMENTO
INSERT INTO
    orcamento (data_inicio, data_termino, local_evento, descricao, status, valor_total)
    VALUE
    (CURRENT_DATE(), DATE_ADD(NOW(), INTERVAL 4 HOUR), 'São Paulo', 'Gravação de frigorífico industrial', 'Em análise', 0.0);

-- ORCAMENTO_EQUIPAMENTOS
INSERT INTO
    orcamento_equipamentos (orcamento_id, equipamento_id)
    VALUES
    (1, 1),
    (1, 2);

-- USO_EQUIPAMENTO
INSERT INTO
    uso_equipamento (fk_orcamento, fk_equipamento, quantidade_usada)
    VALUES
    (1, 1, 1),
    (1, 2, 1);