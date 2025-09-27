CREATE IF NOT EXISTS TABLE currency (
                          code VARCHAR(3) PRIMARY KEY,
                          symbol VARCHAR(10) NOT NULL,
                          display_name VARCHAR(100) NOT NULL,
                          decimal_places INTEGER NOT NULL DEFAULT 2,
                          active BOOLEAN NOT NULL DEFAULT true,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          created_by VARCHAR(255),
                          updated_by VARCHAR(255)
);

INSERT INTO currency (code, symbol, display_name, decimal_places, active) VALUES
                                                                              ('BRL', 'R$', 'Real Brasileiro', 2, true),
                                                                              ('EUR', '€', 'Euro', 2, true),
                                                                              ('USD', '$', 'Dólar Americano', 2, true),
                                                                              ('GBP', '£', 'Libra Esterlina', 2, true),
                                                                              ('CHF', 'CHF', 'Franco Suíço', 2, true);

CREATE INDEX idx_currency_active ON currency (active);