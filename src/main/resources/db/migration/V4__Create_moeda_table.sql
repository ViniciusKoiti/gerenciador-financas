CREATE TABLE IF NOT EXISTS moeda (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     codigo VARCHAR(3) NOT NULL UNIQUE,
    simbolo VARCHAR(10) NOT NULL,
    nome VARCHAR(100) NOT NULL,
    casas_decimais INTEGER NOT NULL DEFAULT 2,
    ativo BOOLEAN NOT NULL DEFAULT true,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP
    );

INSERT INTO moeda (codigo, simbolo, nome, casas_decimais, ativo)
SELECT 'BRL', 'R$', 'Real Brasileiro', 2, true
WHERE NOT EXISTS (SELECT 1 FROM moeda WHERE codigo = 'BRL');

INSERT INTO moeda (codigo, simbolo, nome, casas_decimais, ativo)
SELECT 'EUR', '€', 'Euro', 2, true
WHERE NOT EXISTS (SELECT 1 FROM moeda WHERE codigo = 'EUR');

INSERT INTO moeda (codigo, simbolo, nome, casas_decimais, ativo)
SELECT 'USD', '$', 'Dólar Americano', 2, true
WHERE NOT EXISTS (SELECT 1 FROM moeda WHERE codigo = 'USD');

INSERT INTO moeda (codigo, simbolo, nome, casas_decimais, ativo)
SELECT 'GBP', '£', 'Libra Esterlina', 2, true
WHERE NOT EXISTS (SELECT 1 FROM moeda WHERE codigo = 'GBP');

INSERT INTO moeda (codigo, simbolo, nome, casas_decimais, ativo)
SELECT 'CHF', 'CHF', 'Franco Suíço', 2, true
WHERE NOT EXISTS (SELECT 1 FROM moeda WHERE codigo = 'CHF');


CREATE INDEX IF NOT EXISTS idx_currency_active ON moeda (ativo);
CREATE INDEX IF NOT EXISTS idx_currency_codigo ON moeda (codigo);