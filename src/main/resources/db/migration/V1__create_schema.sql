CREATE TABLE IF NOT EXISTS usuario (
                                       id BIGSERIAL PRIMARY KEY,
                                       cpf VARCHAR(14) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    telefone VARCHAR(20),
    data_nascimento DATE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP,
    criado_por VARCHAR(255),
    atualizado_por VARCHAR(255)
    );



CREATE TABLE pessoa (
                        id BIGINT PRIMARY KEY,
                        nome VARCHAR(255) NOT NULL,
                        cpf VARCHAR(14) NOT NULL UNIQUE,
                        email VARCHAR(255) NOT NULL UNIQUE,
                        telefone VARCHAR(20),
                        data_nascimento DATE,
                        logradouro VARCHAR(255),
                        numero_endereco VARCHAR(10),
                        complemento VARCHAR(100),
                        bairro VARCHAR(100),
                        cidade VARCHAR(100),
                        estado VARCHAR(2),
                        cep VARCHAR(9)
);

CREATE TABLE cliente (
                         id BIGINT PRIMARY KEY,
                         usuario_id BIGINT,
                         pix_chave VARCHAR(255),
                         pix_tipo VARCHAR(50),
                         pix_banco VARCHAR(100),
                         pix_ativo BOOLEAN DEFAULT TRUE,
                         criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         criado_por VARCHAR(100),
                         atualizado_por VARCHAR(100),
                         ativo BOOLEAN DEFAULT TRUE,
                         FOREIGN KEY (id) REFERENCES pessoa(id) ON DELETE CASCADE,
                         FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);





CREATE TABLE IF NOT EXISTS categoria (
                                         id BIGSERIAL PRIMARY KEY,
                                         nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    ativa BOOLEAN NOT NULL DEFAULT TRUE,
    icone VARCHAR(100),
    categoria_pai_id BIGINT,
    usuario_id BIGINT NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP,
    criado_por VARCHAR(255),
    atualizado_por VARCHAR(255),
    CONSTRAINT fk_categoria_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_categoria_pai FOREIGN KEY (categoria_pai_id) REFERENCES categoria(id) ON DELETE SET NULL
    );


CREATE TABLE IF NOT EXISTS transacoes (
                                          id BIGSERIAL PRIMARY KEY,
                                          descricao VARCHAR(255) NOT NULL,
    valor DECIMAL(19,2) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    data TIMESTAMP NOT NULL,
    observacoes TEXT,
    usuario_id BIGINT NOT NULL,
    categoria_id BIGINT NOT NULL,
    cliente_id BIGINT,

    pago BOOLEAN DEFAULT FALSE,
    recorrente BOOLEAN DEFAULT FALSE,
    periodicidade INTEGER,
    tipo_recorrencia VARCHAR(50),
    ignorar_limite_categoria BOOLEAN DEFAULT FALSE,
    ignorar_orcamento BOOLEAN DEFAULT FALSE,
    parcelado BOOLEAN DEFAULT FALSE,

    data_pagamento TIMESTAMP,
    data_vencimento DATE, -- Mudança de TIMESTAMP para DATE

    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP,
    criado_por VARCHAR(255),
    atualizado_por VARCHAR(255),

    -- Constraints
    CONSTRAINT fk_transacao_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_transacao_categoria FOREIGN KEY (categoria_id) REFERENCES categoria(id) ON DELETE RESTRICT,
    CONSTRAINT fk_transacao_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE SET NULL
    );

-- Criar tabela comprovante_fiscais
CREATE TABLE IF NOT EXISTS comprovante_fiscais (
                                                   id BIGSERIAL PRIMARY KEY,
                                                   transacao_id BIGINT NOT NULL,
                                                   tipo SMALLINT NOT NULL, -- CUPOM_FISCAL, NOTA_FISCAL, RECIBO, etc.
    numero_documento VARCHAR(100),
    hash VARCHAR(255),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP,
    criado_por VARCHAR(255),
    atualizado_por VARCHAR(255),
    CONSTRAINT fk_comprovante_transacao FOREIGN KEY (transacao_id) REFERENCES transacoes(id) ON DELETE CASCADE
    );

-- Criar tabela anexos
CREATE TABLE IF NOT EXISTS anexos (
                                      id BIGSERIAL PRIMARY KEY,
                                      nome_original VARCHAR(255),
    nome_arquivo VARCHAR(255),
    tipo_arquivo VARCHAR(100),
    tamanho BIGINT,
    caminho_arquivo VARCHAR(500),
    tipo VARCHAR(50), -- Enum: IMAGEM, PDF, DOCUMENTO, etc.
    data_upload TIMESTAMP,
    hash_arquivo VARCHAR(255),
    comprovante_fiscal_id BIGINT NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP,
    criado_por VARCHAR(255),
    atualizado_por VARCHAR(255),
    CONSTRAINT fk_anexo_comprovante FOREIGN KEY (comprovante_fiscal_id) REFERENCES comprovante_fiscais(id) ON DELETE CASCADE
    );


-- Criar índices para performance
CREATE INDEX IF NOT EXISTS idx_usuario_email ON usuario(email);
CREATE INDEX IF NOT EXISTS idx_usuario_cpf ON usuario(cpf);
CREATE INDEX IF NOT EXISTS idx_categoria_usuario ON categoria(usuario_id);
CREATE INDEX IF NOT EXISTS idx_categoria_ativa ON categoria(ativa);
CREATE INDEX IF NOT EXISTS idx_transacao_usuario ON transacoes(usuario_id);
CREATE INDEX IF NOT EXISTS idx_transacao_categoria ON transacoes(categoria_id);
CREATE INDEX IF NOT EXISTS idx_transacao_data ON transacoes(data);
CREATE INDEX IF NOT EXISTS idx_transacao_tipo ON transacoes(tipo);


