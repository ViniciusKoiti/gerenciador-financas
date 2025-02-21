package com.vinicius.gerenciamento_financeiro.adapter.out.memory;

import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.port.out.transacao.TransacaoRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    @Override
    public Optional<Transacao> buscarTransacaoPorId(Long id) {
        return transacoes.stream().filter(transacao -> {
            return Objects.equals(transacao.getId(), id);
        }).findFirst();
    }


}
