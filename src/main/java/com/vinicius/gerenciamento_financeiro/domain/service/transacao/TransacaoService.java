package com.vinicius.gerenciamento_financeiro.domain.service.transacao;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.TransacaoMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao.TransacaoPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao.TransacaoResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.vinicius.gerenciamento_financeiro.port.in.GerenciarTransacaoUseCase;
import com.vinicius.gerenciamento_financeiro.port.out.transacao.TransacaoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransacaoService implements GerenciarTransacaoUseCase {

    private final TransacaoRepository transacaoRepository;
    private final TransacaoMapper transacaoMapper;

    public TransacaoService(@Qualifier("transacaoPersistenceAdapter") TransacaoRepository transacaoRepository,
                            TransacaoMapper transacaoMapper) {
        this.transacaoRepository = transacaoRepository;
        this.transacaoMapper = transacaoMapper;
    }
    @Override
    public void adicionarTransacao(TransacaoPost transacaoPost) {
        Transacao transacao = transacaoMapper.toEntity(transacaoPost);
        transacaoRepository.salvarTransacao(transacao);
    }

    @Override
    public List<TransacaoResponse> obterTodasTransacoes() {
        return transacaoRepository.buscarTodasTransacoes().stream().map(transacaoMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public BigDecimal calcularSaldo() {
        List<Transacao> transacoes = transacaoRepository.buscarTodasTransacoes();
        BigDecimal saldo = BigDecimal.ZERO;
        for (Transacao transacao : transacoes) {
            if (transacao.getTipo() == Transacao.Tipo.RECEITA) {
                saldo = saldo.add(transacao.getValor());
            } else if (transacao.getTipo() == Transacao.Tipo.DESPESA) {
                saldo = saldo.subtract(transacao.getValor());
            }
        }
        return saldo;
    }
}
