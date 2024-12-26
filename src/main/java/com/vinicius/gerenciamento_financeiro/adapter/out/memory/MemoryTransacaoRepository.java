package com.vinicius.gerenciamento_financeiro.adapter.out.memory;

import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.port.out.transacao.TransacaoRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Profile("dev")
public class MemoryTransacaoRepository implements TransacaoRepository {

    private final List<Transacao> transacoes = new ArrayList<>();

    @Override
    public void salvarTransacao(Transacao transacao) {
        transacoes.add(transacao);
    }

    @Override
    public List<Transacao> buscarTodasTransacoes() {
        return new ArrayList<>(transacoes);
    }


}
