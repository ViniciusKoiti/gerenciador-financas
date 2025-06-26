INSERT INTO usuario (email, senha, nome, criado_em) VALUES ('viniciusnakahara@gmail.com', '$2a$10$rm8uzhw5KT.cL9aO9sjFguYEoo79ixE4ORCng6XMiJ8dnl.cbmyPa', 'Exemplo', CURRENT_TIMESTAMP());

INSERT INTO categoria (nome, descricao, ativa, icone, usuario_id, criado_em) VALUES ('Alimentação', 'Gastos com alimentação', TRUE, 'food', 1, CURRENT_TIMESTAMP());
INSERT INTO categoria (nome, descricao, ativa, icone, usuario_id, criado_em) VALUES ('Transporte', 'Gastos com transporte', TRUE, 'car', 1, CURRENT_TIMESTAMP());
INSERT INTO categoria (nome, descricao, ativa, icone, usuario_id, criado_em) VALUES ('Moradia', 'Gastos com moradia', TRUE, 'home', 1, CURRENT_TIMESTAMP());
INSERT INTO categoria (nome, descricao, ativa, icone, usuario_id, criado_em) VALUES ('Educação', 'Gastos com educação', TRUE, 'school', 1, CURRENT_TIMESTAMP());
INSERT INTO categoria (nome, descricao, ativa, icone, usuario_id, criado_em) VALUES ('Lazer', 'Gastos com lazer', TRUE, 'party', 1, CURRENT_TIMESTAMP());
INSERT INTO categoria (nome, descricao, ativa, icone, usuario_id, criado_em) VALUES ('Saúde', 'Gastos com saúde', TRUE, 'health', 1, CURRENT_TIMESTAMP());

INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Compra no supermercado', 300.00, 'DESPESA', TIMESTAMP '2024-02-10 14:30:00', 1, 1, CURRENT_TIMESTAMP());
INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Pagamento do aluguel', 1200.00, 'DESPESA', TIMESTAMP '2024-02-05 09:00:00', 1, 3, CURRENT_TIMESTAMP());
INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Salário mensal', 5000.00, 'RECEITA', TIMESTAMP '2024-02-01 08:00:00', 1, 1, CURRENT_TIMESTAMP());
INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Mensalidade da academia', 120.00, 'DESPESA', TIMESTAMP '2024-02-15 16:45:00', 1, 5, CURRENT_TIMESTAMP());
INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Consulta médica', 180.00, 'DESPESA', TIMESTAMP '2024-02-18 10:30:00', 1, 6, CURRENT_TIMESTAMP());
INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Curso online', 89.90, 'DESPESA', TIMESTAMP '2024-02-13 20:15:00', 1, 4, CURRENT_TIMESTAMP());
INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Abastecimento de combustível', 150.00, 'DESPESA', TIMESTAMP '2024-02-11 18:20:00', 1, 2, CURRENT_TIMESTAMP());
INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Freelance', 800.00, 'RECEITA', TIMESTAMP '2024-02-21 17:00:00', 1, 1, CURRENT_TIMESTAMP());
INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Jantar em restaurante', 125.50, 'DESPESA', TIMESTAMP '2024-02-22 20:30:00', 1, 1, CURRENT_TIMESTAMP());
INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Cinema com amigos', 60.00, 'DESPESA', TIMESTAMP '2024-02-24 19:15:00', 1, 5, CURRENT_TIMESTAMP());
INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Uber para o trabalho', 35.80, 'DESPESA', TIMESTAMP '2024-02-20 07:45:00', 1, 2, CURRENT_TIMESTAMP());
INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Livros didáticos', 210.45, 'DESPESA', TIMESTAMP '2024-02-16 14:20:00', 1, 4, CURRENT_TIMESTAMP());
INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Conta de luz', 180.00, 'DESPESA', TIMESTAMP '2024-02-08 13:00:00', 1, 3, CURRENT_TIMESTAMP());
INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Conta de água', 95.30, 'DESPESA', TIMESTAMP '2024-02-08 13:05:00', 1, 3, CURRENT_TIMESTAMP());
INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Internet e TV', 160.00, 'DESPESA', TIMESTAMP '2024-02-09 09:30:00', 1, 3, CURRENT_TIMESTAMP());
INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Medicamentos', 85.70, 'DESPESA', TIMESTAMP '2024-02-18 11:45:00', 1, 6, CURRENT_TIMESTAMP());
INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Bônus de desempenho', 1000.00, 'RECEITA', TIMESTAMP '2024-02-25 15:00:00', 1, 1, CURRENT_TIMESTAMP());
INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Manutenção do carro', 350.00, 'DESPESA', TIMESTAMP '2024-02-14 15:30:00', 1, 2, CURRENT_TIMESTAMP());
INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Presente de aniversário', 120.00, 'DESPESA', TIMESTAMP '2024-02-19 16:00:00', 1, 5, CURRENT_TIMESTAMP());
INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Material de escritório', 75.60, 'DESPESA', TIMESTAMP '2024-02-17 11:20:00', 1, 4, CURRENT_TIMESTAMP());

-- Transações dos últimos 30 dias (01/02/2025 a 01/03/2025)
INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Compra no supermercado', 320.50, 'DESPESA', TIMESTAMP '2025-02-02 14:30:00', 1, 1, CURRENT_TIMESTAMP());

INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Pagamento do aluguel', 1250.00, 'DESPESA', TIMESTAMP '2025-02-05 09:00:00', 1, 3, CURRENT_TIMESTAMP());

INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Salário mensal', 5200.00, 'RECEITA', TIMESTAMP '2025-02-01 08:00:00', 1, 1, CURRENT_TIMESTAMP());

INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Mensalidade da academia', 125.00, 'DESPESA', TIMESTAMP '2025-02-10 16:45:00', 1, 5, CURRENT_TIMESTAMP());

INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Consulta médica', 195.00, 'DESPESA', TIMESTAMP '2025-02-12 10:30:00', 1, 6, CURRENT_TIMESTAMP());

INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Curso online', 99.90, 'DESPESA', TIMESTAMP '2025-02-13 20:15:00', 1, 4, CURRENT_TIMESTAMP());

INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Abastecimento de combustível', 175.80, 'DESPESA', TIMESTAMP '2025-02-16 18:20:00', 1, 2, CURRENT_TIMESTAMP());

INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Freelance', 850.00, 'RECEITA', TIMESTAMP '2025-02-18 17:00:00', 1, 1, CURRENT_TIMESTAMP());

INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Jantar em restaurante', 135.50, 'DESPESA', TIMESTAMP '2025-02-19 20:30:00', 1, 1, CURRENT_TIMESTAMP());

INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Cinema com amigos', 65.00, 'DESPESA', TIMESTAMP '2025-02-22 19:15:00', 1, 5, CURRENT_TIMESTAMP());

INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Uber para o trabalho', 42.30, 'DESPESA', TIMESTAMP '2025-02-20 07:45:00', 1, 2, CURRENT_TIMESTAMP());

INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Livros didáticos', 230.45, 'DESPESA', TIMESTAMP '2025-02-21 14:20:00', 1, 4, CURRENT_TIMESTAMP());

INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Conta de luz', 195.00, 'DESPESA', TIMESTAMP '2025-02-08 13:00:00', 1, 3, CURRENT_TIMESTAMP());

INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Conta de água', 105.30, 'DESPESA', TIMESTAMP '2025-02-08 13:05:00', 1, 3, CURRENT_TIMESTAMP());

INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Internet e TV', 169.99, 'DESPESA', TIMESTAMP '2025-02-09 09:30:00', 1, 3, CURRENT_TIMESTAMP());

INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Medicamentos', 93.70, 'DESPESA', TIMESTAMP '2025-02-24 11:45:00', 1, 6, CURRENT_TIMESTAMP());

INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Bônus de desempenho', 1200.00, 'RECEITA', TIMESTAMP '2025-02-25 15:00:00', 1, 1, CURRENT_TIMESTAMP());

INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Manutenção do carro', 380.00, 'DESPESA', TIMESTAMP '2025-02-26 15:30:00', 1, 2, CURRENT_TIMESTAMP());

INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Presente de aniversário', 150.00, 'DESPESA', TIMESTAMP '2025-02-27 16:00:00', 1, 5, CURRENT_TIMESTAMP());

INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Material de escritório', 85.60, 'DESPESA', TIMESTAMP '2025-02-28 11:20:00', 1, 4, CURRENT_TIMESTAMP());

-- Transações de março (mês atual)
INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Compra no supermercado', 340.75, 'DESPESA', TIMESTAMP '2025-03-01 10:15:00', 1, 1, CURRENT_TIMESTAMP());

INSERT INTO transacoes (descricao, valor, tipo, data, usuario_id, categoria_id, criado_em) VALUES ('Pagamento de assinatura', 29.90, 'DESPESA', TIMESTAMP '2025-03-01 08:30:00', 1, 5, CURRENT_TIMESTAMP());