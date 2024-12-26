package com.vinicius.gerenciamento_financeiro.port.out.transacao;

import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;

import java.util.List;
public interface TransacaoRepository {
    void salvarTransacao(Transacao transacao);
    List<Transacao> buscarTodasTransacoes();



}