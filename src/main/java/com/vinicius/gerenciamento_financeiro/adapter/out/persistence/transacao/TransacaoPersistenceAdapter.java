package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.categoria.entity.CategoriaJpaEntity;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.TransacaoJpaEntity;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.usuario.entity.UsuarioJpaEntity;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.port.out.transacao.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component("transacaoPersistenceAdapter")
@RequiredArgsConstructor
public class TransacaoPersistenceAdapter implements TransacaoRepository {

    private final JpaTransacaoRepository jpaRepository;
    private final TransacaoJpaMapper transacaoJpaMapper;

    @Override
    @Transactional
    public void salvarTransacao(Transacao transacao) {
        TransacaoJpaEntity jpaEntity = transacao.isNova()
                ? transacaoJpaMapper.toJpaEntity(transacao)
                : atualizarExistente(transacao);

        jpaRepository.save(jpaEntity);
    }

    private TransacaoJpaEntity atualizarExistente(Transacao transacao) {
        TransacaoJpaEntity jpa = jpaRepository.findById(transacao.getId().getValue())
                .orElseThrow(() -> new IllegalStateException(
                        "Transação não encontrada: " + transacao.getId().getValue()
                ));

        transacaoJpaMapper.toDomainEntity(jpa);
        return jpa;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transacao> buscarTodasTransacoesPorUsuario(Long usuarioId) {
        log.debug("Buscando todas as transações para usuário: {}", usuarioId);

        return jpaRepository.findAllByUsuarioIdWithRelations(usuarioId)
                .stream()
                .map(transacaoJpaMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transacao> buscarTransacoesPorUsuarioPaginado(Long usuarioId, Pageable pageable) {
        log.debug("Buscando transações paginadas para usuário: {}, página: {}", usuarioId, pageable.getPageNumber());

        return jpaRepository.findAllByUsuarioIdWithRelations(usuarioId, pageable)
                .map(transacaoJpaMapper::toDomainEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Transacao> buscarTransacaoPorIdEUsuario(Long transacaoId, Long usuarioId) {
        log.debug("Buscando transação {} para usuário: {}", transacaoId, usuarioId);

        return jpaRepository.findByIdAndUsuario_Id(transacaoId, usuarioId)
                .map(transacaoJpaMapper::toDomainEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transacao> buscarTransacoesPorCategoriaIdEUsuario(Long categoriaId, Long usuarioId) {
        log.debug("Buscando transações da categoria {} para usuário: {}", categoriaId, usuarioId);

        return jpaRepository.findAllByCategoriaIdAndUsuarioIdWithRelations(categoriaId, usuarioId)
                .stream()
                .map(transacaoJpaMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transacao> buscarTransacoesPorCategoriaIdEUsuarioPaginado(Long categoriaId, Long usuarioId, Pageable pageable) {
        log.debug("Buscando transações paginadas da categoria {} para usuário: {}", categoriaId, usuarioId);

        return jpaRepository.findAllByCategoria_IdAndUsuario_Id(categoriaId, usuarioId, pageable)
                .map(transacaoJpaMapper::toDomainEntity);
    }

    @Override
    @Deprecated
    @Transactional(readOnly = true)
    public List<Transacao> buscarTodasTransacoes() {
        log.warn("Método deprecated sendo usado: buscarTodasTransacoes()");

        return jpaRepository.findAll()
                .stream()
                .map(transacaoJpaMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Deprecated
    @Transactional(readOnly = true)
    public Optional<Transacao> buscarTransacaoPorId(Long id) {
        log.warn("Método deprecated sendo usado: buscarTransacaoPorId({})", id);

        return jpaRepository.findById(id)
                .map(transacaoJpaMapper::toDomainEntity);
    }

    @Override
    @Deprecated
    @Transactional(readOnly = true)
    public List<Transacao> buscarTransacoesPorCategoriaId(Long id) {
        log.warn("Método deprecated sendo usado: buscarTransacoesPorCategoriaId({})", id);

        return jpaRepository.findAllByCategoria_Id(id)
                .stream()
                .map(transacaoJpaMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    public boolean existeTransacaoParaUsuario(Long transacaoId, Long usuarioId) {
        return jpaRepository.existsByIdAndUsuario_Id(transacaoId, usuarioId);
    }

    public long contarTransacoesPorUsuario(Long usuarioId) {
        return jpaRepository.countByUsuario_Id(usuarioId);
    }

    private UsuarioJpaEntity criarUsuarioJpaMinimo(Long usuarioId) {
        return UsuarioJpaEntity.builder()
                .id(usuarioId)
                .build();
    }

    private CategoriaJpaEntity criarCategoriaJpaMinimo(Long categoriaId) {
        return CategoriaJpaEntity.builder()
                .id(categoriaId)
                .build();
    }
}