CREATE TABLE IF NOT EXISTS moeda (
                                        id BIGSERIAL PRIMARY KEY,
                                        codigo VARCHAR(3) NOT NULL UNIQUE,
    simbolo VARCHAR(10) NOT NULL,
    nome VARCHAR(100) NOT NULL,
    casas_decimais INTEGER NOT NULL DEFAULT 2,
    ativo BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
    );

INSERT INTO currency (codigo, simbolo, nome, casas_decimais, ativo)
SELECT * FROM (VALUES
                   ('BRL', 'R, 'Real Brasileiro', 2, true),
    ('EUR', '€', 'Euro', 2, true),
    ('USD', ', 'Dólar Americano', 2, true),
                   ('GBP', '£', 'Libra Esterlina', 2, true),
                   ('CHF', 'CHF', 'Franco Suíço', 2, true)
              ) AS new_currencies(codigo, simbolo, nome, casas_decimais, ativo)
WHERE NOT EXISTS (
    SELECT 1 FROM currency WHERE currency.codigo = new_currencies.codigo
);

CREATE INDEX IF NOT EXISTS idx_currency_active ON currency (ativo);
CREATE INDEX IF NOT EXISTS idx_currency_codigo ON currency (codigo);