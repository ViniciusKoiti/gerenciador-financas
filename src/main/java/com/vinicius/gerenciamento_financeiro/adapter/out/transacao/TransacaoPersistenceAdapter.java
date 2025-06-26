package com.vinicius.gerenciamento_financeiro.adapter.out.transacao;

import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.port.out.transacao.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransacaoPersistenceAdapter implements TransacaoRepository {

    private final JpaTransacaoRepository jpaRepository;

    @Override
    public void salvarTransacao(Transacao transacao) {
        try {
            jpaRepository.save(transacao);
            log.debug("Transação salva com sucesso: ID {}", transacao.getId());
        } catch (Exception e) {
            log.error("Erro ao salvar transação: {}", e.getMessage(), e);
            throw e;
        }
    }


    @Override
    public List<Transacao> buscarTodasTransacoesPorUsuario(Long usuarioId) {
        log.debug("Buscando todas as transações para usuário: {}", usuarioId);
        return jpaRepository.findAllByUsuarioIdWithCategoria(usuarioId);
    }

    @Override
    public Page<Transacao> buscarTransacoesPorUsuarioPaginado(Long usuarioId, Pageable pageable) {
        log.debug("Buscando transações paginadas para usuário: {}, página: {}", usuarioId, pageable.getPageNumber());
        return jpaRepository.findAllByUsuarioIdWithCategoria(usuarioId, pageable);
    }

    @Override
    public Optional<Transacao> buscarTransacaoPorIdEUsuario(Long transacaoId, Long usuarioId) {
        log.debug("Buscando transação {} para usuário: {}", transacaoId, usuarioId);
        return jpaRepository.findByIdAndUsuario_id(transacaoId, usuarioId);
    }

    @Override
    public List<Transacao> buscarTransacoesPorCategoriaIdEUsuario(Long categoriaId, Long usuarioId) {
        log.debug("Buscando transações da categoriaJpaEntity {} para usuário: {}", categoriaId, usuarioId);
        return jpaRepository.findAllByCategoriaIdAndUsuarioIdWithCategoria(categoriaId, usuarioId);
    }

    @Override
    public Page<Transacao> buscarTransacoesPorCategoriaIdEUsuarioPaginado(Long categoriaId, Long usuarioId, Pageable pageable) {
        log.debug("Buscando transações paginadas da categoriaJpaEntity {} para usuário: {}", categoriaId, usuarioId);
        return jpaRepository.findAllByCategoriaIdAndUsuarioIdWithCategoria(categoriaId, usuarioId, pageable);
    }

    // ========== MÉTODOS LEGADOS (DEPRECATED) ==========

    @Override
    @Deprecated
    public List<Transacao> buscarTodasTransacoes() {
        log.warn("Método deprecated sendo usado: buscarTodasTransacoes()");
        return jpaRepository.findAll();
    }

    @Override
    @Deprecated
    public Optional<Transacao> buscarTransacaoPorId(Long id) {
        log.warn("Método deprecated sendo usado: buscarTransacaoPorId({})", id);
        return jpaRepository.findById(id);
    }

    @Override
    @Deprecated
    public List<Transacao> buscarTransacoesPorCategoriaId(Long id) {
        log.warn("Método deprecated sendo usado: buscarTransacoesPorCategoriaId({})", id);
        return jpaRepository.findAllByCategoria_id(id);
    }

    // ========== MÉTODOS UTILITÁRIOS ==========

    /**
     * Verifica se uma transação existe e pertence ao usuário
     */
    public boolean existeTransacaoParaUsuario(Long transacaoId, Long usuarioId) {
        return jpaRepository.existsByIdAndUsuario_id(transacaoId, usuarioId);
    }

    /**
     * Conta o total de transações do usuário
     */
    public long contarTransacoesPorUsuario(Long usuarioId) {
        return jpaRepository.countByUsuario_id(usuarioId);
    }

    /**
     * Conta transações de uma categoriaJpaEntity específica para o usuário
     */
    public long contarTransacoesPorCategoriaEUsuario(Long categoriaId, Long usuarioId) {
        return jpaRepository.countByCategoria_idAndUsuario_id(categoriaId, usuarioId);
    }

    /**
     * Busca as transações mais recentes do usuário (útil para dashboard)
     */
    public Page<Transacao> buscarTransacoesRecentes(Long usuarioId, Pageable pageable) {
        return jpaRepository.findRecentByUsuarioId(usuarioId, pageable);
    }
}