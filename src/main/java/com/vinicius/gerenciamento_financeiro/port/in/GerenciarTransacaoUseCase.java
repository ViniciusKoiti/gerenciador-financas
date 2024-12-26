package com.vinicius.gerenciamento_financeiro.port.in;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao.TransacaoPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao.TransacaoResponse;

import java.math.BigDecimal;
import java.util.List;

public interface GerenciarTransacaoUseCase {
    void adicionarTransacao(TransacaoPost transacao);
    List<TransacaoResponse> obterTodasTransacoes();

    BigDecimal calcularSaldo();

}
