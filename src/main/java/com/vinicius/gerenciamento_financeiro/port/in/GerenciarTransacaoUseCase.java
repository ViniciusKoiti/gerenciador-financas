package com.vinicius.gerenciamento_financeiro.port.in;

import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.domain.model.moeda.MontanteMonetario;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.TransacaoId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;

import java.util.List;

/**
 * Port de entrada para gerenciamento de transações.
 * Define contratos usando APENAS tipos de domínio.
 */
public interface GerenciarTransacaoUseCase {
    
    /**
     * Adiciona uma nova transação
     */
    Transacao adicionarTransacao(Transacao transacao);
    
    /**
     * Obtém todas as transações de um usuário
     */
    List<Transacao> obterTodasTransacoes(UsuarioId usuarioId);
    
    /**
     * Calcula o saldo total de um usuário
     */
    MontanteMonetario calcularSaldo(UsuarioId usuarioId);
    
    /**
     * Atualiza a categoria de uma transação
     */
    void atualizarTransacaoCategoria(CategoriaId categoriaId, TransacaoId transacaoId);
    
    /**
     * Busca transações por categoria
     */
    List<Transacao> buscarTransacoesPorCategoriaId(CategoriaId categoriaId);
}
