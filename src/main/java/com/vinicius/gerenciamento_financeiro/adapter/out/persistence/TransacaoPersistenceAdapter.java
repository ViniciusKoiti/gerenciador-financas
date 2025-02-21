package com.vinicius.gerenciamento_financeiro.adapter.out.persistence;

import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.port.out.transacao.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TransacaoPersistenceAdapter implements TransacaoRepository {
    private final JpaTransacaoRepository jpaRepository;
    @Override
    public void salvarTransacao(Transacao transacao) {
        jpaRepository.save(transacao);
    }

    @Override
    public List<Transacao> buscarTodasTransacoes() {
        return jpaRepository.findAll();
    }

    public Optional<Transacao> buscarTransacaoPorId(Long id) {return jpaRepository.findById(id);}
}

