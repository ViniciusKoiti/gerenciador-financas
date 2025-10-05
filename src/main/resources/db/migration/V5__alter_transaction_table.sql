-- V5: Adicionar suporte a MontanteMonetario na tabela transacoes

ALTER TABLE transacoes 
    ADD COLUMN valor_definitivo DECIMAL(19,6),
    ADD COLUMN valor_moeda_id VARCHAR(3);

-- Adicionar foreign key para moeda
ALTER TABLE transacoes 
    ADD CONSTRAINT fk_transacao_moeda 
    FOREIGN KEY (valor_moeda_id) REFERENCES moeda(codigo) ON DELETE RESTRICT;

-- Migrar dados existentes para a nova estrutura
-- Todas as transações existentes usam BRL como moeda padrão
UPDATE transacoes 
SET valor_definitivo = valor,
    valor_moeda_id = 'BRL'
WHERE valor_definitivo IS NULL;

-- Tornar as novas colunas obrigatórias após migração
ALTER TABLE transacoes 
    ALTER COLUMN valor_definitivo SET NOT NULL,
    ALTER COLUMN valor_moeda_id SET NOT NULL;

-- Criar índice para performance em consultas por moeda
CREATE INDEX idx_transacao_moeda ON transacoes(valor_moeda_id);

-- Adicionar comentários para documentação
COMMENT ON COLUMN transacoes.valor_definitivo IS 'Valor da transação em formato decimal com 6 casas decimais para suporte multi-currency';
COMMENT ON COLUMN transacoes.valor_moeda_id IS 'Código ISO da moeda (FK para tabela moeda)';

-- Nota: O campo 'valor' original é mantido para compatibilidade com versões anteriores
-- mas será depreciado em futuras versões