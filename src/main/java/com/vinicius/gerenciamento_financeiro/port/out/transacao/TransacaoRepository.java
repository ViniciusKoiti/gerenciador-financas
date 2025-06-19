package com.vinicius.gerenciamento_financeiro.port.out.transacao;

import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TransacaoRepository {
    void salvarTransacao(Transacao transacao);

    List<Transacao> buscarTodasTransacoesPorUsuario(Long usuarioId);

    Optional<Transacao> buscarTransacaoPorIdEUsuario(Long transacaoId, Long usuarioId);

    List<Transacao> buscarTransacoesPorCategoriaIdEUsuario(Long categoriaId, Long usuarioId);

    Page<Transacao> buscarTransacoesPorUsuarioPaginado(Long usuarioId, Pageable pageable);

    Page<Transacao> buscarTransacoesPorCategoriaIdEUsuarioPaginado(Long categoriaId, Long usuarioId, Pageable pageable);

    // Métodos legados (manter compatibilidade)
    @Deprecated
    List<Transacao> buscarTodasTransacoes();

    @Deprecated
    Optional<Transacao> buscarTransacaoPorId(Long id);

    @Deprecated
    List<Transacao> buscarTransacoesPorCategoriaId(Long id);



}